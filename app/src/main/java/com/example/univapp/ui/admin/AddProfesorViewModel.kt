package com.example.univapp.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Profesor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AddProfesorUiState(
    val isSaving: Boolean = false,
    val error: String? = null,
    val navigateBack: Boolean = false
)

class AddProfesorViewModel(private val carreraId: String) : ViewModel() {

    private val db = Firebase.firestore
    private val _uiState = MutableStateFlow(AddProfesorUiState())
    val uiState = _uiState.asStateFlow()

    fun saveProfesor(
        nombres: String,
        apellidos: String,
        telefono: String?,
        correo: String,
        numeroEmpleado: String?,
        turno: String,
        activo: Boolean
    ) {
        viewModelScope.launch {
            _uiState.value = AddProfesorUiState(isSaving = true)

            val newProfesor = Profesor(
                nombre = nombres,
                apellidos = apellidos,
                telefono = telefono,
                correo = correo,
                numeroEmpleado = numeroEmpleado,
                turno = turno,
                activo = activo,
                carreraId = carreraId
            )

            try {
                db.collection("profesores").add(newProfesor).await()
                _uiState.value = AddProfesorUiState(isSaving = false, navigateBack = true)
            } catch (e: Exception) {
                _uiState.value = AddProfesorUiState(isSaving = false, error = e.message)
            }
        }
    }

    fun onDoneNavigating() {
        _uiState.value = _uiState.value.copy(navigateBack = false)
    }
}

class AddProfesorViewModelFactory(private val carreraId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddProfesorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddProfesorViewModel(carreraId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
