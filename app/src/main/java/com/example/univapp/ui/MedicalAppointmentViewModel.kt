package com.example.univapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.MedicalAppointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class MedicalAppointmentViewModel(app: Application) : AndroidViewModel(app) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val reason = MutableStateFlow("")
    val date = MutableStateFlow("")
    val time = MutableStateFlow("")
    val service = MutableStateFlow("Médico General")
    val priority = MutableStateFlow("Normal")
    val location = MutableStateFlow("Consultorio A-102")

    private val _confirmed = MutableStateFlow<MedicalAppointment?>(null)
    val confirmed: StateFlow<MedicalAppointment?> = _confirmed

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _userAppointments = MutableStateFlow<List<MedicalAppointment>>(emptyList())
    val userAppointments: StateFlow<List<MedicalAppointment>> = _userAppointments

    init {
        fetchUserAppointments()
    }

    fun fetchUserAppointments() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("medical_appointments")
            .whereEqualTo("userId", uid)
            .orderBy("createdAtMillis", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = snapshot.toObjects(MedicalAppointment::class.java)
                    _userAppointments.value = list
                }
            }
    }

    fun clearError() {
        _error.value = null
    }

    fun confirmAppointment(onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _error.value = "Sesión no válida. Por favor, inicia sesión de nuevo."
            return
        }
        if (reason.value.isBlank() || date.value.isBlank() || time.value.isBlank()) {
            _error.value = "Por favor, completa todos los campos de la cita."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val id = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()

            val appointment = MedicalAppointment(
                id = id,
                userId = uid,
                service = service.value,
                reason = reason.value,
                priority = priority.value,
                date = date.value,
                time = time.value,
                location = location.value,
                createdAtMillis = now,
                status = "CONFIRMADA"
            )

            val data = hashMapOf(
                "id" to id,
                "userId" to uid,
                "service" to appointment.service,
                "reason" to appointment.reason,
                "priority" to appointment.priority,
                "date" to appointment.date,
                "time" to appointment.time,
                "location" to appointment.location,
                "status" to appointment.status,
                "createdAt" to FieldValue.serverTimestamp(),
                "createdAtMillis" to now
            )

            db.collection("medical_appointments").document(id)
                .set(data)
                .addOnSuccessListener {
                    _confirmed.value = appointment
                    _loading.value = false
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    _loading.value = false
                    if (e.message?.contains("PERMISSION_DENIED") == true) {
                        _error.value = "Error de permisos en el servidor. Contacta al administrador."
                    } else {
                        _error.value = "No se pudo guardar la cita: ${e.localizedMessage}"
                    }
                }
        }
    }
}
