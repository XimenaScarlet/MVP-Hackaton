package com.example.univapp.data

data class MedicalAppointment(
    val id: String = "",
    val userId: String = "",
    val service: String = "Médico General",
    val reason: String = "",
    val priority: String = "Normal",
    val date: String = "",     // ejemplo: "2026-01-27"
    val time: String = "",     // "10:00"
    val location: String = "Consultorio A-102",
    val status: String = "CONFIRMADA",
    val createdAtMillis: Long = 0L
)
