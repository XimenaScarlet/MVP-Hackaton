package com.example.univapp.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.example.univapp.data.Horario
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminHorariosViewModel : ViewModel() {

    private val db = Firebase.firestore

    private var carrerasListener: ListenerRegistration? = null
    private var gruposListener: ListenerRegistration? = null
    private var horariosListener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(HorariosUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        listenForCarreras()
    }

    private fun listenForCarreras() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        carrerasListener?.remove()

        carrerasListener = db.collection("carreras")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    handleError(e, "carreras")
                    return@addSnapshotListener
                }

                val carreras = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Carrera::class.java)?.apply { id = doc.id }
                } ?: emptyList()

                _uiState.value = _uiState.value.copy(
                    carreras = carreras,
                    isLoading = false
                )
            }
    }

    fun onCarreraSelected(carrera: Carrera?) {
        // Limpia listeners y estado dependiente
        gruposListener?.remove()
        horariosListener?.remove()

        if (carrera == null) {
            _uiState.value = _uiState.value.copy(
                selectedCarrera = null,
                selectedGrupo = null,
                grupos = emptyList(),
                horarios = emptyList(),
                error = null
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            selectedCarrera = carrera,
            selectedGrupo = null,
            grupos = emptyList(),
            horarios = emptyList(),
            error = null
        )

        listenForGrupos(carrera.id)
    }

    private fun listenForGrupos(carreraId: String) {
        if (carreraId.isBlank()) return

        _uiState.value = _uiState.value.copy(isLoading = true, grupos = emptyList(), error = null)
        gruposListener?.remove()

        // ✅ ARREGLO: un solo listener directo a "grupos" filtrando por carreraId
        gruposListener = db.collection("grupos")
            .whereEqualTo("carreraId", carreraId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    handleError(e, "grupos")
                    return@addSnapshotListener
                }

                val grupos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Grupo::class.java)?.apply { id = doc.id }
                } ?: emptyList()

                _uiState.value = _uiState.value.copy(
                    grupos = grupos,
                    isLoading = false
                )
            }
    }

    fun onGrupoSelected(grupo: Grupo?) {
        horariosListener?.remove()

        if (grupo == null) {
            _uiState.value = _uiState.value.copy(
                selectedGrupo = null,
                horarios = emptyList(),
                error = null
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            selectedGrupo = grupo,
            horarios = emptyList(),
            error = null
        )

        listenForHorarios(grupo.id)
    }

    private fun listenForHorarios(grupoId: String) {
        if (grupoId.isBlank()) return

        _uiState.value = _uiState.value.copy(isLoading = true, horarios = emptyList(), error = null)
        horariosListener?.remove()

        horariosListener = db.collection("horarios")
            .whereEqualTo("grupoId", grupoId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    handleError(e, "horarios")
                    return@addSnapshotListener
                }

                val horarios = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Horario::class.java)?.apply { id = doc.id }
                } ?: emptyList()

                _uiState.value = _uiState.value.copy(
                    horarios = horarios,
                    isLoading = false
                )
            }
    }

    fun onBack() {
        val s = _uiState.value

        when {
            s.selectedGrupo != null -> {
                // Regresa a lista de grupos
                horariosListener?.remove()
                _uiState.value = s.copy(selectedGrupo = null, horarios = emptyList(), error = null)
            }
            s.selectedCarrera != null -> {
                // Regresa a lista de carreras
                gruposListener?.remove()
                horariosListener?.remove()
                _uiState.value = s.copy(
                    selectedCarrera = null,
                    selectedGrupo = null,
                    grupos = emptyList(),
                    horarios = emptyList(),
                    error = null
                )
            }
        }
    }

    private fun handleError(e: Exception, context: String) {
        Log.e("AdminHorariosVM", "Error en $context", e)
        _uiState.value = _uiState.value.copy(
            error = "Error en $context: ${e.message}",
            isLoading = false
        )
    }

    override fun onCleared() {
        carrerasListener?.remove()
        gruposListener?.remove()
        horariosListener?.remove()
        super.onCleared()
    }
}

data class HorariosUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val carreras: List<Carrera> = emptyList(),
    val grupos: List<Grupo> = emptyList(),
    val horarios: List<Horario> = emptyList(),
    val selectedCarrera: Carrera? = null,
    val selectedGrupo: Grupo? = null
)
