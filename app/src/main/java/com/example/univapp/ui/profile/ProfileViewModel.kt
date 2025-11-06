package com.example.univapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PerfilAlumno(
    val id: String = "",
    val nombre: String = "",
    val matricula: String = "",
    val correo: String = "",
    val carrera: String = "",
    val grupoId: String = "",
    val semestre: String = "",          // opcional si lo guardas
    val direccion: String? = null,
    val fechaNacimiento: String? = null,
    val telefono: String? = null
)

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _perfil = MutableStateFlow<PerfilAlumno?>(null)
    val perfil: StateFlow<PerfilAlumno?> = _perfil

    private val _err = MutableStateFlow<String?>(null)
    val err: StateFlow<String?> = _err

    fun load() = viewModelScope.launch {
        _err.value = null
        val u = auth.currentUser ?: run { _err.value = "Sin sesión"; return@launch }
        val email = u.email.orEmpty()
        val mat = email.substringBefore("@")

        db.collection("alumnos").document(mat).get()
            .addOnSuccessListener { d ->
                if (!d.exists()) {
                    _err.value = "No se encontró tu expediente."
                    return@addOnSuccessListener
                }
                _perfil.value = PerfilAlumno(
                    id = d.id,
                    nombre = d.getString("nombre").orEmpty(),
                    matricula = d.getString("matricula").orEmpty().ifEmpty { mat },
                    correo = d.getString("correo").orEmpty().ifEmpty { email },
                    carrera = d.getString("carrera").orEmpty(),
                    grupoId = d.getString("grupoId").orEmpty(),
                    semestre = d.getString("semestre").orEmpty(),
                    direccion = d.getString("direccion"),
                    fechaNacimiento = d.getString("fechaNacimiento"),
                    telefono = d.getString("telefono")
                )
            }
            .addOnFailureListener { _err.value = it.message ?: "Error al cargar perfil" }
    }
}
