package com.example.univapp.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.poiji.annotation.ExcelCell
import java.util.Date

data class Alumno(
    @get:Exclude var id: String = "",
    
    @ExcelCell(0) // Column A: Matrícula
    var matricula: String? = null,
    
    @ExcelCell(1) // Column B: Nombre
    var nombre: String? = null,
    
    @ExcelCell(2) // Column C: Correo
    var correo: String? = null,
    
    @ExcelCell(3) // Column D: Carrera ID (Optional)
    var carreraId: String? = null,
    
    @ExcelCell(4) // Column E: Grupo ID (Optional)
    var grupoId: String? = null,
    
    var estatusAcademico: String = "Activo",
    var genero: String = "M",
    var edad: Int? = 0,
    var activo: Boolean? = true,
    var telefono: String? = null,
    var fechaNacimiento: String? = null,
    var fechaIngreso: String? = null,
    var nombreContacto: String? = null,
    var telefonoEmergencia: String? = null,
    var direccion: String? = null,
    
    @ServerTimestamp val createdAt: Date? = null
)
