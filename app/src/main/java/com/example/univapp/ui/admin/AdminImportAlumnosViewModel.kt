package com.example.univapp.ui.admin

import android.app.Application
import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.dhatim.fastexcel.reader.ReadableWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.streams.asSequence

class AdminImportAlumnosViewModel(application: Application) : AndroidViewModel(application) {

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState

    private val db = Firebase.firestore
    private val TAG = "IMPORT_ALUMNOS"

    private fun getOrCreateSecondaryAuth(): FirebaseAuth {
        val context = getApplication<Application>().applicationContext
        val options = FirebaseApp.getInstance().options
        val secondaryApp = try {
            FirebaseApp.initializeApp(context, options, "secondary")
        } catch (e: Exception) {
            try {
                FirebaseApp.getInstance("secondary")
            } catch (e2: Exception) {
                FirebaseApp.initializeApp(context, options, "secondary")
            }
        }
        return FirebaseAuth.getInstance(secondaryApp)
    }

    fun importAlumnosFromUri(uri: Uri, inputStream: InputStream?) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading

            try {
                val finalResult = withContext(Dispatchers.IO) {
                    val context = getApplication<Application>().applicationContext
                    val tempFile = File(context.cacheDir, "import_alumnos_secure.xlsx")

                    val secondaryAuth = getOrCreateSecondaryAuth()

                    try {
                        inputStream?.use { input ->
                            FileOutputStream(tempFile).use { output -> input.copyTo(output) }
                        }

                        if (!tempFile.exists() || tempFile.length() == 0L) throw Exception("Archivo vacío o no válido.")

                        val alumnosRaw = leerAlumnosDesdeExcel(tempFile)
                        if (alumnosRaw.isEmpty()) throw Exception("No se encontraron datos en el archivo.")

                        var createdInAuthCount = 0
                        var existingInAuthCount = 0
                        var authFailedCount = 0
                        var skippedCount = 0
                        val rowErrors = mutableListOf<String>()
                        val firestoreBatchList = mutableListOf<Alumno>()

                        // 1. Verificar existentes en Firestore por batch (máximo 10 por whereIn)
                        val matriculasFromExcel = alumnosRaw.mapNotNull { it.matricula?.trim() }.filter { it.isNotBlank() }.distinct()
                        val existingFirestoreMatriculas = mutableSetOf<String>()
                        
                        matriculasFromExcel.chunked(10).forEach { chunk ->
                            try {
                                val querySnapshot = db.collection("alumnos")
                                    .whereIn(FieldPath.documentId(), chunk)
                                    .get().await()
                                querySnapshot.documents.forEach { doc ->
                                    existingFirestoreMatriculas.add(doc.id)
                                }
                            } catch(e: Exception) {
                                Log.e(TAG, "Error verificando duplicados en Firestore: ${e.message}")
                            }
                        }

                        // 2. Procesar cada alumno
                        alumnosRaw.forEachIndexed { index, rawAlumno ->
                            val rowNum = index + 2
                            val matricula = rawAlumno.matricula?.trim() ?: ""
                            val correo = rawAlumno.correo?.trim() ?: ""
                            val nombre = rawAlumno.nombre?.trim() ?: ""
                            val id = matricula

                            if (matricula.isBlank()) return@forEachIndexed

                            // Evitar duplicados en Firestore
                            if (existingFirestoreMatriculas.contains(id)) {
                                rowErrors.add("Fila $rowNum: Matrícula '$matricula' ya existe en Firestore.")
                                skippedCount++
                                return@forEachIndexed
                            }

                            // Validaciones
                            if (nombre.isBlank()) {
                                rowErrors.add("Fila $rowNum: Nombre vacío.")
                                return@forEachIndexed
                            }
                            if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                                rowErrors.add("Fila $rowNum: Correo '$correo' inválido.")
                                return@forEachIndexed
                            }
                            if (matricula.length < 6) {
                                rowErrors.add("Fila $rowNum: Matrícula debe tener >= 6 caracteres (para password)." )
                                return@forEachIndexed
                            }

                            // Crear en Firebase Auth usando cuenta secundaria
                            var authOk = false
                            try {
                                secondaryAuth.createUserWithEmailAndPassword(correo, matricula).await()
                                secondaryAuth.signOut() // Desloguear al alumno de la app secundaria
                                createdInAuthCount++
                                authOk = true
                                Log.d(TAG, "Usuario creado en Auth: $correo")
                            } catch (e: Exception) {
                                if (e is FirebaseAuthUserCollisionException || e.message?.contains("already in use") == true) {
                                    existingInAuthCount++
                                    authOk = true
                                    Log.d(TAG, "Usuario ya existe en Auth: $correo")
                                } else {
                                    authFailedCount++
                                    rowErrors.add("Fila $rowNum: Error Auth ($correo): ${e.message}")
                                    Log.e(TAG, "Fila $rowNum: Error creando usuario en Auth", e)
                                }
                            }

                            if (authOk) {
                                firestoreBatchList.add(rawAlumno.copy(id = id, matricula = matricula, correo = correo))
                            }
                        }

                        // 3. Guardar en Firestore por batch
                        if (firestoreBatchList.isNotEmpty()) {
                            firestoreBatchList.chunked(450).forEach { chunk ->
                                db.runBatch { batch ->
                                    chunk.forEach { alumno ->
                                        val docRef = db.collection("alumnos").document(alumno.id)
                                        batch.set(docRef, alumno, SetOptions.merge())
                                    }
                                }.await()
                            }
                        }

                        // Generar resumen y meterlo como primeras líneas en rowErrors
                        val summary = mutableListOf<String>()
                        summary.add("--- RESUMEN DE IMPORTACIÓN ---")
                        summary.add("• Firestore importados: ${firestoreBatchList.size}")
                        summary.add("• Auth creados: $createdInAuthCount")
                        summary.add("• Auth existentes: $existingInAuthCount")
                        summary.add("• Auth fallidos: $authFailedCount")
                        summary.add("• Omitidos (ya en Firestore): $skippedCount")
                        summary.add("------------------------------------")
                        
                        val finalRowErrors = summary + rowErrors

                        ImportResult(
                            totalProcessed = firestoreBatchList.size,
                            createdInAuth = createdInAuthCount,
                            existingInAuth = existingInAuthCount,
                            rowErrors = finalRowErrors
                        )

                    } finally {
                        if (tempFile.exists()) tempFile.delete()
                    }
                }

                _importState.value = ImportState.Success(finalResult)

            } catch (t: Throwable) {
                Log.e(TAG, "Error crítico en la importación", t)
                _importState.value = ImportState.Error(t.message ?: "Error desconocido.")
            }
        }
    }

    private fun leerAlumnosDesdeExcel(file: File): List<Alumno> {
        return FileInputStream(file).use { fis ->
            val wb = ReadableWorkbook(fis)
            val sheet = wb.firstSheet ?: return emptyList()
            val list = mutableListOf<Alumno>()

            sheet.openStream().use { rows ->
                rows.asSequence().drop(1).forEach { row ->
                    val matricula = row.getCellText(0)?.trim()
                    val nombre = row.getCellText(1)?.trim()
                    val correo = row.getCellText(2)?.trim()
                    val carreraId = row.getCellText(3)?.trim()
                    val grupoId = row.getCellText(4)?.trim()

                    if (!matricula.isNullOrBlank()) {
                        list.add(Alumno(matricula = matricula, nombre = nombre, correo = correo, carreraId = carreraId, grupoId = grupoId))
                    }
                }
            }
            list
        }
    }

    private fun org.dhatim.fastexcel.reader.Row.getCellText(index: Int): String? {
        val cell = getCell(index) ?: return null
        return cell.text?.trim()
    }

    fun resetState() { _importState.value = ImportState.Idle }
}
