package com.example.univapp.ui.model

// ÚNICA definición del modelo para evitar redeclaraciones.
data class SubjectLite(
    val id: Long,
    val name: String,
    val professor: String,
    val room: String,
    val schedule: String, // Ej: "Lun & Mie · 07:00–08:40"
    val credits: Int
)

// Demo por cuatrimestre. Ajusta a tu repo/DB cuando gustes.
@Suppress("SpellCheckingInspection")
fun subjectsByTerm(term: Int): List<SubjectLite> = when (term) {
    1 -> listOf(
        SubjectLite(101, "Fundamentos de Programación", "Mtra. Sofía Lozano", "A-203", "Lun & Mie · 07:00–08:40", 8),
        SubjectLite(102, "Matemáticas I",               "Dr. Hugo Pérez",      "B-104", "Mar & Jue · 09:00–10:40", 7),
        SubjectLite(103, "Habilidades de Comunicación", "Mtro. Daniel Cortés", "C-002", "Vie · 11:00–13:30",      5),
        SubjectLite(104, "Introducción a Bases de Datos","Ing. M. Torres",     "Lab-1", "Lun · 12:00–14:00",      6),
        SubjectLite(105, "Inglés A1",                    "Lic. P. Ramos",       "D-101", "Mar & Jue · 11:00–12:40",4)
    )
    2 -> listOf(
        SubjectLite(201, "POO (Kotlin)",        "Mtra. Laura Rivas",   "Lab-2", "Lun & Mie · 09:00–10:40", 8),
        SubjectLite(202, "Estructuras de Datos","Dr. Luis Ortega",     "Lab-3", "Mar & Jue · 07:00–08:40", 8),
        SubjectLite(203, "Matemáticas II",      "Mtra. Ana Villarreal","B-201", "Vie · 12:00–14:00",      6)
    )
    else -> emptyList()
}
