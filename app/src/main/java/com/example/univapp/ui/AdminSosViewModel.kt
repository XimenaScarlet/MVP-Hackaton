package com.example.univapp.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.ListenerRegistration
import java.util.Date

@IgnoreExtraProperties
data class SosAlert(
    val alumnoId: String = "",
    val alumnoNombre: String = "",
    val email: String = "",
    val location: GeoPoint? = null,
    val active: Boolean = false,
    val status: String = "",
    @ServerTimestamp val timestamp: Date? = null
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
                    doc.toObject(SosAlert::class.java)
                } ?: emptyList()
                
                _alerts.value = alertList
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
