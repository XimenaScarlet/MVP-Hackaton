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
import java.io.InputStream

class AdminImportAlumnosViewModel(application: Application) : AndroidViewModel(application) {

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState

    private val MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB

    fun importAlumnosFromUri(uri: Uri, inputStream: InputStream?) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading
            
            try {
                val result = withContext(Dispatchers.IO) {
                    val stream = inputStream ?: throw Exception("No se pudo abrir el stream del archivo. Asegúrate de que el archivo esté descargado localmente.")
                    
                    val context = getApplication<Application>().applicationContext
                    val tempFile = File(context.cacheDir, "temp_import_${System.currentTimeMillis()}.xlsx")
                    
                    Log.d("ImportAlumnos", "Copiando stream a archivo temporal...")
                    stream.use { input ->
                        tempFile.outputStream().use { output ->
                            val bytes = input.copyTo(output)
                            Log.d("ImportAlumnos", "Bytes copiados: $bytes")
                            
                            if (bytes == 0L) {
                                tempFile.delete()
                                throw Exception("El archivo está vacío o no se pudo leer el contenido.")
                            }
                            
                            if (bytes > MAX_FILE_SIZE) {
                                tempFile.delete()
                                throw Exception("El archivo supera el límite de 10MB.")
                            }
                        }
                    }

                    Log.d("ImportAlumnos", "Iniciando Poiji...")
                    // Saltamos la fila 0 (encabezados) para evitar basura en la BD
                    val options = PoijiOptions.PoijiOptionsBuilder.settings().headerStart(0).build()
                    val rawList = try {
                        Poiji.fromExcel(tempFile, Alumno::class.java, options)
                    } catch (e: Exception) {
                        Log.e("ImportAlumnos", "Poiji falló al procesar el archivo", e)
                        throw Exception("Error al leer el Excel: El formato no es compatible o el archivo está protegido.")
                    } finally {
                        tempFile.delete()
                    }

                    if (rawList.isEmpty()) throw Exception("No se encontraron datos legibles en el archivo. Asegúrate de usar el orden de columnas: A:Matricula, B:Nombre, C:Correo...")

                    val validAlumnos = mutableListOf<Alumno>()
                    val errors = mutableListOf<String>()

                    rawList.forEachIndexed { index, alumno ->
                        val rowNum = index + 2 
                        val matricula = alumno.matricula?.toString()?.trim() ?: ""
                        
                        // Ignorar si accidentalmente leyó los encabezados como datos
                        if (matricula.equals("Matricula", ignoreCase = true) || matricula.equals("Matrícula", ignoreCase = true)) {
                            return@forEachIndexed
                        }

                        // Sanitizar Matrícula: solo permitir caracteres alfanuméricos y guiones
                        val safeDocId = matricula.filter { it.isLetterOrDigit() || it == '-' || it == '_' }

                        when {
                            safeDocId.isEmpty() -> errors.add("Fila $rowNum: Matrícula vacía o inválida")
                            alumno.nombre.isNullOrBlank() -> errors.add("Fila $rowNum: Nombre vacío")
                            alumno.correo.isNullOrBlank() || !Patterns.EMAIL_ADDRESS.matcher(alumno.correo!!).matches() -> 
                                errors.add("Fila $rowNum: Correo inválido (${alumno.correo ?: "vacío"})")
                            else -> validAlumnos.add(alumno.copy(matricula = safeDocId, id = safeDocId))
                        }
                    }

                    if (validAlumnos.isEmpty()) {
                        throw Exception("No se encontraron registros válidos. Errores: ${errors.take(2).joinToString(", ")}")
                    }

                    Log.d("ImportAlumnos", "Guardando ${validAlumnos.size} alumnos en Firestore...")
                    val db = Firebase.firestore
                    val chunks = validAlumnos.chunked(450)
                    var processedCount = 0

                    chunks.forEach { chunk ->
                        db.runBatch { batch ->
                            chunk.forEach { alumno ->
                                val docRef = db.collection("alumnos").document(alumno.id)
                                batch.set(docRef, alumno)
                            }
                        }.await()
                        processedCount += chunk.size
                    }

                    ImportResult(processedCount, errors)
                }

                _importState.value = ImportState.Success(result.count, result.rowErrors)

            } catch (t: Throwable) {
                Log.e("ImportAlumnos", "Error fatal en importación", t)
                _importState.value = ImportState.Error(t.localizedMessage ?: "Error desconocido al importar.")
            }
        }
    }

    fun resetState() {
        _importState.value = ImportState.Idle
    }
}

data class ImportResult(val count: Int, val rowErrors: List<String>)

sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    data class Success(val count: Int, val errors: List<String> = emptyList()) : ImportState()
    data class Error(val message: String) : ImportState()
}
