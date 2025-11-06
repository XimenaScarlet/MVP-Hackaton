package com.example.univapp.data

data class Alumno(
    val id: String = "",
    val nombre: String = "",
    val matricula: String = "",
    val carrera: String = "",
    val semestre: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)
