package com.example.univapp.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Materia
import com.example.univapp.data.Profesor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel(private val repo: AdminRepo = AdminRepo()) : ViewModel() {

    private val db = Firebase.firestore

    private val _profesores = MutableStateFlow<List<Profesor>>(emptyList())
    val profesores = _profesores.asStateFlow()

    private val _materias = MutableStateFlow<List<Materia>>(emptyList())
    val materias = _materias.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _navigateBack = MutableStateFlow(false)
    val navigateBack = _navigateBack.asStateFlow()

    fun onDoneNavigating() { _navigateBack.value = false }

    fun getMateriaById(materiaId: String): Flow<Materia?> = flow {
        try {
            val document = db.collection("materias").document(materiaId).get().await()
            val materia = document.toObject(Materia::class.java)?.apply { id = document.id }
            emit(materia)
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun deleteMateria(materiaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                db.collection("materias").document(materiaId).delete().await()
                _navigateBack.value = true
            } catch (e: Exception) {
                _error.value = "Error al eliminar materia: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun refreshAll() {
        fetchProfesores()
        fetchMaterias()
    }

    private fun fetchProfesores() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _profesores.value = repo.getProfesores()
            } catch (e: Exception) {
                _error.value = "Error al cargar profesores: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    private fun fetchMaterias() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _materias.value = repo.getMaterias()
            } catch (e: Exception) {
                _error.value = "Error al cargar materias: ${e.message}"
            }
            _isLoading.value = false
        }
    }
}
