package com.example.univapp.ui.admin

import com.example.univapp.data.Materia
import com.example.univapp.data.Profesor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AdminRepo {

    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun getProfesores(): List<Profesor> {
        return try {
            db.collection("profesores").get().await().toObjects(Profesor::class.java)
        } catch (e: Exception) {
            // En un caso real, aquí se podría loguear el error.
            emptyList()
        }
    }

    suspend fun getMaterias(): List<Materia> {
        return try {
            db.collection("materias").get().await().toObjects(Materia::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Puedes agregar aquí más funciones para obtener alumnos, grupos, etc.
}
