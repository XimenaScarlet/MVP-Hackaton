package com.example.univapp.ui

import androidx.lifecycle.ViewModel
import com.example.univapp.data.Horario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimetableViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _horarios = MutableStateFlow<List<Horario>>(emptyList())
    val horarios = _horarios.asStateFlow()

    private val _groupName = MutableStateFlow("")
    val groupName = _groupName.asStateFlow()

    private val _carreraName = MutableStateFlow("")
    val carreraName = _carreraName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun load() {
        val user = auth.currentUser ?: return
        val matricula = user.email?.substringBefore("@") ?: return

        _isLoading.value = true
        db.collection("alumnos").document(matricula).get()
            .addOnSuccessListener { alumnoDoc ->
                val grupoId = alumnoDoc.getString("grupoId") ?: ""
                val carreraId = alumnoDoc.getString("carreraId") ?: ""

                if (grupoId.isNotBlank()) {
                    fetchGroupName(grupoId)
                    fetchHorarios(grupoId)
                }
                if (carreraId.isNotBlank()) {
                    fetchCarreraName(carreraId)
                }
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

    private fun fetchGroupName(grupoId: String) {
        db.collection("grupos").document(grupoId).get()
            .addOnSuccessListener { doc ->
                _groupName.value = doc.getString("nombre") ?: grupoId
            }
    }

    private fun fetchCarreraName(carreraId: String) {
        db.collection("carreras").document(carreraId).get()
            .addOnSuccessListener { doc ->
                _carreraName.value = doc.getString("nombre") ?: ""
            }
    }

    private fun fetchHorarios(grupoId: String) {
        db.collection("horarios").whereEqualTo("grupoId", grupoId).get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Horario::class.java)?.apply { id = it.id } }
                _horarios.value = list
            }
    }
}
