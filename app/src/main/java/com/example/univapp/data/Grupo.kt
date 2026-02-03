package com.example.univapp.data

import com.google.firebase.firestore.Exclude

data class Grupo(
    @get:Exclude var id: String = "",
    var nombre: String? = null,
    var carreraId: String? = null,
    var turno: String? = null,
    var numAlumnos: Int? = 0,
    var tutorId: String? = null,
    var programType: String? = null
)
