package com.example.univapp.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.example.univapp.data.Materia
import com.example.univapp.data.repository.MateriaRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MateriasUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val carreras: List<Carrera> = emptyList(),
    val grupos: List<Grupo> = emptyList(),
    val materias: List<Materia> = emptyList(),
    val selectedCarrera: Carrera? = null,
    val selectedGrupo: Grupo? = null
)

@HiltViewModel
class AdminMateriasViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val repository: MateriaRepository
) : ViewModel() {

    private var listener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(MateriasUiState())
    val uiState = _uiState.asStateFlow()

    init {
        listenForCarreras()
    }

    private fun listenForCarreras() {
        _uiState.update { it.copy(isLoading = true) }
        db.collection("carreras").addSnapshotListener { snapshot, e ->
            if (e != null) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar carreras") }
                return@addSnapshotListener
            }
            val carreras = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Carrera::class.java)?.apply { id = doc.id }
            } ?: emptyList()
            _uiState.update { it.copy(carreras = carreras, isLoading = false) }
        }
    }

    fun onCarreraSelected(carrera: Carrera?) {
        _uiState.update { it.copy(selectedCarrera = carrera, selectedGrupo = null, grupos = emptyList(), materias = emptyList()) }
        if (carrera == null) return

        _uiState.update { it.copy(isLoading = true) }
        db.collection("grupos").whereEqualTo("carreraId", carrera.id)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _uiState.update { it.copy(isLoading = false, error = "Error al cargar grupos") }
                    return@addSnapshotListener
                }
                val grupos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Grupo::class.java)?.apply { id = doc.id }
                } ?: emptyList()
                _uiState.update { it.copy(grupos = grupos, isLoading = false) }
            }
    }

    fun onGrupoSelected(grupo: Grupo?) {
        _uiState.update { it.copy(selectedGrupo = grupo, materias = emptyList()) }
        if (grupo == null) return

        _uiState.update { it.copy(isLoading = true) }
        listener?.remove()
        listener = db.collection("materias").whereEqualTo("grupoId", grupo.id)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _uiState.update { it.copy(isLoading = false, error = "Error al cargar materias") }
                    return@addSnapshotListener
                }
                val materias = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Materia::class.java)?.apply { id = doc.id }
                } ?: emptyList()
                _uiState.update { it.copy(materias = materias, isLoading = false) }
            }
    }

    fun agregarMateria(materia: Materia) {
        viewModelScope.launch {
            repository.saveMateria(materia)
        }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
