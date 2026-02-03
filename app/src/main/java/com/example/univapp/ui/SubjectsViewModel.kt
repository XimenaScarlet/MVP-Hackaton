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

data class GradeData(
    val materiaName: String,
    val score: Double,
    val approved: Boolean
)

class SubjectsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _currentSemester = MutableStateFlow(1)
    val currentSemester: StateFlow<Int> = _currentSemester

    private val _subjects = MutableStateFlow<List<Materia>>(emptyList())
    val subjects: StateFlow<List<Materia>> = _subjects
    
    private val _grades = MutableStateFlow<List<GradeData>>(emptyList())
    val grades: StateFlow<List<GradeData>> = _grades

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadUserSemester() {
        loadUserSemesterAndSubjects()
    }

    fun loadUserSemesterAndSubjects() {
        val user = auth.currentUser ?: return
        val email = user.email.orEmpty()
        val matricula = email.substringBefore("@")
        
        _loading.value = true
        viewModelScope.launch {
            try {
                // Usamos el documento ID (matrícula) que es más confiable, igual que en ProfileViewModel
                val studentDoc = db.collection("alumnos").document(matricula).get().await()
                
                if (studentDoc.exists()) {
                    val grupoId = studentDoc.getString("grupoId") ?: ""
                    val alumnoId = studentDoc.id
                    
                    val semestreStr = studentDoc.getString("semestre") ?: studentDoc.getString("carreraId") ?: "1"
                    // Extraemos los dígitos (ej: "9°" -> "9")
                    val semestreInt = semestreStr.filter { it.isDigit() }.toIntOrNull() ?: 1
                    _currentSemester.value = semestreInt
                    
                    if (grupoId.isNotEmpty()) {
                        // Traemos las materias del grupo
                        val subjectsQuery = db.collection("materias")
                            .whereEqualTo("grupoId", grupoId)
                            .get()
                            .await()
                        
                        val materiasList = subjectsQuery.documents.mapNotNull { doc ->
                            doc.toObject(Materia::class.java)?.apply { id = doc.id }
                        }
                        _subjects.value = materiasList
                        
                        loadGrades(alumnoId, materiasList)
                    } else {
                        _subjects.value = emptyList()
                        _grades.value = emptyList()
                    }
                } else {
                    // Si no existe el documento por matrícula, intentamos por correo por si acaso
                    val studentQuery = db.collection("alumnos")
                        .whereEqualTo("correo", email)
                        .limit(1)
                        .get()
                        .await()
                    
                    if (!studentQuery.isEmpty) {
                        val doc = studentQuery.documents[0]
                        val grupoId = doc.getString("grupoId") ?: ""
                        val semestreStr = doc.getString("semestre") ?: doc.getString("carreraId") ?: "1"
                        val semestreInt = semestreStr.filter { it.isDigit() }.toIntOrNull() ?: 1
                        _currentSemester.value = semestreInt
                        
                        if (grupoId.isNotEmpty()) {
                            val subjectsQuery = db.collection("materias")
                                .whereEqualTo("grupoId", grupoId)
                                .get()
                                .await()
                            
                            val materiasList = subjectsQuery.documents.mapNotNull { d ->
                                d.toObject(Materia::class.java)?.apply { id = d.id }
                            }
                            _subjects.value = materiasList
                            loadGrades(doc.id, materiasList)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
