package com.example.univapp.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AddAlumnoUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val onSaveSuccess: Boolean = false,
    val carreras: List<Carrera> = emptyList(),
    val grupos: List<Grupo> = emptyList()
)

class AdminAddAlumnoViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow(AddAlumnoUiState())
    val uiState: StateFlow<AddAlumnoUiState> = _uiState.asStateFlow()

    init {
        fetchCarreras()
    }

    private fun fetchCarreras() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val snapshot = db.collection("carreras").get().await()
                val carreras = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Carrera::class.java)?.apply { id = doc.id }
                }
                _uiState.update { it.copy(carreras = carreras, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar carreras.") }
            }
        }
    }

    fun onCarreraSelected(carreraId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, grupos = emptyList()) }
            try {
                val snapshot = db.collection("grupos").whereEqualTo("carreraId", carreraId).get().await()
                val grupos = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Grupo::class.java)?.apply { id = doc.id }
                }
                _uiState.update { it.copy(grupos = grupos, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar grupos.") }
            }
        }
    }

    fun saveAlumno(
        matricula: String,
        nombre: String,
        carreraId: String,
        grupoId: String,
        genero: String,
        estatus: String
    ) {
        if (matricula.isBlank() || nombre.isBlank()) {
            _uiState.update { it.copy(error = "La matrícula y el nombre son obligatorios.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val newAlumno = Alumno(
                    matricula = matricula,
                    nombre = nombre,
                    carreraId = carreraId,
                    grupoId = grupoId,
                    genero = genero,
                    estatusAcademico = estatus,
                    correo = "$matricula@uts.edu.mx",
                    edad = 0 // Default value
                )

                db.collection("alumnos").document(matricula).set(newAlumno).await()
                _uiState.update { it.copy(isLoading = false, onSaveSuccess = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al guardar el alumno: ${e.message}") }
            }
        }
    }
}