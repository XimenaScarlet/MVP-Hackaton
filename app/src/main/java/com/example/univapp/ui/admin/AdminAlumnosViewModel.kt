package com.example.univapp.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AlumnosUiState(
    val isLoadingCarreras: Boolean = true,
    val isLoadingGrupos: Boolean = false,
    val isLoadingAlumnos: Boolean = false,
    val error: String? = null,
    val carreras: List<Carrera> = emptyList(),
    val grupos: List<Grupo> = emptyList(),
    val selectedCarrera: Carrera? = null,
    val selectedGrupo: Grupo? = null,
    val searchQuery: String = "",
    val alumnos: List<Alumno> = emptyList()
)

class AdminAlumnosViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _uiState = MutableStateFlow(AlumnosUiState())
    val uiState = _uiState.asStateFlow()

    private var allAlumnosForGroup: List<Alumno> = emptyList()

    init {
        fetchCarreras()
    }

    private fun fetchCarreras() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("carreras").get().await()
                val carreras = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Carrera::class.java)?.apply { id = doc.id }
                    } catch (e: Exception) {
                        Log.e("AdminAlumnosVM", "Error deserializing carrera ${doc.id}", e)
                        null
                    }
                }
                _uiState.update { it.copy(carreras = carreras, isLoadingCarreras = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingCarreras = false, error = "Error al cargar carreras.") }
            }
        }
    }

    fun onCarreraSelected(carrera: Carrera?) {
        _uiState.update { it.copy(selectedCarrera = carrera, selectedGrupo = null, grupos = emptyList(), alumnos = emptyList()) }
        if (carrera == null) return

        _uiState.update { it.copy(isLoadingGrupos = true) }
        viewModelScope.launch {
            try {
                val snapshot = db.collection("grupos").whereEqualTo("carreraId", carrera.id).get().await()
                val grupos = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Grupo::class.java)?.apply { id = doc.id }
                    } catch (e: Exception) {
                        Log.e("AdminAlumnosVM", "Error deserializing grupo ${doc.id}", e)
                        null
                    }
                }
                _uiState.update { it.copy(grupos = grupos, isLoadingGrupos = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingGrupos = false, error = "Error al cargar grupos.") }
            }
        }
    }

    fun onGrupoSelected(grupo: Grupo?) {
        _uiState.update { it.copy(selectedGrupo = grupo, alumnos = emptyList()) }
        if (grupo == null) {
            allAlumnosForGroup = emptyList()
            return
        }

        _uiState.update { it.copy(isLoadingAlumnos = true) }
        viewModelScope.launch {
            try {
                val snapshot = db.collection("alumnos").whereEqualTo("grupoId", grupo.id).get().await()
                allAlumnosForGroup = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Alumno::class.java)?.apply { id = doc.id }
                    } catch (e: Exception) {
                        Log.e("AdminAlumnosVM", "Error deserializing alumno ${doc.id}", e)
                        null
                    }
                }
                applyFilters()
                _uiState.update { it.copy(isLoadingAlumnos = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingAlumnos = false, error = "Error al cargar alumnos.") }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery
        val filtered = if (query.isBlank()) {
            allAlumnosForGroup
        } else {
            allAlumnosForGroup.filter {
                it.nombre?.contains(query, true) == true || it.matricula?.contains(query, true) == true
            }
        }
        _uiState.update { it.copy(alumnos = filtered) }
    }
}
