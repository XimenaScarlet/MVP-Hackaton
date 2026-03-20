package com.example.univapp.ui.admin

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Grupo
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

class AdminImportGruposViewModel(application: Application) : AndroidViewModel(application) {

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState

    fun importGruposFromUri(uri: Uri, inputStream: InputStream?) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading
            
            try {
                val result = withContext(Dispatchers.IO) {
                    val context = getApplication<Application>().applicationContext
                    val tempFile = File(context.cacheDir, "import_grupos_temp.xlsx")
                    
                    try {
                        inputStream?.use { input ->
                            FileOutputStream(tempFile).use { output ->
                                input.copyTo(output)
                            }
                        }

                        if (!tempFile.exists() || tempFile.length() == 0L) {
                            throw Exception("No se pudo crear el archivo temporal o está vacío.")
                        }

                        val grupos = leerGruposDesdeExcel(tempFile)

                        if (grupos.isEmpty()) {
                            throw Exception("No se encontraron filas de datos legibles.")
                        }

                        val validGrupos = mutableListOf<Grupo>()
                        val errors = mutableListOf<String>()

                        grupos.forEachIndexed { index, grupo ->
                            val rowNum = index + 2 
                            val nombre = grupo.nombre?.trim() ?: ""
                            val carreraId = grupo.carreraId?.trim() ?: ""
                            
                            if (nombre.isEmpty()) {
                                errors.add("Fila $rowNum: Nombre de grupo vacío.")
                                return@forEachIndexed
                            }
                            if (carreraId.isEmpty()) {
                                errors.add("Fila $rowNum: Carrera ID vacío.")
                                return@forEachIndexed
                            }

                            val safeId = nombre.filter { it.isLetterOrDigit() || it == '-' || it == '_' }
                            validGrupos.add(grupo.copy(id = safeId))
                        }

                        if (validGrupos.isEmpty()) {
                            throw Exception("No se encontraron registros válidos para importar.")
                        }

                        val db = Firebase.firestore
                        val chunks = validGrupos.chunked(450)
                        var processedCount = 0

                        chunks.forEach { chunk ->
                            db.runBatch { batch ->
                                chunk.forEach { g ->
                                    val docRef = db.collection("grupos").document(g.id)
                                    batch.set(docRef, g)
                                }
                            }.await()
                            processedCount += chunk.size
                        }

                        ImportResult(
                            totalProcessed = processedCount,
                            createdInAuth = 0,
                            existingInAuth = 0,
                            rowErrors = errors
                        )

                    } finally {
                        if (tempFile.exists()) tempFile.delete()
                    }
                }

                _importState.value = ImportState.Success(result)

            } catch (t: Throwable) {
                Log.e("ImportGrupos", "Error fatal", t)
                _importState.value = ImportState.Error(t.message ?: "Error desconocido.")
            }
        }
    }

    private fun leerGruposDesdeExcel(file: File): List<Grupo> {
        FileInputStream(file).use { fis ->
            val wb = ReadableWorkbook(fis)
            val sheet = wb.firstSheet ?: return emptyList()
            val grupos = mutableListOf<Grupo>()

            sheet.openStream().use { rows ->
                rows.asSequence().drop(1).forEach { row -> 
                    val nombre = row.getCellText(0)?.trim()
                    val carreraId = row.getCellText(1)?.trim()
                    val turno = row.getCellText(2)?.trim()
                    val tutorId = row.getCellText(3)?.trim()
                    val programType = row.getCellText(4)?.trim()

                    if (nombre.isNullOrBlank()) return@forEach

                    grupos.add(Grupo(
                        nombre = nombre,
                        carreraId = carreraId,
                        turno = turno,
                        tutorId = tutorId,
                        programType = programType
                    ))
                }
            }
            return grupos
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
