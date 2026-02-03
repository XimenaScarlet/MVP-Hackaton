package com.example.univapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class PerfilAlumno(
    val id: String = "",
    val nombre: String = "",
    val matricula: String = "",
    val correo: String = "",
    val carrera: String = "",
    val grupoId: String = "",
    val semestre: String = "",
    val direccion: String? = null,
    val fechaNacimiento: String? = null,
    val telefono: String? = null
)

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _perfil = MutableStateFlow<PerfilAlumno?>(null)
    val perfil: StateFlow<PerfilAlumno?> = _perfil

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _err = MutableStateFlow<String?>(null)
    val err: StateFlow<String?> = _err

    fun load() = viewModelScope.launch {
        _loading.value = true
        _err.value = null
        
        try {
            val user = auth.currentUser ?: throw Exception("Sin sesión activa")
            val email = user.email.orEmpty()
            val matriculaSugerida = email.substringBefore("@")

            // 1. Intentar cargar por matrícula (docId unificado)
            var doc = db.collection("alumnos").document(matriculaSugerida).get().await()

            // 2. Si no existe por ID, buscar por el campo correo
            if (!doc.exists()) {
                val query = db.collection("alumnos")
                    .whereEqualTo("correo", email)
                    .limit(1)
                    .get()
                    .await()
                if (!query.isEmpty) {
                    doc = query.documents.first()
                }
            }

            if (doc.exists()) {
                val alumno = doc.toObject(Alumno::class.java)
                _perfil.value = PerfilAlumno(
                    id = doc.id,
                    nombre = alumno?.nombre ?: "Sin nombre",
                    matricula = alumno?.matricula ?: doc.id,
                    correo = alumno?.correo ?: email,
                    carrera = alumno?.carreraId ?: "No asignada", // Aquí podrías resolver el nombre si tienes tabla carreras
                    grupoId = alumno?.grupoId ?: "",
                    semestre = "Pendiente", // Ajustar según tu lógica de semestre
                    direccion = alumno?.direccion,
                    fechaNacimiento = alumno?.fechaNacimiento,
                    telefono = alumno?.telefono
                )
            } else {
                _err.value = "Expediente no encontrado para: $email"
            }
        } catch (e: Exception) {
            _err.value = e.message ?: "Error al cargar perfil"
        } finally {
            _loading.value = false
        }
    }
}
