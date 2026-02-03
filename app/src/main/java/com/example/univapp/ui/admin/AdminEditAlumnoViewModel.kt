package com.example.univapp.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Alumno
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminEditAlumnoViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _alumno = MutableStateFlow<Alumno?>(null)
    val alumno = _alumno.asStateFlow()

    private val _carreras = MutableStateFlow<List<Carrera>>(emptyList())
    val carreras = _carreras.asStateFlow()

    private val _grupos = MutableStateFlow<List<Grupo>>(emptyList())
    val grupos = _grupos.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun loadAlumno(alumnoId: String) {
        if (alumnoId.isBlank()) {
            _errorMessage.value = "ID de alumno no válido."
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val alumnoDoc = db.collection("alumnos").document(alumnoId).get().await()

                if (!alumnoDoc.exists()) {
                    _errorMessage.value = "Error: No se encontró al alumno con ID: $alumnoId"
                    _isLoading.value = false
                    return@launch
                }

                // Manual deserialization to be more robust against data type errors
                val data = alumnoDoc.data
                if (data == null) {
                    _errorMessage.value = "Error: Los datos del alumno están vacíos."
                    _isLoading.value = false
                    return@launch
                }

                val edadAsObject = data["edad"]
                val edadInt = when (edadAsObject) {
                    is Long -> edadAsObject.toInt()
                    is String -> edadAsObject.toIntOrNull() ?: 0
                    else -> 0
                }

                val loadedAlumno = Alumno(
                    id = alumnoDoc.id,
                    nombre = data["nombre"] as? String ?: "",
                    telefono = data["telefono"] as? String ?: "",
                    genero = data["genero"] as? String ?: "",
                    edad = edadInt,
                    fechaNacimiento = data["fechaNacimiento"] as? String ?: "",
                    carreraId = data["carreraId"] as? String ?: "",
                    grupoId = data["grupoId"] as? String ?: "",
                    estatusAcademico = data["estatusAcademico"] as? String ?: "",
                    fechaIngreso = data["fechaIngreso"] as? String ?: "",
                    nombreContacto = data["nombreContacto"] as? String ?: "",
                    telefonoEmergencia = data["telefonoEmergencia"] as? String ?: "",
                    matricula = data["matricula"] as? String ?: ""
                )

                _alumno.value = loadedAlumno
                val carrerasSnapshot = db.collection("carreras").get().await()
                _carreras.value = carrerasSnapshot.toObjects(Carrera::class.java)
                loadedAlumno.carreraId?.let { loadGruposForCarrera(it) }

            } catch (e: Exception) {
                _errorMessage.value = "Error al procesar los datos del alumno: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadGruposForCarrera(carreraId: String) {
        viewModelScope.launch {
            try {
                val gruposSnapshot = db.collection("grupos").whereEqualTo("carreraId", carreraId).get().await()
                _grupos.value = gruposSnapshot.toObjects(Grupo::class.java)
            } catch (e: Exception) {
                // Don't overwrite a more important error message
            }
        }
    }

    fun onFieldChange(field: String, value: Any) {
        _alumno.value?.let { currentAlumno ->
            val updatedAlumno = when (field) {
                "nombre" -> currentAlumno.copy(nombre = value as String)
                "telefono" -> currentAlumno.copy(telefono = value as String)
                "genero" -> currentAlumno.copy(genero = value as String)
                "edad" -> currentAlumno.copy(edad = (value as String).toIntOrNull() ?: 0)
                "fechaNacimiento" -> currentAlumno.copy(fechaNacimiento = value as String)
                "carreraId" -> {
                    loadGruposForCarrera(value as String)
                    currentAlumno.copy(carreraId = value as String, grupoId = "")
                }
                "grupoId" -> currentAlumno.copy(grupoId = value as String)
                "estatusAcademico" -> currentAlumno.copy(estatusAcademico = value as String)
                "fechaIngreso" -> currentAlumno.copy(fechaIngreso = value as String)
                "nombreContacto" -> currentAlumno.copy(nombreContacto = value as String)
                "telefonoEmergencia" -> currentAlumno.copy(telefonoEmergencia = value as String)
                else -> currentAlumno
            }
            _alumno.value = updatedAlumno
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            if (_alumno.value == null) {
                _errorMessage.value = "No hay datos de alumno para guardar."
                return@launch
            }
            _isSaving.value = true
            _errorMessage.value = null
            try {
                db.collection("alumnos").document(_alumno.value!!.id).set(_alumno.value!!).await()
                _saveSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar los cambios: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
}