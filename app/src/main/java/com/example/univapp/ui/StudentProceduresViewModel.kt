package com.example.univapp.ui

import android.app.Application
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.example.univapp.data.LocalStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class StudentProceduresViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val localStore = LocalStore(application)

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _studentData = MutableStateFlow<Alumno?>(null)
    val studentData = _studentData.asStateFlow()

    private val _digitalDocuments = MutableStateFlow<List<DocumentRecord>>(emptyList())
    val digitalDocuments = _digitalDocuments.asStateFlow()

    private val _allRequests = MutableStateFlow<List<RequestRecord>>(emptyList())
    val allRequests = _allRequests.asStateFlow()

    data class DocumentRecord(
        val id: String = "",
        val title: String = "",
        val date: String = "",
        val folio: String = "",
        val tipo: String = "",
        val fileName: String = "",
        val createdAtMillis: Long = 0L
    )

    data class RequestRecord(
        val id: String = "",
        val title: String = "",
        val folio: String = "",
        val date: String = "",
        val status: String = "",
        val tipo: String = "",
        val entrega: String = "",
        val createdAtMillis: Long = 0L
    )

    init {
        loadFromLocal()
        loadStudentData()
        loadDigitalDocuments()
        loadAllRequests()
    }

    private fun loadFromLocal() {
        val localReqs = localStore.getRequests()
        val localDocs = localStore.getDocuments()
        _allRequests.value = localReqs.sortedByDescending { it.createdAtMillis }
        _digitalDocuments.value = localDocs.sortedByDescending { it.createdAtMillis }
    }

    private fun loadStudentData() {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                var doc = db.collection("alumnos").document(user.uid).get().await()
                if (!doc.exists()) {
                    val query = db.collection("alumnos")
                        .whereEqualTo("correo", user.email)
                        .limit(1)
                        .get()
                        .await()
                    if (!query.isEmpty) {
                        doc = query.documents.first()
                    }
                }
                if (doc.exists()) {
                    // Se corrige deprecación de toObject
                    _studentData.value = doc.toObject(Alumno::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadDigitalDocuments() {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("tramites")
                    .whereEqualTo("userId", user.uid)
                    .whereEqualTo("entrega", "Digital")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val remoteList = snapshot.documents.map { doc ->
                    val timestamp = doc.getTimestamp("createdAt")
                    val millis = timestamp?.toDate()?.time ?: 0L
                    val dateStr = if (timestamp != null) {
                        SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")).format(timestamp.toDate())
                    } else "Reciente"
                    
                    DocumentRecord(
                        id = doc.id,
                        title = doc.getString("titulo") ?: "Documento",
                        date = "Emitido: $dateStr",
                        folio = doc.getString("folio") ?: "",
                        tipo = doc.getString("tipo") ?: "",
                        fileName = doc.getString("fileName") ?: "",
                        createdAtMillis = millis
                    )
                }
                
                val currentLocal = _digitalDocuments.value
                val merged = (remoteList + currentLocal)
                    .distinctBy { it.folio.ifBlank { it.id } }
                    .sortedByDescending { it.createdAtMillis }
                
                _digitalDocuments.value = merged
                localStore.saveDocuments(merged)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadAllRequests() {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("tramites")
                    .whereEqualTo("userId", user.uid)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val remoteList = snapshot.documents.map { doc ->
                    val timestamp = doc.getTimestamp("createdAt")
                    val millis = timestamp?.toDate()?.time ?: 0L
                    val dateStr = if (timestamp != null) {
                        SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")).format(timestamp.toDate())
                    } else "Reciente"
                    
                    RequestRecord(
                        id = doc.id,
                        title = doc.getString("titulo") ?: "Trámite",
                        folio = doc.getString("folio") ?: "",
                        date = dateStr,
                        status = doc.getString("status") ?: "PENDIENTE",
                        tipo = doc.getString("tipo") ?: "",
                        entrega = doc.getString("entrega") ?: "",
                        createdAtMillis = millis
                    )
                }
                
                val currentLocal = _allRequests.value
                val merged = (remoteList + currentLocal)
                    .distinctBy { it.folio.ifBlank { it.id } }
                    .sortedByDescending { it.createdAtMillis }
                
                _allRequests.value = merged
                localStore.saveRequests(merged)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun requestCertificate(
        tipoSimple: Boolean,
        uso: String,
        isDigital: Boolean,
        onSuccess: (Boolean) -> Unit
    ) {
        val user = auth.currentUser ?: return
        val student = _studentData.value
        val now = System.currentTimeMillis()
        
        viewModelScope.launch {
            _loading.value = true
            try {
                val folio = "ESC-${Calendar.getInstance().get(Calendar.YEAR)}-${(1000..9999).random()}"
                val title = "Constancia de Estudios"
                val fileName = "Constancia_${folio}.pdf"
                
                val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")).format(Date(now))
                val newRequest = RequestRecord(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    folio = folio,
                    date = dateFormatted,
                    status = if (isDigital) "COMPLETADO" else "PENDIENTE",
                    tipo = if (tipoSimple) "Simple" else "Con calificaciones",
                    entrega = if (isDigital) "Digital" else "Ventanilla",
                    createdAtMillis = now
                )
                
                val updatedReqs = (listOf(newRequest) + _allRequests.value).distinctBy { it.folio.ifBlank { it.id } }
                _allRequests.value = updatedReqs
                localStore.saveRequests(updatedReqs)

                if (isDigital) {
                    val saved = generateAndSavePdfLocally(student, folio, tipoSimple, title, fileName)
                    if (saved) {
                        val newDoc = DocumentRecord(
                            id = newRequest.id,
                            title = title,
                            date = "Emitido: $dateFormatted",
                            folio = folio,
                            tipo = newRequest.tipo,
                            fileName = fileName,
                            createdAtMillis = now
                        )
                        val updatedDocs = (listOf(newDoc) + _digitalDocuments.value).distinctBy { it.folio.ifBlank { it.id } }
                        _digitalDocuments.value = updatedDocs
                        localStore.saveDocuments(updatedDocs)
                    }
                }

                val requestMap = hashMapOf(
                    "userId" to user.uid,
                    "alumnoNombre" to (student?.nombre ?: user.email ?: "Estudiante"),
                    "titulo" to title,
                    "tipo" to (if (tipoSimple) "Simple" else "Con calificaciones"),
                    "uso" to uso,
                    "entrega" to (if (isDigital) "Digital" else "Ventanilla"),
                    "folio" to folio,
                    "fileName" to fileName,
                    "status" to (if (isDigital) "COMPLETADO" else "PENDIENTE"),
                    "areaDestino" to "escolares",
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
                db.collection("tramites").add(requestMap).await()
                loadAllRequests()

                _loading.value = false
                onSuccess(isDigital)
            } catch (e: Exception) {
                _loading.value = false
                onSuccess(isDigital)
            }
        }
    }

    fun requestIDReplacement(reason: String, description: String, date: String, onSuccess: () -> Unit) {
        val user = auth.currentUser ?: return
        val student = _studentData.value
        val now = System.currentTimeMillis()
        
        viewModelScope.launch {
            _loading.value = true
            try {
                val folio = "REP-${Calendar.getInstance().get(Calendar.YEAR)}-${(1000..9999).random()}"
                val title = "Reposición de Credencial"
                
                val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")).format(Date(now))
                val newRequest = RequestRecord(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    folio = folio,
                    date = dateFormatted,
                    status = "PENDIENTE",
                    tipo = reason,
                    entrega = "Ventanilla",
                    createdAtMillis = now
                )
                
                val updatedReqs = (listOf(newRequest) + _allRequests.value).distinctBy { it.folio.ifBlank { it.id } }
                _allRequests.value = updatedReqs
                localStore.saveRequests(updatedReqs)

                val requestMap = hashMapOf(
                    "userId" to user.uid,
                    "alumnoNombre" to (student?.nombre ?: user.email ?: "Estudiante"),
                    "titulo" to title,
                    "motivo" to reason,
                    "descripcion" to description,
                    "fechaCita" to date,
                    "entrega" to "Ventanilla",
                    "folio" to folio,
                    "status" to "PENDIENTE",
                    "areaDestino" to "escolares",
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
                db.collection("tramites").add(requestMap).await()
                loadAllRequests()

                _loading.value = false
                onSuccess()
            } catch (e: Exception) {
                _loading.value = false
                onSuccess()
            }
        }
    }

    fun requestInternshipCertificate(company: String, city: String, directedTo: String, onSuccess: () -> Unit) {
        val user = auth.currentUser ?: return
        val student = _studentData.value
        val now = System.currentTimeMillis()
        
        viewModelScope.launch {
            _loading.value = true
            try {
                val folio = "PRA-${Calendar.getInstance().get(Calendar.YEAR)}-${(1000..9999).random()}"
                val title = "Constancia para Prácticas"
                
                val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")).format(Date(now))
                val newRequest = RequestRecord(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    folio = folio,
                    date = dateFormatted,
                    status = "PENDIENTE",
                    tipo = company,
                    entrega = "Ventanilla",
                    createdAtMillis = now
                )
                
                val updatedReqs = (listOf(newRequest) + _allRequests.value).distinctBy { it.folio.ifBlank { it.id } }
                _allRequests.value = updatedReqs
                localStore.saveRequests(updatedReqs)

                val requestMap = hashMapOf(
                    "userId" to user.uid,
                    "alumnoNombre" to (student?.nombre ?: user.email ?: "Estudiante"),
                    "titulo" to title,
                    "empresa" to company,
                    "ciudad" to city,
                    "dirigidoA" to directedTo,
                    "entrega" to "Ventanilla",
                    "folio" to folio,
                    "status" to "PENDIENTE",
                    "areaDestino" to "escolares",
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
                db.collection("tramites").add(requestMap).await()
                loadAllRequests()

                _loading.value = false
                onSuccess()
            } catch (e: Exception) {
                _loading.value = false
                onSuccess()
            }
        }
    }

    fun requestBajaTemporal(motivo: String, fecha: String, onSuccess: () -> Unit) {
        val user = auth.currentUser ?: return
        val student = _studentData.value
        val now = System.currentTimeMillis()
        
        viewModelScope.launch {
            _loading.value = true
            try {
                val folio = "BAJ-${Calendar.getInstance().get(Calendar.YEAR)}-${(1000..9999).random()}"
                val title = "Baja Temporal"
                
                val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")).format(Date(now))
                val newRequest = RequestRecord(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    folio = folio,
                    date = dateFormatted,
                    status = "PENDIENTE",
                    tipo = motivo,
                    entrega = "Ventanilla",
                    createdAtMillis = now
                )
                
                val updatedReqs = (listOf(newRequest) + _allRequests.value).distinctBy { it.folio.ifBlank { it.id } }
                _allRequests.value = updatedReqs
                localStore.saveRequests(updatedReqs)

                val requestMap = hashMapOf(
                    "userId" to user.uid,
                    "alumnoNombre" to (student?.nombre ?: user.email ?: "Estudiante"),
                    "titulo" to title,
                    "motivo" to motivo,
                    "fechaCita" to fecha,
                    "entrega" to "Ventanilla",
                    "folio" to folio,
                    "status" to "PENDIENTE",
                    "areaDestino" to "escolares",
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
                db.collection("tramites").add(requestMap).await()
                loadAllRequests()

                _loading.value = false
                onSuccess()
            } catch (e: Exception) {
                _loading.value = false
                onSuccess()
            }
        }
    }

    private fun generateAndSavePdfLocally(student: Alumno?, folio: String, simple: Boolean, title: String, fileName: String): Boolean {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        val studentName = student?.nombre ?: "ESTUDIANTE"
        val matricula = student?.matricula ?: "N/A"
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        paint.color = Color.BLACK
        paint.textSize = 22f
        paint.isFakeBoldText = true
        canvas.drawText("UNIVERSIDAD TECNOLÓGICA DE COAHUILA", 80f, 80f, paint)
        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("DEPARTAMENTO DE SERVICIOS ESCOLARES", 160f, 110f, paint)
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText(title.uppercase(), 180f, 180f, paint)
        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("A QUIEN CORRESPONDA:", 50f, 250f, paint)
        canvas.drawText("Por medio de la presente se hace constar que el C. $studentName", 50f, 280f, paint)
        canvas.drawText("con matrícula $matricula, se encuentra actualmente inscrito en esta institución", 50f, 300f, paint)
        canvas.drawText("cursando el periodo escolar vigente con estatus de ACTIVO.", 50f, 320f, paint)
        if (!simple) {
            paint.isFakeBoldText = true
            canvas.drawText("PROMEDIO GENERAL: 9.2", 50f, 360f, paint)
            paint.isFakeBoldText = false
        }
        canvas.drawText("Para los fines que al interesado convengan, se extiende la presente", 50f, 420f, paint)
        canvas.drawText("el día $date en la ciudad de Ramos Arizpe, Coahuila.", 50f, 440f, paint)
        paint.textSize = 10f
        paint.color = Color.DKGRAY
        canvas.drawText("Folio de Validación: $folio", 50f, 750f, paint)
        canvas.drawText("Este documento cuenta con validez oficial digital.", 50f, 770f, paint)
        pdfDocument.finishPage(page)
        return try {
            val docDir = getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(docDir, fileName)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            false
        }
    }

    fun openDocument(record: DocumentRecord) {
        val context = getApplication<Application>()
        val docDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(docDir, record.fileName)

        if (file.exists()) {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "No hay aplicación para abrir PDFs", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Re-generando documento...", Toast.LENGTH_SHORT).show()
            viewModelScope.launch {
                val saved = generateAndSavePdfLocally(_studentData.value, record.folio, record.tipo == "Simple", record.title, record.fileName)
                if (saved) openDocument(record)
            }
        }
    }
}
