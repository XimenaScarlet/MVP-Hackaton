package com.example.univapp.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de administración:
 * - Flujos en vivo desde AdminRepo
 * - Alta completa de alumnos sin perder la sesión del admin
 */
class AdminViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AdminRepo()

    // ----- Flujos de lectura -----
    val carreras   = repo.carrerasFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val alumnos    = repo.alumnosFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val grupos     = repo.gruposFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val materias   = repo.materiasFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val profesores = repo.profesoresFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ----- Estado UI -----
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val handler = CoroutineExceptionHandler { _, e ->
        _loading.value = false
        _error.value = e.message ?: "Error inesperado"
    }

    // Instancias por defecto (sigues autenticado como admin aquí)
    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun refreshAll() = viewModelScope.launch(handler) {
        _loading.value = true
        repo.refreshAll()
        _loading.value = false
    }

    // CRUD que delegan al repo (si ya los usas)
    fun saveAlumno(a: Alumno) = viewModelScope.launch(Dispatchers.IO + handler) { repo.saveAlumno(a) }
    fun deleteAlumno(id: String) = viewModelScope.launch(Dispatchers.IO + handler) { repo.deleteAlumno(id) }

    fun saveGrupo(g: Grupo) = viewModelScope.launch(Dispatchers.IO + handler) { repo.saveGrupo(g) }
    fun deleteGrupo(id: String) = viewModelScope.launch(Dispatchers.IO + handler) { repo.deleteGrupo(id) }

    fun saveMateria(m: Materia) = viewModelScope.launch(Dispatchers.IO + handler) { repo.saveMateria(m) }
    fun deleteMateria(id: String) = viewModelScope.launch(Dispatchers.IO + handler) { repo.deleteMateria(id) }

    fun saveProfesor(p: Profesor) = viewModelScope.launch(Dispatchers.IO + handler) { repo.saveProfesor(p) }
    fun deleteProfesor(id: String) = viewModelScope.launch(Dispatchers.IO + handler) { repo.deleteProfesor(id) }

    fun horariosByGrupo(grupoId: String) = repo.horariosByGrupoFlow(grupoId)
    fun saveHorario(h: Horario) = viewModelScope.launch(Dispatchers.IO + handler) { repo.saveHorario(h) }
    fun deleteHorario(id: String) = viewModelScope.launch(Dispatchers.IO + handler) { repo.deleteHorario(id) }

    // -------------------------------------------------------------------------
    //  ALTA COMPLETA DE ALUMNO (Auth secundario + write en Firestore)
    // -------------------------------------------------------------------------
    fun addAlumnoCompleto(
        nombre: String,
        matricula: String,
        carrera: String,
        direccion: String,
        edad: Int,
        fechaNacimiento: String,
        telefono: String
    ) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            _loading.value = true
            _error.value = null

            val cleanMat = matricula.trim().uppercase()
            val email = if (cleanMat.contains("@")) cleanMat else "${cleanMat.lowercase()}@alumno.utc.edu.mx"
            val password = cleanMat // contraseña temporal

            // ===== 1) Crear usuario en Auth PERO en un FirebaseApp secundario =====
            val app = FirebaseApp.getInstance()                     // app por defecto (admin logueado)
            val secondary: FirebaseApp = try {
                FirebaseApp.getInstance("secondary")
            } catch (_: IllegalStateException) {
                // Reusa las mismas opciones del default
                FirebaseApp.initializeApp(getApplication(), app.options, "secondary")!!
            }
            val secondaryAuth = FirebaseAuth.getInstance(secondary)

            try {
                // Si ya existe, esto lanzará FirebaseAuthUserCollisionException y seguimos
                secondaryAuth.createUserWithEmailAndPassword(email, password).awaitVoid()
                // (Opcional) puedes actualizar displayName con REST o Cloud Functions
            } catch (e: Exception) {
                if (e !is FirebaseAuthUserCollisionException) {
                    _loading.value = false
                    _error.value = e.message ?: "No se pudo crear la cuenta del alumno."
                    return@launch
                }
                // Ya existía el usuario → continuamos
            }

            // ===== 2) Escribir/mezclar documento en 'alumnos' con la sesión admin actual =====
            val docId = cleanMat
            val data = hashMapOf(
                "id"              to docId,
                "nombre"          to nombre.trim(),
                "matricula"       to cleanMat,
                "correo"          to email,
                "carrera"         to carrera.trim(),
                "grupoId"         to "",
                "semestre"        to "",
                "direccion"       to direccion.trim(),
                "edad"            to edad,
                "fechaNacimiento" to fechaNacimiento.trim(),
                "telefono"        to telefono.trim(),
                "createdAt"       to FieldValue.serverTimestamp(),
                "updatedAt"       to FieldValue.serverTimestamp()
            )

            try {
                db.collection("alumnos").document(docId).set(data, SetOptions.merge()).awaitVoid()
                // refresca listas visibles
                repo.refreshAll()
                _loading.value = false
                _error.value = null
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "No se pudo guardar el alumno en la base de datos."
            }
        }
    }
}

/* ----------------------- Helper: Task -> suspend ----------------------- */
private suspend fun com.google.android.gms.tasks.Task<*>.awaitVoid() {
    kotlinx.coroutines.suspendCancellableCoroutine<Unit> { cont ->
        addOnSuccessListener { cont.resume(Unit) {} }
        addOnFailureListener { e -> cont.resumeWith(Result.failure(e)) }
        addOnCanceledListener { cont.cancel() }
    }
}
