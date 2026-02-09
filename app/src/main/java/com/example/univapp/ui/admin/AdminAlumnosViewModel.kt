package com.example.univapp.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
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
    
    private var alumnosListener: ListenerRegistration? = null
    private var gruposListener: ListenerRegistration? = null

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
                    } catch (e: Exception) { null }
                }
                _uiState.update { it.copy(carreras = carreras, isLoadingCarreras = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingCarreras = false, error = "Error al cargar carreras.") }
            }
        }
    }

    fun onCarreraSelected(carrera: Carrera?) {
        gruposListener?.remove()
        _uiState.update { it.copy(selectedCarrera = carrera, selectedGrupo = null, grupos = emptyList(), alumnos = emptyList()) }
        if (carrera == null) return

        _uiState.update { it.copy(isLoadingGrupos = true) }
        
        gruposListener = db.collection("grupos")
            .whereEqualTo("carreraId", carrera.id)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _uiState.update { it.copy(isLoadingGrupos = false) }
                    return@addSnapshotListener
                }
                val grupos = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Grupo::class.java)?.apply { id = doc.id }
                    } catch (ex: Exception) { null }
                } ?: emptyList()
                _uiState.update { it.copy(grupos = grupos, isLoadingGrupos = false) }
            }
    }

    fun onGrupoSelected(grupo: Grupo?) {
        alumnosListener?.remove()
        _uiState.update { it.copy(selectedGrupo = grupo, alumnos = emptyList()) }
        if (grupo == null) {
            allAlumnosForGroup = emptyList()
            return
        }

        _uiState.update { it.copy(isLoadingAlumnos = true) }
        
        // MEJORA: Buscamos por ID de documento O por el Nombre del grupo (ej. "9IDGSA")
        // Esto soluciona el problema de que no se vean los alumnos importados por Excel
        val groupName = grupo.nombre ?: ""
        
        alumnosListener = db.collection("alumnos")
            .where(
                Filter.or(
                    Filter.equalTo("grupoId", grupo.id),
                    Filter.equalTo("grupoId", groupName)
                )
            )
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _uiState.update { it.copy(isLoadingAlumnos = false, error = "Error en conexión real.") }
                    return@addSnapshotListener
                }
                
                allAlumnosForGroup = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Alumno::class.java)?.apply { id = doc.id }
                    } catch (ex: Exception) { null }
                } ?: emptyList()
                
                applyFilters()
                _uiState.update { it.copy(isLoadingAlumnos = false) }
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

    override fun onCleared() {
        super.onCleared()
        alumnosListener?.remove()
        gruposListener?.remove()
    }
}
