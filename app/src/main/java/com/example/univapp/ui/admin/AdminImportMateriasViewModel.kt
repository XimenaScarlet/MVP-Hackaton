package com.example.univapp.ui.admin

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Materia
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

class AdminImportMateriasViewModel(application: Application) : AndroidViewModel(application) {

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState

    private val MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB

    fun importMateriasFromUri(uri: Uri, inputStream: InputStream?) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading
            
            try {
                val result = withContext(Dispatchers.IO) {
                    val stream = inputStream ?: throw Exception("No se pudo abrir el stream del archivo.")
                    
                    val context = getApplication<Application>().applicationContext
                    val tempFile = File(context.cacheDir, "temp_import_mat_${System.currentTimeMillis()}.xlsx")
                    
                    stream.use { input ->
                        tempFile.outputStream().use { output ->
                            val bytes = input.copyTo(output)
                            if (bytes == 0L) {
                                tempFile.delete()
                                throw Exception("El archivo está vacío.")
                            }
                            if (bytes > MAX_FILE_SIZE) {
                                tempFile.delete()
                                throw Exception("El archivo supera el límite de 10MB.")
                            }
                        }
                    }

                    val options = PoijiOptions.PoijiOptionsBuilder.settings().headerStart(0).build()
                    val rawList = try {
                        Poiji.fromExcel(tempFile, Materia::class.java, options)
                    } catch (e: Exception) {
                        throw Exception("Error al leer el Excel.")
                    } finally {
                        tempFile.delete()
                    }

                    if (rawList.isEmpty()) throw Exception("No se encontraron datos.")

                    val validMaterias = mutableListOf<Materia>()
                    val errors = mutableListOf<String>()

                    rawList.forEachIndexed { index, materia ->
                        val rowNum = index + 2
                        if (materia.nombre.isNullOrBlank()) {
                            errors.add("Fila $rowNum: Nombre vacío")
                        } else {
                            val id = if (materia.id.isNullOrBlank()) Firebase.firestore.collection("materias").document().id else materia.id
                            validMaterias.add(materia.copy(id = id))
                        }
                    }

                    if (validMaterias.isEmpty()) {
                        throw Exception("No se encontraron registros válidos.")
                    }

                    val db = Firebase.firestore
                    val chunks = validMaterias.chunked(450)
                    var processedCount = 0

                    chunks.forEach { chunk ->
                        db.runBatch { batch ->
                            chunk.forEach { materia ->
                                val docRef = db.collection("materias").document(materia.id)
                                batch.set(docRef, materia)
                            }
                        }.await()
                        processedCount += chunk.size
                    }

                    ImportResult(processedCount, errors)
                }

                _importState.value = ImportState.Success(result.count, result.rowErrors)

            } catch (t: Throwable) {
                _importState.value = ImportState.Error(t.localizedMessage ?: "Error desconocido.")
            }
        }
    }

    fun resetState() {
        _importState.value = ImportState.Idle
    }
}
