package com.example.univapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@IgnoreExtraProperties
data class SosAlert(
    val matricula: String = "",
    val authUid: String = "",
    val alumnoNombre: String = "",
    val email: String = "",
    val location: GeoPoint? = null,
    val active: Boolean = false,
    val status: String = "",
    @ServerTimestamp val timestamp: Date? = null
)

@HiltViewModel
class AdminSosViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {
    private val TAG = "SOS_ADMIN_DEBUG"
    private val _alerts = MutableStateFlow<List<SosAlert>>(emptyList())
    val alerts = _alerts.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    /**
     * Inicia la escucha en tiempo real de alertas SOS activas.
     * Se usa 'matricula' como identificador único del documento.
     */
    fun startListening() {
        if (listenerRegistration != null) return
        
        Log.d(TAG, "Escuchando alertas SOS activas uniformemente...")
        
        listenerRegistration = db.collection("sos_alerts")
            .whereEqualTo("active", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error en SnapshotListener: ${error.message}")
                    _error.value = "Error de conexión con el servidor SOS."
                    return@addSnapshotListener
                }
                
                val alertList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        // El documentId es la matrícula.
                        doc.toObject(SosAlert::class.java)?.copy(matricula = doc.id)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al procesar alerta ${doc.id}: ${e.message}")
                        null
                    }
                } ?: emptyList()
                
                Log.d(TAG, "Alertas SOS activas actualizadas: ${alertList.size}")
                _alerts.value = alertList
                _error.value = null
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
