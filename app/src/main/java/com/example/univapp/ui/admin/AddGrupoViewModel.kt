package com.example.univapp.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.univapp.data.Grupo
import com.example.univapp.data.Profesor
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddGrupoViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow(AddGrupoUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchProfesores()
    }

    private fun fetchProfesores() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        db.collection("profesores").get()
            .addOnSuccessListener { snapshot ->
                val profesores = snapshot.documents.mapNotNull {
                    it.toObject(Profesor::class.java)?.copy(id = it.id)
                }
                _uiState.value = _uiState.value.copy(profesores = profesores, isLoading = false)
            }
            .addOnFailureListener { e ->
                Log.e("AddGrupoVM", "Error fetching professors", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Error al cargar tutores.")
            }
    }

    fun saveGrupo(nombre: String, tipo: String, tutor: Profesor?, carreraId: String, onComplete: () -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val newGrupo = Grupo(
            nombre = nombre,
            carreraId = carreraId,
            turno = tipo,
            tutorId = tutor?.id
        )

        db.collection("grupos").add(newGrupo)
            .addOnSuccessListener {
                logActivity(ActivityType.CREATE, "Creó el grupo $nombre.")
                _uiState.value = _uiState.value.copy(isLoading = false)
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("AddGrupoVM", "Error saving group", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Error al guardar el grupo.")
            }
    }

    private fun logActivity(type: ActivityType, description: String) {
        val log = hashMapOf(
            "type" to type.name,
            "description" to description,
            "timestamp" to FieldValue.serverTimestamp()
        )
        db.collection("activity_log").add(log)
    }
}

data class AddGrupoUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val profesores: List<Profesor> = emptyList()
)
