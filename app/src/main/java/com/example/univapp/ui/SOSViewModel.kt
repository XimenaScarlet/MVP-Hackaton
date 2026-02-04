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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SOSViewModel(private val locationHelper: LocationHelper) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var trackingJob: Job? = null

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private var offlineSession: Session? = null
    fun setOfflineSession(session: Session?) { this.offlineSession = session }

    fun startTracking() {
        if (trackingJob != null) return
        _isTracking.value = true

        viewModelScope.launch {
            try {
                val user = auth.currentUser
                val email = (user?.email ?: offlineSession?.email ?: "").lowercase().trim()
                val matricula = email.substringBefore("@")
                val uid = user?.uid ?: offlineSession?.userId ?: matricula

                var nombreAlumno = "Alumno"
                var matriculaReal = matricula

                // 1. Obtener datos del alumno
                if (matricula.isNotEmpty()) {
                    try {
                        val doc = db.collection("alumnos").document(matricula).get().await()
                        if (doc.exists()) {
                            nombreAlumno = doc.getString("nombre") ?: nombreAlumno
                            matriculaReal = doc.getString("matricula") ?: matricula
                        }
                    } catch (e: Exception) {
                        Log.e("SOS_DEBUG", "Error buscando datos", e)
                    }
                }

                // 2. Crear registro inicial
                val initialData = hashMapOf(
                    "alumnoId" to uid,
                    "matricula" to matriculaReal,
                    "nombre" to nombreAlumno,
                    "email" to email,
                    "active" to true,
                    "status" to "active",
                    "timestamp" to FieldValue.serverTimestamp()
                )
                db.collection("sos_alerts").document(uid).set(initialData, SetOptions.merge())

                // 3. Loop de ubicación forzada
                trackingJob = launch {
                    while (_isTracking.value) {
                        val latLng = locationHelper.getCurrentLocation()
                        if (latLng != null) {
                            val locData = mapOf(
                                "location" to GeoPoint(latLng.latitude, latLng.longitude),
                                "timestamp" to FieldValue.serverTimestamp()
                            )
                            db.collection("sos_alerts").document(uid).update(locData)
                                .addOnSuccessListener { Log.d("SOS_DEBUG", "Ubicación enviada OK") }
                        } else {
                            Log.w("SOS_DEBUG", "GPS no devolvió ubicación aún")
                        }
                        delay(4000) // Actualizar cada 4 segundos
                    }
                }
            } catch (e: Exception) {
                Log.e("SOS_DEBUG", "Error en startTracking", e)
            }
        }
    }

    fun stopTracking() {
        _isTracking.value = false
        trackingJob?.cancel()
        trackingJob = null
        val uid = auth.currentUser?.uid ?: offlineSession?.userId ?: ""
        if (uid.isNotEmpty()) {
            db.collection("sos_alerts").document(uid).update("active", false, "status", "ended")
        }
    }

    override fun onCleared() {
        super.onCleared()
        trackingJob?.cancel()
    }
}
