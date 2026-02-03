package com.example.univapp.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class EditMateriaUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val materia: Materia? = null,
    val currentCarrera: Carrera? = null,
    val currentGrupo: Grupo? = null,
    val profesores: List<Profesor> = emptyList(),
    val navigateBack: Boolean = false
)

class AdminEditMateriaViewModel(private val materiaId: String) : ViewModel() {

    private val db = Firebase.firestore
    private val _uiState = MutableStateFlow(EditMateriaUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // 1. Cargar la materia específica
                val mSnap = db.collection("materias").document(materiaId).get().await()
                val materia = mSnap.toObject(Materia::class.java)?.apply { id = mSnap.id }
                    ?: throw IllegalStateException("Materia no encontrada.")

                // 2. Cargar SOLO la carrera y grupo actuales (ya que no son editables)
                val carrera = if (materia.carreraId.isNotBlank()) {
                    db.collection("carreras").document(materia.carreraId).get().await().toObject(Carrera::class.java)
                } else null

                val grupo = if (materia.grupoId.isNotBlank()) {
                    db.collection("grupos").document(materia.grupoId).get().await().toObject(Grupo::class.java)
                } else null

                // 3. Cargar la lista de profesores (para el dropdown)
                val profesores = db.collection("profesores").get().await().documents.mapNotNull { 
                    it.toObject(Profesor::class.java)?.apply { id = it.id }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        materia = materia,
                        currentCarrera = carrera,
                        currentGrupo = grupo,
                        profesores = profesores
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateMateria(updatedMateria: Materia) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                db.collection("materias").document(materiaId).set(updatedMateria).await()
                _uiState.update { it.copy(isSaving = false, navigateBack = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun onDoneNavigating() {
        _uiState.update { it.copy(navigateBack = false) }
    }
}

class AdminEditMateriaViewModelFactory(private val materiaId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminEditMateriaViewModel(materiaId) as T
    }
}
