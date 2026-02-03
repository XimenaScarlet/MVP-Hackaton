package com.example.univapp.ui.admin

import android.util.Log
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminGruposUiState(
    val carreras: List<Carrera> = emptyList(),
    val selectedCarrera: Carrera? = null,
    val grupos: List<Grupo> = emptyList(),
    val profesores: List<Profesor> = emptyList(),
    val alumnos: List<Alumno> = emptyList(),
    val isLoading: Boolean = true
)

class AdminGruposViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow(AdminGruposUiState())
    val uiState: StateFlow<AdminGruposUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val carrerasSnapshot = db.collection("carreras").get().await()
                val carreras = carrerasSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Carrera::class.java)?.apply { id = doc.id }
                }

                val profesSnapshot = db.collection("profesores").get().await()
                val profesores = profesSnapshot.documents.mapNotNull { document ->
                    document.toObject(Profesor::class.java)?.apply { id = document.id }
                }

                // Filter students: only those without a grupoId
                val alumnosSnapshot = db.collection("alumnos").get().await()
                val alumnos = alumnosSnapshot.documents.mapNotNull { document ->
                    val alumno = document.toObject(Alumno::class.java)?.apply { id = document.id }
                    if (alumno?.grupoId.isNullOrBlank()) alumno else null
                }

                _uiState.value = _uiState.value.copy(
                    carreras = carreras,
                    profesores = profesores,
                    alumnos = alumnos,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("AdminGruposVM", "Error loading initial data", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onCarreraSelected(carrera: Carrera?) {
        _uiState.value = _uiState.value.copy(selectedCarrera = carrera)
        if (carrera != null) {
            loadGrupos(carrera.id)
        } else {
            _uiState.value = _uiState.value.copy(grupos = emptyList())
        }
    }

    private fun loadGrupos(carreraId: String) {
        viewModelScope.launch {
            db.collection("grupos").whereEqualTo("carreraId", carreraId).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val grupos = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Grupo::class.java)?.apply { id = document.id }
                } ?: emptyList()
                _uiState.value = _uiState.value.copy(grupos = grupos)
            }
        }
    }

    fun createGroup(
        groupName: String,
        tutorId: String,
        studentIds: List<String>,
        carreraId: String = ""
    ) {
        viewModelScope.launch {
            val newGroupRef = db.collection("grupos").document()
            val newGroup = Grupo(
                id = newGroupRef.id,
                nombre = groupName,
                tutorId = tutorId,
                numAlumnos = studentIds.size,
                carreraId = carreraId
            )

            db.runBatch { batch ->
                batch.set(newGroupRef, newGroup)
                studentIds.forEach {
                    val studentRef = db.collection("alumnos").document(it)
                    batch.update(studentRef, mapOf("grupoId" to newGroup.id, "carreraId" to carreraId))
                }
            }.await()
            
            // Refresh student list to remove assigned students
            loadInitialData()
        }
    }
}
