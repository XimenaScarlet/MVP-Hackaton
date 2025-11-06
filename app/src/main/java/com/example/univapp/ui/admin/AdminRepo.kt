package com.example.univapp.ui.admin

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AdminRepo {

    private val db = Firebase.firestore
    private fun log(msg: String) = Log.d("AdminRepo", msg)

    /* -------------------- Mappers -------------------- */

    private fun DocumentSnapshot.toAlumnoSafe(): Alumno = Alumno(
        id = id,
        nombre = getString("nombre") ?: "",
        matricula = getString("matricula") ?: "",
        correo = getString("correo") ?: "",
        carrera = getString("carrera") ?: "",
        grupoId = getString("grupoId") ?: ""
    )

    private fun DocumentSnapshot.toGrupoSafe(): Grupo = Grupo(
        id = id,
        nombre = getString("nombre") ?: "",
        tutor = getString("tutor") ?: "",
        carrera = getString("carrera") ?: "",
        alumnos = (get("alumnos") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
    )

    private fun DocumentSnapshot.toMateriaSafe(): Materia = Materia(
        id = id,
        nombre = getString("nombre") ?: "",
        clave = getString("clave") ?: "",
        carrera = getString("carrera") ?: "",
        descripcion = getString("descripcion") ?: ""
    )

    private fun DocumentSnapshot.toProfesorSafe(): Profesor = Profesor(
        id = id,
        nombre = getString("nombre") ?: "",
        correo = getString("correo") ?: "",
        telefono = getString("telefono") ?: "",
        departamento = getString("departamento") ?: "",
        materiasIds = (get("materiasIds") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        gruposIds   = (get("gruposIds") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
    )

    private fun DocumentSnapshot.toHorarioSafe(): Horario = Horario(
        id = id,
        grupoId = getString("grupoId") ?: "",
        materiaId = getString("materiaId") ?: "",
        materiaNombre = getString("materiaNombre") ?: "",
        diaSemana = (getLong("diaSemana") ?: 1L).toInt(),
        horaInicio = getString("horaInicio") ?: "08:00",
        horaFin = getString("horaFin") ?: "09:00",
        aula = getString("aula") ?: ""
    )

    private fun DocumentSnapshot.toCarreraSafe(): Carrera = Carrera(
        id     = id,
        nombre = getString("nombre") ?: "",
        tipo   = getString("tipo") ?: "",
        activo = getBoolean("activo") ?: true
    )

    /* -------------------- Flujos en vivo -------------------- */

    val alumnosFlow: Flow<List<Alumno>> = callbackFlow {
        val reg: ListenerRegistration = db.collection("alumnos")
            .addSnapshotListener { qs, e ->
                if (e != null) { trySend(emptyList()); return@addSnapshotListener }
                trySend(qs?.documents?.map { it.toAlumnoSafe() } ?: emptyList())
            }
        awaitClose { reg.remove() }
    }

    val gruposFlow: Flow<List<Grupo>> = callbackFlow {
        val reg = db.collection("grupos")
            .addSnapshotListener { qs, e ->
                if (e != null) { trySend(emptyList()); return@addSnapshotListener }
                trySend(qs?.documents?.map { it.toGrupoSafe() } ?: emptyList())
            }
        awaitClose { reg.remove() }
    }

    val materiasFlow: Flow<List<Materia>> = callbackFlow {
        val reg = db.collection("materias")
            .addSnapshotListener { qs, e ->
                if (e != null) { trySend(emptyList()); return@addSnapshotListener }
                trySend(qs?.documents?.map { it.toMateriaSafe() } ?: emptyList())
            }
        awaitClose { reg.remove() }
    }

    val profesoresFlow: Flow<List<Profesor>> = callbackFlow {
        val reg = db.collection("profesores")
            .addSnapshotListener { qs, e ->
                if (e != null) { trySend(emptyList()); return@addSnapshotListener }
                trySend(qs?.documents?.map { it.toProfesorSafe() } ?: emptyList())
            }
        awaitClose { reg.remove() }
    }

    /** Carreras activas para el dropdown */
    val carrerasFlow: Flow<List<Carrera>> = callbackFlow {
        val reg = db.collection("carreras")
            .whereEqualTo("activo", true)           // asegúrate que 'activo' sea Boolean en Firestore
            .addSnapshotListener { qs, e ->
                if (e != null) { trySend(emptyList()); return@addSnapshotListener }
                val list = qs?.documents?.map { it.toCarreraSafe() } ?: emptyList()
                log("carrerasFlow -> ${list.size} items")
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    /** Horarios por grupo */
    fun horariosByGrupoFlow(grupoId: String): Flow<List<Horario>> = callbackFlow {
        val reg = db.collection("horarios")
            .whereEqualTo("grupoId", grupoId)
            .addSnapshotListener { qs, e ->
                if (e != null) { trySend(emptyList()); return@addSnapshotListener }
                trySend(qs?.documents?.map { it.toHorarioSafe() } ?: emptyList())
            }
        awaitClose { reg.remove() }
    }

    /* -------------------- Warmup opcional -------------------- */
    suspend fun refreshAll() {
        // Solo para “despertar” cachés; no bloquea los flows
        db.collection("carreras").limit(1).get().await()
        db.collection("grupos").limit(1).get().await()
        db.collection("materias").limit(1).get().await()
        db.collection("profesores").limit(1).get().await()
        db.collection("alumnos").limit(1).get().await()
    }

    /* -------------------- CRUD resumidos -------------------- */

    suspend fun saveAlumno(a: Alumno) {
        val data = mapOf(
            "nombre" to a.nombre, "matricula" to a.matricula, "correo" to a.correo,
            "carrera" to a.carrera, "grupoId" to a.grupoId
        )
        if (a.id.isBlank()) db.collection("alumnos").add(data).await()
        else db.collection("alumnos").document(a.id).set(data, SetOptions.merge()).await()
    }
    suspend fun deleteAlumno(id: String) {
        if (id.isNotBlank()) db.collection("alumnos").document(id).delete().await()
    }

    suspend fun saveGrupo(g: Grupo) {
        val data = mapOf(
            "nombre" to g.nombre, "tutor" to g.tutor, "carrera" to g.carrera,
            "alumnos" to (g.alumnos ?: emptyList<String>())
        )
        if (g.id.isBlank()) db.collection("grupos").add(data).await()
        else db.collection("grupos").document(g.id).set(data, SetOptions.merge()).await()
    }
    suspend fun deleteGrupo(id: String) {
        if (id.isNotBlank()) db.collection("grupos").document(id).delete().await()
    }

    suspend fun saveMateria(m: Materia) {
        val data = mapOf(
            "nombre" to m.nombre, "clave" to m.clave, "carrera" to m.carrera,
            "descripcion" to m.descripcion
        )
        if (m.id.isBlank()) db.collection("materias").add(data).await()
        else db.collection("materias").document(m.id).set(data, SetOptions.merge()).await()
    }
    suspend fun deleteMateria(id: String) {
        if (id.isNotBlank()) db.collection("materias").document(id).delete().await()
    }

    suspend fun saveProfesor(p: Profesor) {
        val data = mapOf(
            "nombre" to p.nombre, "correo" to p.correo, "telefono" to p.telefono,
            "departamento" to p.departamento, "materiasIds" to p.materiasIds,
            "gruposIds" to p.gruposIds
        )
        if (p.id.isBlank()) db.collection("profesores").add(data).await()
        else db.collection("profesores").document(p.id).set(data, SetOptions.merge()).await()
    }
    suspend fun deleteProfesor(id: String) {
        if (id.isNotBlank()) db.collection("profesores").document(id).delete().await()
    }

    suspend fun saveHorario(h: Horario) {
        val data = mapOf(
            "grupoId" to h.grupoId, "materiaId" to h.materiaId, "materiaNombre" to h.materiaNombre,
            "diaSemana" to h.diaSemana, "horaInicio" to h.horaInicio, "horaFin" to h.horaFin,
            "aula" to h.aula
        )
        if (h.id.isBlank()) db.collection("horarios").add(data).await()
        else db.collection("horarios").document(h.id).set(data, SetOptions.merge()).await()
    }
    suspend fun deleteHorario(id: String) {
        if (id.isNotBlank()) db.collection("horarios").document(id).delete().await()
    }
}
