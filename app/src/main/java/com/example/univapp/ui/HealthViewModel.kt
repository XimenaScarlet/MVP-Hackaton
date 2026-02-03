package com.example.univapp.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class HealthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _availableTimes = MutableStateFlow<List<String>>(emptyList())
    val availableTimes = _availableTimes.asStateFlow()

    // Appointment Draft
    var draftReason = ""
    var draftIsUrgent = false
    var draftAllergies = ""
    var draftMedications = ""
    var draftDate: Date? = null
    var draftTime = ""
    var draftType = "Médico General"

    fun setDraftInfo(reason: String, isUrgent: Boolean, allergies: String, medications: String) {
        draftReason = reason
        draftIsUrgent = isUrgent
        draftAllergies = allergies
        draftMedications = medications
    }

    fun setDraftSchedule(date: Date, time: String) {
        draftDate = date
        draftTime = time
    }

    fun loadAvailableSlots(date: Date, professionalType: String) {
        viewModelScope.launch {
            _loading.value = true
            val allSlots = listOf(
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", 
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30"
            )
            try {
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                val snapshot = db.collection("citas")
                    .whereEqualTo("fecha", dateStr)
                    .whereEqualTo("tipoServicio", professionalType)
                    .get()
                    .await()
                
                val takenSlots = snapshot.documents.mapNotNull { it.getString("hora") }
                
                val now = Calendar.getInstance()
                val isToday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now.time) == dateStr
                
                _availableTimes.value = allSlots.filter { slot ->
                    if (takenSlots.contains(slot)) return@filter false
                    if (isToday) {
                        val slotHour = slot.split(":")[0].toInt()
                        val slotMin = slot.split(":")[1].toInt()
                        val slotTime = Calendar.getInstance().apply {
                            time = date
                            set(Calendar.HOUR_OF_DAY, slotHour)
                            set(Calendar.MINUTE, slotMin)
                        }
                        val buffer = Calendar.getInstance()
                        buffer.add(Calendar.MINUTE, 15)
                        return@filter slotTime.after(buffer)
                    }
                    true
                }
            } catch (e: Exception) {
                Log.e("HealthViewModel", "Error loading slots", e)
                _availableTimes.value = allSlots
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun confirmAppointment(providedUid: String? = null, providedEmail: String? = null): Boolean {
        val uid = providedUid ?: auth.currentUser?.uid ?: return false
        val email = providedEmail ?: auth.currentUser?.email ?: ""
        val date = draftDate ?: return false
        
        _loading.value = true
        return try {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
            val appointmentMap = hashMapOf(
                "userId" to uid,
                "userEmail" to email,
                "motivo" to draftReason.take(500),
                "esUrgente" to draftIsUrgent,
                "alergias" to draftAllergies,
                "medicamentos" to draftMedications,
                "fecha" to dateStr,
                "hora" to draftTime,
                "tipoServicio" to draftType,
                "status" to "PROGRAMADA",
                "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
            
            db.collection("citas").add(appointmentMap).await()
            true
        } catch (e: Exception) {
            Log.e("HealthViewModel", "Error confirming appointment", e)
            false
        } finally {
            _loading.value = false
        }
    }
}
