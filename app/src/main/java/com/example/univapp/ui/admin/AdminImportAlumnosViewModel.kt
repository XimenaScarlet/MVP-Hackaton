package com.example.univapp.ui.admin

import android.app.Application
import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.poiji.bind.Poiji
import com.poiji.option.PoijiOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AdminImportAlumnosViewModel(application: Application) : AndroidViewModel(application) {

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState

    fun importAlumnosFromUri(uri: Uri, inputStream: InputStream?) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading
            
            try {
                val result = withContext(Dispatchers.IO) {
                    val context = getApplication<Application>().applicationContext
                    val tempFile = File(context.cacheDir, "import_alumnos_v3.xlsx")
                    
                    try {
                        inputStream?.use { input ->
                            FileOutputStream(tempFile).use { output ->
                                input.copyTo(output)
                            }
                        }

                        if (!tempFile.exists() || tempFile.length() == 0L) {
                            throw Exception("No se pudo crear el archivo temporal o está vacío.")
                        }

                        Log.d("ImportExcel", "Archivo copiado a cache: ${tempFile.absolutePath} (${tempFile.length()} bytes)")

                        // MEJORA: Opciones por defecto de Poiji para mayor compatibilidad con Android StAX
                        val options = PoijiOptions.PoijiOptionsBuilder.settings().build()
                        
                        val rawList: List<Alumno> = try {
                            Poiji.fromExcel(tempFile, Alumno::class.java, options)
                        } catch (e: Exception) {
                            Log.e("ImportExcel", "Error de lectura Poiji", e)
                            throw Exception("Error de lectura: ${e.localizedMessage ?: "Formato no válido"}")
                        }

                        if (rawList.isNullOrEmpty()) {
                            throw Exception("No se encontraron filas de datos legibles.")
                        }

                        val validAlumnos = mutableListOf<Alumno>()
                        val errors = mutableListOf<String>()

                        rawList.forEachIndexed { index, alumno ->
                            val rowNum = index + 2 
                            val matricula = alumno.matricula?.toString()?.trim() ?: ""
                            
                            if (matricula.isEmpty() || matricula.equals("Matricula", true) || matricula.equals("Matrícula", true)) return@forEachIndexed

                            val safeDocId = matricula.filter { it.isLetterOrDigit() || it == '-' || it == '_' }

                            when {
                                safeDocId.isEmpty() -> errors.add("Fila $rowNum: Matrícula inválida.")
                                alumno.nombre.isNullOrBlank() -> errors.add("Fila $rowNum: Nombre vacío.")
                                alumno.correo.isNullOrBlank() || !Patterns.EMAIL_ADDRESS.matcher(alumno.correo!!).matches() -> 
                                    errors.add("Fila $rowNum: Correo inválido.")
                                else -> {
                                    validAlumnos.add(alumno.copy(
                                        id = safeDocId,
                                        matricula = safeDocId
                                    ))
                                }
                            }
                        }

                        if (validAlumnos.isEmpty()) {
                            throw Exception("No se encontraron registros válidos para importar.\n${errors.take(1)}")
                        }

                        val db = Firebase.firestore
                        val chunks = validAlumnos.chunked(450)
                        var processedCount = 0

                        chunks.forEach { chunk ->
                            db.runBatch { batch ->
                                chunk.forEach { a ->
                                    val docRef = db.collection("alumnos").document(a.id)
                                    batch.set(docRef, a)
                                }
                            }.await()
                            processedCount += chunk.size
                        }

                        ImportResult(processedCount, errors)

                    } finally {
                        if (tempFile.exists()) tempFile.delete()
                    }
                }

                _importState.value = ImportState.Success(result.count, result.rowErrors)

            } catch (t: Throwable) {
                Log.e("ImportExcel", "Error fatal", t)
                _importState.value = ImportState.Error(t.message ?: "Error desconocido.")
            }
        }
    }

    fun resetState() { _importState.value = ImportState.Idle }
}
