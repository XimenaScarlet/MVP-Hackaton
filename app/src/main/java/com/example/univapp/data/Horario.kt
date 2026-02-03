package com.example.univapp.data

import com.google.firebase.firestore.Exclude

data class Horario(
    @get:Exclude var id: String = "",
    var materiaId: String? = null,
    var materiaNombre: String? = null, // Denormalized for easy display
    var dias: List<String> = emptyList(), // LUN, MAR, MIE, JUE, VIE
    var horaInicio: String? = null, // "07:00"
    var horaFin: String? = null, // "09:00"
    var salon: String? = null,
    var profesorId: String? = null,
    var profesorNombre: String? = null, // Denormalized
    var grupoId: String? = null,
    var carreraId: String? = null
)
