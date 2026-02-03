package com.example.univapp.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.example.univapp.data.Profesor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class GroupDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val group: Grupo? = null,
    val carrera: Carrera? = null,
    val tutor: Profesor? = null,
    val alumnos: List<Alumno> = emptyList()
)

class AdminGroupDetailViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()

    fun load(groupId: String) {
        if (groupId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "ID de grupo no proporcionado.") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // 1) Cargar Grupo
                val gSnap = db.collection("grupos").document(groupId).get().await()
                val grupo = gSnap.toObject(Grupo::class.java)?.apply { id = gSnap.id }
                    ?: throw IllegalStateException("Grupo con ID '$groupId' no encontrado.")

                // 2) Cargar Carrera usando el ID guardado
                val carreraId = grupo.carreraId
                val carrera = if (!carreraId.isNullOrBlank()) {
                    db.collection("carreras").document(carreraId).get().await()
                        .toObject(Carrera::class.java)?.apply { id = carreraId }
                } else null

                // 3) Cargar Tutor usando el ID guardado
                val tutorId = grupo.tutorId
                val tutor = if (!tutorId.isNullOrBlank()) {
                     db.collection("profesores").document(tutorId).get().await()
                        .toObject(Profesor::class.java)?.apply { id = tutorId }
                } else null

                // 4) Cargar Alumnos usando la consulta whereEqualTo (Opción A recomendada)
                val alumnos = fetchAlumnosByGroupId(groupId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        group = grupo,
                        carrera = carrera,
                        tutor = tutor,
                        alumnos = alumnos
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error desconocido al cargar datos.") }
            }
        }
    }

    private suspend fun fetchAlumnosByGroupId(groupId: String): List<Alumno> {
        val snap = db.collection("alumnos")
            .whereEqualTo("grupoId", groupId)
            .get()
            .await()

        return snap.documents.mapNotNull { doc ->
            doc.toObject(Alumno::class.java)?.apply { id = doc.id }
        }
    }

    fun retry(groupId: String) {
        load(groupId)
    }
}
