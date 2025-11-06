package com.example.univapp.data

import com.example.univapp.util.await
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class AlumnosRepo(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val col get() = db.collection("alumnos")

    suspend fun add(alumno: Alumno) {
        val doc = if (alumno.id.isBlank()) col.document() else col.document(alumno.id)
        val withId = alumno.copy(id = doc.id)
        doc.set(withId).await()
    }

    fun stream() = callbackFlow<List<Alumno>> {
        val reg = col.orderBy("createdAt").addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snap?.toObjects<Alumno>().orEmpty()
            trySend(list)
        }
        awaitClose { reg.remove() }
    }

    suspend fun delete(id: String) { col.document(id).delete().await() }
}
