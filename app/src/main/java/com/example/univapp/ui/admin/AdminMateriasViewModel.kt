package com.example.univapp.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.example.univapp.data.Materia
import com.example.univapp.data.Profesor
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MateriasUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val carreras: List<Carrera> = emptyList(),
    val grupos: List<Grupo> = emptyList(),
    val materias: List<Materia> = emptyList(),
    val profesores: List<Profesor> = emptyList(),
    val selectedCarrera: Carrera? = null,
    val selectedGrupo: Grupo? = null
)

class AdminMateriasViewModel : ViewModel() {

    private val db = Firebase.firestore
    private var listener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(MateriasUiState())
    val uiState = _uiState.asStateFlow()

    init {
        listenForCarreras()
    }

    private fun listenForCarreras() {
        _uiState.update { it.copy(isLoading = true) }
        listener?.remove()
        listener = db.collection("carreras").addSnapshotListener { snapshot, e ->
            if (e != null) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar carreras") }
                return@addSnapshotListener
            }
            val carreras = snapshot?.documents?.mapNotNull { doc ->
                 try {
                    doc.toObject(Carrera::class.java)?.apply { id = doc.id }
                } catch (ex: Exception) {
                    Log.e("AdminMateriasVM", "Error deserializing carrera ${doc.id}", ex)
                    null
                }
            } ?: emptyList()
            _uiState.update { it.copy(carreras = carreras, isLoading = false) }
        }
    }

    fun onCarreraSelected(carrera: Carrera?) {
        _uiState.update { it.copy(selectedCarrera = carrera, selectedGrupo = null, grupos = emptyList(), materias = emptyList()) }
        if (carrera == null) return

        _uiState.update { it.copy(isLoading = true) }
        listener?.remove()
        listener = db.collection("grupos").whereEqualTo("carreraId", carrera.id)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _uiState.update { it.copy(isLoading = false, error = "Error al cargar grupos") }
                    return@addSnapshotListener
                }
                val grupos = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Grupo::class.java)?.apply { id = doc.id }
                    } catch (ex: Exception) {
                        Log.e("AdminMateriasVM", "Error deserializing grupo ${doc.id}", ex)
                        null
                    }
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
                    try {
                        doc.toObject(Materia::class.java)?.apply { id = doc.id }
                    } catch (ex: Exception) {
                        Log.e("AdminMateriasVM", "Error deserializing materia ${doc.id}", ex)
                        null
                    }
                } ?: emptyList()
                _uiState.update { it.copy(materias = materias, isLoading = false) }
            }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
