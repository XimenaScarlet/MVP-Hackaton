package com.example.univapp.ui.admin

data class Alumno(
    val id: String = "",
    val nombre: String = "",
    val matricula: String = "",
    val correo: String = "",
    val carrera: String = "",
    val grupoId: String = ""
)

data class Grupo(
    val id: String = "",
    val nombre: String = "",
    val tutor: String = "",
    val carrera: String = "",
    val alumnos: List<String>? = emptyList()
)

data class Materia(
    val id: String = "",
    val nombre: String = "",
    val clave: String = "",
    val carrera: String = "",
    val descripcion: String = ""
)

data class Profesor(
    val id: String = "",
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val departamento: String = "",
    val materiasIds: List<String> = emptyList(),
    val gruposIds: List<String> = emptyList()
)

/** Colección /carreras */
data class Carrera(
    val id: String = "",
    val nombre: String = "",    // p.ej. "TSU en Logística", "Ingeniería en Software"
    val tipo: String = "",      // "TSU" | "Ingeniería"
    val activo: Boolean = true
)

/** Colección /horarios (1 doc por clase/slot) */
data class Horario(
    val id: String = "",
    val grupoId: String = "",
    val materiaId: String = "",
    val materiaNombre: String = "",
    val diaSemana: Int = 1,            // 1=Lun … 7=Dom
    val horaInicio: String = "08:00",  // HH:mm
    val horaFin: String = "09:00",     // HH:mm
    val aula: String = ""
)
