package com.example.univapp.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.example.univapp.data.Profesor
import com.example.univapp.data.repository.GroupsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupDetailUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val group: Grupo? = null,
    val carrera: Carrera? = null,
    val tutor: Profesor? = null,
    val alumnos: List<Alumno> = emptyList(),
    val isEmpty: Boolean = false
)

@HiltViewModel
class AdminGroupDetailViewModel @Inject constructor(
    private val repository: GroupsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    fun load(groupId: String) {
        if (groupId.isBlank()) {
            _uiState.update { it.copy(error = "Identificador de grupo inválido.") }
            return
        }

        pollingJob?.cancel()
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val group = repository.getGroup(groupId) ?: throw Exception("Grupo no encontrado")
                val carrera = group.carreraId?.let { repository.getCarrera(it) }
                val tutor = group.tutorId?.let { repository.getTutor(it) }
                
                // Usamos el nombre del grupo para la búsqueda extendida
                val alumnos = repository.getAlumnosByGroup(groupId, group.nombre)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        group = group,
                        carrera = carrera,
                        tutor = tutor,
                        alumnos = alumnos,
                        isEmpty = alumnos.isEmpty()
                    )
                }

                startPolling(groupId, group.nombre)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar datos.") }
            }
        }
    }

    private fun startPolling(groupId: String, groupName: String?) {
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(5000)
                try {
                    val freshAlumnos = repository.getAlumnosByGroup(groupId, groupName)
                    _uiState.update { 
                        it.copy(
                            alumnos = freshAlumnos, 
                            isEmpty = freshAlumnos.isEmpty()
                        )
                    }
                } catch (e: Exception) {
                    // Silencioso
                }
            }
        }
    }

    fun retry(groupId: String) {
        load(groupId)
    }
}
