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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AddHorarioUiState(
    val materias: List<Materia> = emptyList(),
    val profesores: List<Profesor> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaving: Boolean = false,
    val navigateBack: Boolean = false
)

class AddHorarioViewModel(
    private val carreraId: String,
    private val grupoId: String
) : ViewModel() {

    private val db = Firebase.firestore
    private val _uiState = MutableStateFlow(AddHorarioUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = AddHorarioUiState(isLoading = true)
            try {
                // Cargar materias de la carrera específica
                val materiasSnapshot = db.collection("materias")
                    .whereEqualTo("carreraId", carreraId)
                    .get().await()

                val materias = materiasSnapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Materia::class.java)?.apply { id = doc.id }
                    } catch (e: Exception) {
                        Log.e("AddHorarioVM", "Error deserializing materia: ${doc.id}", e)
                        null
                    }
                }

                // Cargar todos los profesores
                val profesSnapshot = db.collection("profesores").get().await()
                val profesores = profesSnapshot.documents.mapNotNull { doc ->
                    try {
                        val nombre = doc.getString("nombre")
                        if (nombre.isNullOrBlank()) {
                            Log.w("AddHorarioVM", "Profesor con ID ${doc.id} descartado por nombre nulo o vacío.")
                            return@mapNotNull null
                        }
                        
                        Profesor(
                            id = doc.id,
                            nombre = nombre,
                            apellidos = doc.getString("apellidos") ?: "",
                            correo = doc.getString("correo") ?: "",
                            carreraId = doc.getString("carreraId") ?: "",
                            telefono = doc.getString("telefono"),
                            numeroEmpleado = doc.getString("numeroEmpleado"),
                            activo = doc.getBoolean("activo") ?: true,
                            turno = doc.getString("turno") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("AddHorarioVM", "Error al procesar profesor: ${doc.id}", e)
                        null
                    }
                }

                _uiState.value = AddHorarioUiState(
                    materias = materias,
                    profesores = profesores,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = AddHorarioUiState(isLoading = false, error = e.message)
            }
        }
    }

    fun saveHorario(
        materia: Materia,
        dias: List<String>,
        horaInicio: String,
        horaFin: String,
        salon: String,
        profesor: Profesor?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val newHorario = Horario(
                materiaId = materia.id,
                materiaNombre = materia.nombre,
                dias = dias,
                horaInicio = horaInicio,
                horaFin = horaFin,
                salon = salon,
                profesorId = profesor?.id,
                profesorNombre = profesor?.nombre,
                grupoId = grupoId,
                carreraId = carreraId
            )

            try {
                db.collection("horarios").add(newHorario).await()
                _uiState.value = _uiState.value.copy(isSaving = false, navigateBack = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun onDoneNavigating() {
        _uiState.value = _uiState.value.copy(navigateBack = false)
    }
}

// Factory para el ViewModel
class AddHorarioViewModelFactory(
    private val carreraId: String,
    private val grupoId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddHorarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddHorarioViewModel(carreraId, grupoId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
