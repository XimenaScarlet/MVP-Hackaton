package com.example.univapp.data.repository

import com.example.univapp.data.Alumno
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.example.univapp.data.Profesor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Filter
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface GroupsRepository {
    suspend fun getGroup(groupId: String): Grupo?
    suspend fun getCarrera(carreraId: String): Carrera?
    suspend fun getTutor(tutorId: String): Profesor?
    suspend fun getAlumnosByGroup(groupId: String, groupName: String?): List<Alumno>
}

@Singleton
class GroupsRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : GroupsRepository {

    override suspend fun getGroup(groupId: String): Grupo? {
        return try {
            db.collection("grupos").document(groupId).get().await()
                .toObject(Grupo::class.java)?.apply { id = groupId }
        } catch (e: Exception) { null }
    }

    override suspend fun getCarrera(carreraId: String): Carrera? {
        return try {
            db.collection("carreras").document(carreraId).get().await()
                .toObject(Carrera::class.java)?.apply { id = carreraId }
        } catch (e: Exception) { null }
    }

    override suspend fun getTutor(tutorId: String): Profesor? {
        return try {
            db.collection("profesores").document(tutorId).get().await()
                .toObject(Profesor::class.java)?.apply { id = tutorId }
        } catch (e: Exception) { null }
    }

    override suspend fun getAlumnosByGroup(groupId: String, groupName: String?): List<Alumno> {
        return try {
            // BUSQUEDA FLEXIBLE: Busca por ID real, por nombre legible o por el TEMP_ID de tus pruebas
            val query = if (groupName != null) {
                db.collection("alumnos").where(
                    Filter.or(
                        Filter.equalTo("grupoId", groupId),
                        Filter.equalTo("grupoId", groupName),
                        Filter.equalTo("grupoId", "TEMP_ID")
                    )
                )
            } else {
                db.collection("alumnos").whereEqualTo("grupoId", groupId)
            }

            query.get().await().documents.mapNotNull { 
                it.toObject(Alumno::class.java)?.apply { id = it.id } 
            }
        } catch (e: Exception) { emptyList() }
    }
}
