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
                // 1. Obtener identificador (Matrícula o Email)
                val user = auth.currentUser
                val email = user?.email ?: offlineSession?.email ?: ""
                val matricula = email.substringBefore("@")
                val uid = user?.uid ?: offlineSession?.userId ?: matricula

                // 2. BUSCAR DATOS REALES DEL ALUMNO EN LA COLECCIÓN "alumnos"
                var nombreAlumno = "Alumno Desconocido"
                var carreraAlumno = ""
                
                try {
                    val doc = db.collection("alumnos").document(matricula).get().await()
                    if (doc.exists()) {
                        nombreAlumno = doc.getString("nombre") ?: nombreAlumno
                        carreraAlumno = doc.getString("carrera") ?: ""
                    }
                } catch (e: Exception) {
                    Log.e("SOS_DEBUG", "Error buscando datos del alumno", e)
                }

                // 3. CREAR ALERTA CON DATOS COMPLETOS
                val initialData = hashMapOf(
                    "alumnoId" to uid,
                    "matricula" to matricula,
                    "nombre" to nombreAlumno,
                    "carrera" to carreraAlumno,
                    "active" to true,
                    "timestamp" to FieldValue.serverTimestamp()
                )
                
                db.collection("sos_alerts").document(uid).set(initialData, SetOptions.merge())

                // 4. LOOP DE UBICACIÓN
                trackingJob = launch {
                    while (_isTracking.value) {
                        val latLng = locationHelper.getCurrentLocation()
                        if (latLng != null) {
                            val locData = mapOf(
                                "location" to GeoPoint(latLng.latitude, latLng.longitude),
                                "timestamp" to FieldValue.serverTimestamp()
                            )
                            db.collection("sos_alerts").document(uid).set(locData, SetOptions.merge())
                        }
                        delay(5000)
                    }
                }
            } catch (e: Exception) {
                Log.e("SOS_DEBUG", "Fallo general en SOS", e)
            }
        }
    }

    fun stopTracking() {
        val uid = auth.currentUser?.uid ?: offlineSession?.userId ?: return
        _isTracking.value = false
        trackingJob?.cancel()
        trackingJob = null
        db.collection("sos_alerts").document(uid).update("active", false)
    }

    override fun onCleared() {
        super.onCleared()
        trackingJob?.cancel()
    }
}
