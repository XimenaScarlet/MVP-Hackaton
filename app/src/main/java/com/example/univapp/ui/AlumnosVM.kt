package com.example.univapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.example.univapp.data.AlumnosRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlumnosVM(
    private val repo: AlumnosRepo = AlumnosRepo()
) : ViewModel() {

    // SharingStarted.WhileSubscribed(5000) es una optimización CLAVE:
    // Mantiene el flujo activo 5 segundos después de que la pantalla se cierra, 
    // por si el usuario vuelve rápido (evita recargar todo).
    val alumnos: StateFlow<List<Alumno>> =
        repo.stream().stateIn(
            scope = viewModelScope, 
            started = SharingStarted.WhileSubscribed(5000), 
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun agregar(nombre: String, matricula: String, correo: String) {
        if (nombre.isBlank() || matricula.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                repo.add(Alumno(nombre = nombre, matricula = matricula, correo = correo))
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun borrar(id: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                repo.delete(id)
            } finally {
                _isLoading.update { false }
            }
        }
    }
}
