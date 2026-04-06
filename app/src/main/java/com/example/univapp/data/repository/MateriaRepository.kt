package com.example.univapp.data.repository

import android.util.Log
import com.example.univapp.data.Alumno
import com.example.univapp.data.Materia
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MateriaRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun getMateriasPorAlumno(matricula: String): Flow<List<Materia>> = callbackFlow {
        try {
            // 1. Obtener los datos del alumno usando la matrícula
            val alumnoDoc = db.collection("alumnos").document(matricula).get().await()
            
            // IMPORTANTE: Según tu descripción, en Alumno el campo es "groupId"
            val groupId = alumnoDoc.getString("groupId") 
            
            Log.d("MateriaRepo", "Buscando materias para Alumno: $matricula, GrupoId encontrado: $groupId")

            if (groupId != null) {
                // 2. Filtrar materias donde "grupoId" coincida con el ID del alumno
                val subscription = db.collection("materias")
                    .whereEqualTo("grupoId", groupId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("MateriaRepo", "Error en snapshot: ${error.message}")
                            return@addSnapshotListener
                        }
                        
                        val materias = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(Materia::class.java)?.apply { id = doc.id }
                        } ?: emptyList()
                        
                        Log.d("MateriaRepo", "Materias encontradas: ${materias.size}")
                        trySend(materias)
                    }
                awaitClose { subscription.remove() }
            } else {
                Log.w("MateriaRepo", "El alumno no tiene un groupId asignado")
                trySend(emptyList())
                awaitClose()
            }
        } catch (e: Exception) {
            Log.e("MateriaRepo", "Error general: ${e.message}")
            trySend(emptyList())
            awaitClose()
        }
    }

    suspend fun saveMateria(materia: Materia) {
        if (materia.id.isEmpty()) db.collection("materias").add(materia).await()
        else db.collection("materias").document(materia.id).set(materia).await()
    }
}
