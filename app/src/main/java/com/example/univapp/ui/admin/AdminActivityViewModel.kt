package com.example.univapp.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.univapp.data.ActivityLog
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class AdminActivityViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _logs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val logs = _logs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchActivityLogs()
    }

    private fun fetchActivityLogs() {
        _isLoading.value = true
        db.collection("activity_log")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("AdminActivityVM", "Listen failed.", e)
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                val logList = snapshot?.documents?.mapNotNull { doc ->
                    val type = doc.getString("type") ?: "UNKNOWN"
                    val timestamp = doc.getTimestamp("timestamp")?.toDate()
                    val formattedDate = timestamp?.let {
                        SimpleDateFormat("yyyy-MM-dd • HH:mm", Locale.getDefault()).format(it)
                    } ?: ""

                    ActivityLog(
                        id = doc.id,
                        type = type,
                        timestamp = formattedDate,
                        description = doc.getString("description") ?: ""
                    )
                } ?: emptyList()

                _logs.value = logList
                _isLoading.value = false
            }
    }
}
