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

data class AddMateriaUiState(
    val profesores: List<Profesor> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaving: Boolean = false,
    val navigateBack: Boolean = false
)

class AddMateriaViewModel(
    private val carreraId: String,
    private val grupoId: String
) : ViewModel() {

    private val db = Firebase.firestore
    private val _uiState = MutableStateFlow(AddMateriaUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfesores()
    }

    private fun loadProfesores() {
        viewModelScope.launch {
            try {
                val profesSnapshot = db.collection("profesores").get().await()
                val profesores = profesSnapshot.documents.mapNotNull { doc ->
                    try {
                        val nombre = doc.getString("nombre")
                        if (nombre.isNullOrBlank()) {
                            Log.w("AddMateriaVM", "Profesor con ID ${doc.id} descartado por nombre nulo o vacío.")
                            return@mapNotNull null
                        }
                        Profesor(
                            id = doc.id,
                            nombre = nombre,
                            apellidos = doc.getString("apellidos") ?: "",
                            telefono = doc.getString("telefono"),
                            correo = doc.getString("correo") ?: "",
                            numeroEmpleado = doc.getString("numeroEmpleado"),
                            activo = doc.getBoolean("activo") ?: true,
                            turno = doc.getString("turno") ?: "",
                            carreraId = doc.getString("carreraId") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("AddMateriaVM", "Error al procesar profesor: ${doc.id}", e)
                        null
                    }
                }
                _uiState.value = _uiState.value.copy(profesores = profesores, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun saveMateria(
        nombre: String,
        profesor: Profesor?,
        periodo: String,
        turno: String,
        creditos: Int,
        clave: String,
        aula: String,
        descripcion: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val newMateria = Materia(
                nombre = nombre,
                profesorId = profesor?.id,
                periodo = periodo,
                turno = turno,
                creditos = creditos,
                clave = clave,
                aula = aula,
                descripcion = descripcion,
                carreraId = carreraId,
                grupoId = grupoId
            )

            try {
                db.collection("materias").add(newMateria).await()
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

class AddMateriaViewModelFactory(
    private val carreraId: String,
    private val grupoId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddMateriaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddMateriaViewModel(carreraId, grupoId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
