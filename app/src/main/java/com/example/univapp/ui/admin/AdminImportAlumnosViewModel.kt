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

    fun importAlumnosFromUri(uri: Uri, inputStream: InputStream?) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading
            
            try {
                val result = withContext(Dispatchers.IO) {
                    val context = getApplication<Application>().applicationContext
                    val tempFile = File(context.cacheDir, "import_alumnos_fast.xlsx")
                    
                    try {
                        inputStream?.use { input ->
                            FileOutputStream(tempFile).use { output ->
                                input.copyTo(output)
                            }
                        }

                        if (!tempFile.exists() || tempFile.length() == 0L) {
                            throw Exception("No se pudo crear el archivo temporal o está vacío.")
                        }

                        val alumnos = leerAlumnosDesdeExcel(tempFile)

                        if (alumnos.isEmpty()) {
                            throw Exception("No se encontraron filas de datos legibles.")
                        }

                        // VERIFICACIÓN DE DUPLICADOS
                        val matriculasEnExcel = alumnos.mapNotNull { it.matricula }.filter { it.isNotBlank() }
                        if (matriculasEnExcel.isNotEmpty()) {
                            val existingAlumnos = Firebase.firestore.collection("alumnos")
                                .whereIn("matricula", matriculasEnExcel)
                                .get()
                                .await()
                            
                            if (!existingAlumnos.isEmpty) {
                                val existingMatricula = existingAlumnos.documents.first().getString("matricula")
                                throw Exception("Error: La matrícula '$existingMatricula' ya existe en la base de datos.")
                            }
                        }

                        val validAlumnos = mutableListOf<Alumno>()
                        val errors = mutableListOf<String>()

                        alumnos.forEachIndexed { index, alumno ->
                            val rowNum = index + 2 
                            val matricula = alumno.matricula?.trim() ?: ""
                            
                            if (matricula.isEmpty()) return@forEachIndexed

                            val safeDocId = matricula.filter { it.isLetterOrDigit() || it == '-' || it == '_' }

                            when {
                                safeDocId.isEmpty() -> errors.add("Fila $rowNum: Matrícula inválida.")
                                alumno.nombre.isNullOrBlank() -> errors.add("Fila $rowNum: Nombre vacío.")
                                alumno.correo.isNullOrBlank() || !Patterns.EMAIL_ADDRESS.matcher(alumno.correo!!).matches() -> 
                                    errors.add("Fila $rowNum: Correo inválido.")
                                else -> {
                                    validAlumnos.add(alumno.copy(id = safeDocId, matricula = matricula))
                                }
                            }
                        }

                        if (validAlumnos.isEmpty()) {
                            throw Exception("No se encontraron registros válidos para importar.\n${errors.firstOrNull() ?: ""}")
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

    private fun leerAlumnosDesdeExcel(file: File): List<Alumno> {
        FileInputStream(file).use { fis ->
            val wb = ReadableWorkbook(fis)
            val sheet = wb.firstSheet ?: return emptyList()
            val alumnos = mutableListOf<Alumno>()

            sheet.openStream().use { rows ->
                rows.asSequence().drop(1).forEach { row -> 
                    val matricula = row.getCellText(0)?.trim()
                    val nombre = row.getCellText(1)?.trim()
                    val correo = row.getCellText(2)?.trim()
                    val carreraId = row.getCellText(3)?.trim()
                    val grupoId = row.getCellText(4)?.trim()

                    if (matricula.isNullOrBlank() && nombre.isNullOrBlank() && correo.isNullOrBlank()) return@forEach

                    alumnos.add(Alumno(matricula = matricula, nombre = nombre, correo = correo, carreraId = carreraId, grupoId = grupoId))
                }
            }
            return alumnos
        }
    }

    private fun org.dhatim.fastexcel.reader.Row.getCellText(index: Int): String? {
        val cell = getCell(index) ?: return null
        return when (cell.type) {
            org.dhatim.fastexcel.reader.CellType.STRING -> cell.asString()
            org.dhatim.fastexcel.reader.CellType.NUMBER -> {
                val n = cell.asNumber().toDouble()
                if (n % 1.0 == 0.0) n.toLong().toString() else n.toString()
            }
            org.dhatim.fastexcel.reader.CellType.BOOLEAN -> cell.asBoolean().toString()
            else -> cell.text
        }
    }

    fun resetState() { _importState.value = ImportState.Idle }
}
