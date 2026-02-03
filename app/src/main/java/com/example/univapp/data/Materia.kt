package com.example.univapp.data

import com.google.firebase.firestore.Exclude

data class Materia(
    @get:Exclude var id: String = "",
    val nombre: String = "",
    val carreraId: String = "",
    val grupoId: String = "",
    val profesorId: String? = null,
    val periodo: String = "",
    val turno: String = "",
    val creditos: Int = 0,
    val clave: String = "",
    val aula: String = "",
    val descripcion: String = ""
)
