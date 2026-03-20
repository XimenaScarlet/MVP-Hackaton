package com.example.univapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Session
import com.example.univapp.location.LocationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SOSViewModel @Inject constructor(
    private val locationHelper: LocationHelper,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val TAG = "SOS_DEBUG"
    private var trackingJob: Job? = null

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private var offlineSession: Session? = null
    fun setOfflineSession(session: Session?) { this.offlineSession = session }

    private var cachedMatricula: String? = null

    /**
     * Resuelve la matrícula y datos del alumno basándose en el correo de la sesión actual.
     */
    private suspend fun resolveAlumnoData(): Pair<String, String>? {
        val email = auth.currentUser?.email ?: offlineSession?.email ?: return null
        return try {
            val query = db.collection("alumnos")
                .whereEqualTo("correo", email)
                .limit(1)
                .get()
                .await()
            
            val doc = query.documents.firstOrNull()
            if (doc != null) {
                val matricula = doc.id
                val nombre = doc.getString("nombre") ?: "Alumno"
                
                // Opcionalmente, vinculamos el UID actual en Firestore si no existe
                val uid = auth.currentUser?.uid
                if (uid != null && doc.getString("authUid") != uid) {
                    db.collection("alumnos").document(matricula).update("authUid", uid)
                }
                
                Pair(matricula, nombre)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resolviendo alumno: ${e.message}")
            null
        }
    }

    fun startTracking() {
        if (trackingJob != null) return
        
        val email = auth.currentUser?.email ?: offlineSession?.email ?: return
        val uid = auth.currentUser?.uid ?: offlineSession?.userId ?: ""
        
        Log.d(TAG, "Iniciando proceso SOS para: $email")
        _isTracking.value = true

        trackingJob = viewModelScope.launch {
            // 1. Resolver Identificador Principal (Matrícula)
            val alumnoData = resolveAlumnoData()
            if (alumnoData == null) {
                Log.e(TAG, "No se pudo encontrar al alumno en la colección 'alumnos' con el correo $email")
                _isTracking.value = false
                return@launch
            }
            
            val (matricula, nombreAlumno) = alumnoData
            cachedMatricula = matricula
            
            Log.d(TAG, "Identidad confirmada -> Matrícula: $matricula, Nombre: $nombreAlumno")

            // 2. Crear/Actualizar alerta SOS usando MATRÍCULA como Document ID
            val initialData = hashMapOf(
                "matricula" to matricula,
                "authUid" to uid,
                "alumnoNombre" to nombreAlumno,
                "email" to email,
                "active" to true,
                "status" to "active",
                "timestamp" to FieldValue.serverTimestamp()
            )

            try {
                db.collection("sos_alerts").document(matricula)
                    .set(initialData, SetOptions.merge())
                    .await()
                Log.d(TAG, "Alerta SOS activada uniformemente para matrícula: $matricula")
            } catch (e: Exception) {
                Log.e(TAG, "ERROR al crear alerta SOS: ${e.message}")
                _isTracking.value = false
                return@launch
            }

            // 3. Loop de actualización de ubicación
            while (_isTracking.value) {
                val latLng = locationHelper.getCurrentLocation()
                if (latLng != null) {
                    val updateData = hashMapOf(
                        "location" to GeoPoint(latLng.latitude, latLng.longitude),
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    try {
                        db.collection("sos_alerts").document(matricula)
                            .set(updateData, SetOptions.merge())
                            .await()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error actualizando ubicación: ${e.message}")
                    }
                }
                delay(5000)
            }
        }
    }

    fun stopTracking() {
        val email = auth.currentUser?.email ?: offlineSession?.email ?: return
        _isTracking.value = false
        trackingJob?.cancel()
        trackingJob = null
        
        viewModelScope.launch {
            try {
                // Usar matrícula cacheada o resolverla si es necesario
                val matricula = cachedMatricula ?: resolveAlumnoData()?.first
                
                if (matricula != null) {
                    db.collection("sos_alerts").document(matricula).update(
                        mapOf(
                            "active" to false,
                            "status" to "ended",
                            "timestamp" to FieldValue.serverTimestamp()
                        )
                    ).await()
                    Log.d(TAG, "SOS finalizado para matrícula: $matricula")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cerrar el SOS: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        trackingJob?.cancel()
    }
}
