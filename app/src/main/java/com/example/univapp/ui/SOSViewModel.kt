package com.example.univapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Session
import com.example.univapp.location.LocationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SOSViewModel(private val locationHelper: LocationHelper) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var trackingJob: Job? = null

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    // Para manejar sesiones offline si es necesario
    private var offlineSession: Session? = null
    fun setOfflineSession(session: Session?) { this.offlineSession = session }

    fun startTracking() {
        // 1. Obtener ID y Email (Online u Offline)
        // Usamos userId de Session según la definición en SessionManager.kt
        val uid = auth.currentUser?.uid ?: offlineSession?.userId ?: return
        val email = auth.currentUser?.email ?: offlineSession?.email ?: "Usuario Desconocido"

        if (trackingJob != null) return
        _isTracking.value = true

        // 2. Crear documento inicial (aunque no haya ubicación aún)
        viewModelScope.launch {
            val initialData = hashMapOf(
                "alumnoId" to uid,
                "email" to email,
                "active" to true,
                "status" to "active",
                "timestamp" to FieldValue.serverTimestamp()
            )
            db.collection("sos_alerts").document(uid).set(initialData)
        }

        // 3. Loop de rastreo
        trackingJob = viewModelScope.launch {
            while (_isTracking.value) {
                val latLng = locationHelper.getCurrentLocation()
                if (latLng != null) {
                    val updateData = mapOf(
                        "location" to GeoPoint(latLng.latitude, latLng.longitude),
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    db.collection("sos_alerts").document(uid).update(updateData)
                }
                delay(5000)
            }
        }
    }

    fun stopTracking() {
        val uid = auth.currentUser?.uid ?: offlineSession?.userId ?: return
        _isTracking.value = false
        trackingJob?.cancel()
        trackingJob = null
        
        val endData = mapOf(
            "active" to false,
            "status" to "ended",
            "timestamp" to FieldValue.serverTimestamp()
        )
        db.collection("sos_alerts").document(uid).update(endData)
    }

    override fun onCleared() {
        super.onCleared()
        trackingJob?.cancel()
    }
}
