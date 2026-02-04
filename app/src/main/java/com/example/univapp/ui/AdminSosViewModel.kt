package com.example.univapp.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.ListenerRegistration

data class SosAlert(
    val alumnoId: String = "",
    val nombre: String = "",
    val email: String = "",
    val matricula: String = "",
    val location: GeoPoint? = null,
    val active: Boolean = false,
    val status: String = ""
)

class AdminSosViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _alerts = MutableStateFlow<List<SosAlert>>(emptyList())
    val alerts = _alerts.asStateFlow()
    private var listenerRegistration: ListenerRegistration? = null

    fun startListening() {
        if (listenerRegistration != null) return
        
        listenerRegistration = db.collection("sos_alerts")
            .whereEqualTo("active", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                val alertList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        SosAlert(
                            alumnoId = doc.getString("alumnoId") ?: "",
                            nombre = doc.getString("nombre") ?: "",
                            email = doc.getString("email") ?: "",
                            matricula = doc.getString("matricula") ?: "",
                            location = doc.getGeoPoint("location"),
                            active = doc.getBoolean("active") ?: false,
                            status = doc.getString("status") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                _alerts.value = alertList
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
