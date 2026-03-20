package com.example.univapp.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class AddAlumnoUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val onSaveSuccess: Boolean = false,
    val carreras: List<Carrera> = emptyList(),
    val grupos: List<Grupo> = emptyList()
)

class AdminAddAlumnoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow(AddAlumnoUiState())
    val uiState: StateFlow<AddAlumnoUiState> = _uiState.asStateFlow()

    fun saveAlumno(
        matricula: String,
        nombre: String,
        carreraId: String,
        grupoId: String, // ID REAL DEL GRUPO
        genero: String,
        fechaNacimiento: String,
        email: String,
        telefono: String,
        direccion: String
    ) {
        if (matricula.isBlank() || nombre.isBlank() || fechaNacimiento.isBlank()) {
            _uiState.update { it.copy(error = "Completa los campos obligatorios.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 1. Crear cuenta en Auth (Contraseña = Matrícula)
                val context = getApplication<Application>().applicationContext
                val options = FirebaseApp.getInstance().options
                val secondaryApp = try {
                    FirebaseApp.initializeApp(context, options, "secondary")
                } catch (e: Exception) {
                    FirebaseApp.getInstance("secondary")
                }
                val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

                try {
                    secondaryAuth.createUserWithEmailAndPassword(email.trim(), matricula.trim()).await()
                    secondaryAuth.signOut()
                } catch (e: Exception) {
                    if (e.message?.contains("already in use") == false) throw e
                }

                // 2. Calcular edad
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val birthDate = sdf.parse(fechaNacimiento)
                val age = if (birthDate != null) {
                    val today = Calendar.getInstance()
                    val birth = Calendar.getInstance().apply { time = birthDate }
                    var a = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
                    if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) a--
                    a
                } else 0

                // 3. Crear objeto Alumno con el grupoId correcto
                val newAlumno = Alumno(
                    id = matricula.trim(),
                    matricula = matricula.trim(),
                    nombre = nombre.trim(),
                    carreraId = carreraId,
                    grupoId = grupoId, // AQUÍ SE ASIGNA EL ID REAL
                    genero = genero,
                    estatusAcademico = "Activo",
                    correo = email.trim(),
                    edad = age,
                    telefono = telefono.trim(),
                    direccion = direccion.trim()
                )

                db.collection("alumnos").document(matricula.trim()).set(newAlumno).await()
                _uiState.update { it.copy(isLoading = false, onSaveSuccess = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error: ${e.localizedMessage}") }
            }
        }
    }
}
