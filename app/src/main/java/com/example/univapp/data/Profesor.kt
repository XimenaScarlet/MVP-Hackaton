package com.example.univapp.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Profesor(
    @get:Exclude var id: String = "",
    var nombre: String = "",
    var apellidos: String = "",
    var telefono: String? = null,
    var correo: String = "",
    var numeroEmpleado: String? = null,
    @ServerTimestamp var fechaAlta: Date? = null,
    var activo: Boolean = true,
    var turno: String = "",
    var carreraId: String = ""
)
