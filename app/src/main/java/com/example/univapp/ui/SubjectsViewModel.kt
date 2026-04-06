package com.example.univapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Materia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log

data class GradeData(
    val materiaName: String,
    val score: Double,
    val approved: Boolean
)

class SubjectsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Inicializamos con 10 para que sea el cuatrimestre "más grande" por defecto
    private val _currentSemester = MutableStateFlow(10)
    val currentSemester: StateFlow<Int> = _currentSemester

    private val _subjects = MutableStateFlow<List<Materia>>(emptyList())
    val subjects: StateFlow<List<Materia>> = _subjects
    
    private val _grades = MutableStateFlow<List<GradeData>>(emptyList())
    val grades: StateFlow<List<GradeData>> = _grades

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private var userGrupoId: String = ""
    private var userAlumnoId: String = ""

    fun loadUserSemester() {
        loadUserSemesterAndSubjects()
    }

    fun setSemester(semester: Int) {
        _currentSemester.value = semester
        if (userGrupoId.isNotEmpty()) {
            loadSubjectsBySemester(semester)
        }
    }

    fun loadUserSemesterAndSubjects() {
        val user = auth.currentUser ?: return
        val email = user.email.orEmpty()
        
        _loading.value = true
        viewModelScope.launch {
            try {
                val studentQuery = db.collection("alumnos")
                    .whereEqualTo("correo", email)
                    .get()
                    .await()
                
                if (!studentQuery.isEmpty) {
                    val studentDoc = studentQuery.documents[0]
                    userGrupoId = studentDoc.getString("groupId") ?: studentDoc.getString("grupoId") ?: ""
                    userAlumnoId = studentDoc.id
                    
                    // Si el alumno tiene un semestre en DB, lo respetamos, de lo contrario usamos el 10
                    val semestreStr = studentDoc.getString("semestre")
                    val semestreInt = semestreStr?.filter { it.isDigit() }?.toIntOrNull() ?: 10
                    
                    _currentSemester.value = semestreInt
                    
                    if (userGrupoId.isNotEmpty()) {
                        loadSubjectsBySemester(semestreInt)
                    }
                } else {
                    // Si no encontramos al alumno, cargamos por defecto el 10
                    loadSubjectsBySemester(10)
                }
            } catch (e: Exception) {
                Log.e("SubjectsVM", "Error: ${e.message}")
                loadSubjectsBySemester(10)
            } finally {
                _loading.value = false
            }
        }
    }

    private fun loadSubjectsBySemester(semester: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val periodoStr = when(semester) {
                    7 -> "7mo"; 8 -> "8vo"; 9 -> "9no"; 10 -> "10mo"; else -> semester.toString()
                }

                val subjectsQuery = db.collection("materias")
                    .whereEqualTo("grupoId", userGrupoId)
                    .whereEqualTo("periodo", periodoStr)
                    .get()
                    .await()
                
                val materiasList = subjectsQuery.documents.mapNotNull { doc ->
                    doc.toObject(Materia::class.java)?.apply { id = doc.id }
                }
                _subjects.value = materiasList
                
                if (userAlumnoId.isNotEmpty()) {
                    loadGrades(userAlumnoId, materiasList)
                }
            } catch (e: Exception) {
                Log.e("SubjectsVM", "Error loading subjects: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
    
    private suspend fun loadGrades(alumnoId: String, materias: List<Materia>) {
        try {
            val gradesQuery = db.collection("calificaciones")
                .whereEqualTo("alumnoId", alumnoId)
                .get()
                .await()
                
            val gradesMap = gradesQuery.documents.associate { 
                it.getString("materiaId") to (it.getDouble("calificacion") ?: 0.0)
            }
            
            val gradesList = materias.map { materia ->
                val score = gradesMap[materia.id] ?: 0.0
                GradeData(
                    materiaName = materia.nombre,
                    score = score,
                    approved = score >= 7.0
                )
            }
            _grades.value = gradesList
        } catch (e: Exception) {
            _grades.value = materias.map { GradeData(it.nombre, 0.0, false) }
        }
    }
}
