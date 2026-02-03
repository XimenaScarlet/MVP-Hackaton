package com.example.univapp.data

import com.example.univapp.util.await
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class AlumnosRepo(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val col get() = db.collection("alumnos")

    suspend fun add(alumno: Alumno) {
        // Usamos la matrícula como ID del documento si está disponible, si no, uno automático.
        val docId = alumno.matricula?.trim()?.ifBlank { null } ?: alumno.id.ifBlank { null }
        val doc = if (docId == null) col.document() else col.document(docId)
        
        // Guardamos el ID dentro del objeto también
        val finalAlumno = alumno.copy(id = doc.id)
        doc.set(finalAlumno).await()
    }

    fun stream() = callbackFlow<List<Alumno>> {
        val reg = col.orderBy("nombre").addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            // Mapeo manual para asegurar que se asigne el ID del documento
            val list = snap?.documents?.mapNotNull { d ->
                d.toObject(Alumno::class.java)?.apply { id = d.id }
            }.orEmpty()
            trySend(list)
        }
        awaitClose { reg.remove() }
    }

    suspend fun delete(id: String) { 
        if (id.isNotBlank()) col.document(id).delete().await() 
    }
}
