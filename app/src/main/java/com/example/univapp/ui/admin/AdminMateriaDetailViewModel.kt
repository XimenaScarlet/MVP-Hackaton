package com.example.univapp.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.example.univapp.data.Materia
import com.example.univapp.data.Profesor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class MateriaDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val materia: Materia? = null,
    val carrera: Carrera? = null,
    val grupo: Grupo? = null,
    val profesor: Profesor? = null,
    val navigateBack: Boolean = false
)

class AdminMateriaDetailViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow(MateriaDetailUiState())
    val uiState: StateFlow<MateriaDetailUiState> = _uiState.asStateFlow()

    fun load(materiaId: String) {
        if (materiaId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "ID de materia no proporcionado.") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // 1) Cargar Materia
                val mSnap = db.collection("materias").document(materiaId).get().await()
                val materia = mSnap.toObject(Materia::class.java)?.apply { id = mSnap.id }
                    ?: throw IllegalStateException("Materia no encontrada.")

                // 2) Cargar Carrera
                val carrera = if (materia.carreraId.isNotBlank()) {
                    db.collection("carreras").document(materia.carreraId).get().await()
                        .toObject(Carrera::class.java)?.apply { id = materia.carreraId }
                } else null

                // 3) Cargar Grupo
                val grupo = if (materia.grupoId.isNotBlank()) {
                    db.collection("grupos").document(materia.grupoId).get().await()
                        .toObject(Grupo::class.java)?.apply { id = materia.grupoId }
                } else null

                // 4) Cargar Profesor
                val profesor = if (!materia.profesorId.isNullOrBlank()) {
                    db.collection("profesores").document(materia.profesorId).get().await()
                        .toObject(Profesor::class.java)?.apply { id = materia.profesorId }
                } else null

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        materia = materia,
                        carrera = carrera,
                        grupo = grupo,
                        profesor = profesor
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteMateria(materiaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                db.collection("materias").document(materiaId).delete().await()
                _uiState.update { it.copy(isLoading = false, navigateBack = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar: ${e.message}") }
            }
        }
    }

    fun onDoneNavigating() {
        _uiState.update { it.copy(navigateBack = false) }
    }
}
