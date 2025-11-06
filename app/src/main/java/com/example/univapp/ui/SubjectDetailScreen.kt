@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------- Modelo simple para el detalle ---------- */
data class SubjectDetail(
    val id: Long,
    val term: Int,
    val name: String,
    val professor: String,
    val room: String,
    val schedule: String,   // e.g. "Lun & Mie · 07:00–08:40"
    val credits: Int,
    val hoursPerWeek: Int,
    val about: String,
    val evaluation: List<Pair<String, Int>>, // ("Parcial 1", 30) ...
    val resources: List<Pair<String, String?>> // (título, url opcional)
)

/* Demo data; cámbialo a tu repo/DB cuando gustes */
private fun loadSubjectDetail(term: Int, id: Long): SubjectDetail = when (id.toInt()) {
    101 -> SubjectDetail(
        id, term,
        name = "Fundamentos de Programación",
        professor = "Mtra. Sofía Lozano",
        room = "A-203",
        schedule = "Lun & Mie · 07:00–08:40",
        credits = 8,
        hoursPerWeek = 4,
        about = "Introducción a la lógica, variables, tipos de datos, estructuras de control y funciones. Se trabaja con problemas reales y buenas prácticas.",
        evaluation = listOf("Parcial 1" to 30, "Parcial 2" to 30, "Proyecto" to 30, "Asistencia" to 10),
        resources = listOf(
            "Sílabus (PDF)" to null,
            "Guía de ejercicios" to "https://example.com/fp/ejercicios",
            "Repositorio ejemplos" to "https://github.com/example/fp-2025"
        )
    )
    201 -> SubjectDetail(
        id, term,
        name = "POO (Kotlin)",
        professor = "Mtra. Laura Rivas",
        room = "Lab-2",
        schedule = "Lun & Mie · 09:00–10:40",
        credits = 8,
        hoursPerWeek = 4,
        about = "Clases, objetos, herencia, interfaces y colecciones en Kotlin. Proyecto final con app Android básica.",
        evaluation = listOf("Katas" to 25, "Parcial" to 35, "Proyecto" to 40),
        resources = listOf(
            "Kotlin docs" to "https://kotlinlang.org/docs/home.html",
            "Plantilla Android" to "https://github.com/example/android-starter"
        )
    )
    else -> SubjectDetail(
        id, term,
        name = "Materia",
        professor = "Por asignar",
        room = "—",
        schedule = "—",
        credits = 6,
        hoursPerWeek = 3,
        about = "Descripción no disponible.",
        evaluation = listOf("Evaluación" to 100),
        resources = emptyList()
    )
}

/* ---------- UI ---------- */

@Composable
fun SubjectDetailScreen(
    subjectId: Long,
    term: Int,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val detail = remember(subjectId, term) { loadSubjectDetail(term, subjectId) }

    // Colores “card morada” y fondo claro, como el mock
    val topGrad = Brush.verticalGradient(listOf(Color(0xFFEDEBFF), Color(0xFFF7F6FF)))
    val accent = Color(0xFF6C63FF)

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(detail.name, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            // Botón grande redondo como “Clean Delivery”
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        // abre (si existe) el primer recurso con URL; si no, un intento a Classroom/Teams de ejemplo
                        val url = detail.resources.firstOrNull { it.second != null }?.second
                            ?: "https://teams.microsoft.com/"
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Link, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Abrir Aula/Recursos", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FB))
                .padding(pv)
        ) {
            /* ----- Card hero superior (como la pastilla grande) ----- */
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(topGrad)
                        .padding(18.dp)
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        Text(detail.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D2B55))
                        Spacer(Modifier.height(4.dp))
                        Text("Cuatrimestre $term • ${detail.room}", color = Color(0xFF6D6A8A))
                        Spacer(Modifier.height(14.dp))

                        // Chips (dosis -> aquí métricas de materia)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            InfoChip(
                                icon = Icons.Outlined.School,
                                title = "${detail.credits}",
                                subtitle = "Créditos"
                            )
                            InfoChip(
                                icon = Icons.Outlined.Timer,
                                title = "${detail.hoursPerWeek} h",
                                subtitle = "Por semana"
                            )
                            InfoChip(
                                icon = Icons.Outlined.CalendarMonth,
                                title = detail.schedule.substringBefore('·').trim(),
                                subtitle = detail.schedule.substringAfter('·', "")
                            )
                        }
                    }
                }
            }

            /* ----- Sección “About Subject” ----- */
            SectionTitle("Sobre la materia")
            Text(
                detail.about,
                color = Color(0xFF5D6670),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(14.dp))

            /* ----- Profesor ----- */
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(2.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE9E7FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = accent)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(detail.professor, fontWeight = FontWeight.SemiBold, color = Color(0xFF2D2B55))
                        Text(detail.schedule, color = Color(0xFF6D6A8A), fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ----- Evaluación ----- */
            SectionTitle("Evaluación")
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                detail.evaluation.forEach { (label, pct) ->
                    EvaluationRow(label, pct)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ----- Recursos / Alternativas ----- */
            SectionTitle("Recursos")
            Column(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (detail.resources.isEmpty()) {
                    Text("No hay recursos registrados.", color = Color(0xFF6D6A8A))
                } else {
                    detail.resources.forEach { (label, url) ->
                        ResourceItem(label = label, url = url)
                    }
                }
            }

            Spacer(Modifier.height(90.dp)) // espacio para el botón inferior
        }
    }
}

/* ---------- Pieces ---------- */

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = Color(0xFF2D2B55),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDEBFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF6C63FF))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, color = Color(0xFF2D2B55))
                Text(subtitle, color = Color(0xFF6D6A8A), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun EvaluationRow(label: String, percent: Int) {
    val barBg = Color(0xFFECEBFF)
    val barFg = Color(0xFF6C63FF)
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color(0xFF495366))
            Text("$percent%", color = Color(0xFF495366), fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(barBg)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percent.coerceIn(0, 100) / 100f)
                    .background(barFg)
            )
        }
    }
}

@Composable
private fun ResourceItem(label: String, url: String?) {
    val ctx = LocalContext.current
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Book, contentDescription = null, tint = Color(0xFF6C63FF))
            Spacer(Modifier.width(12.dp))
            Text(label, modifier = Modifier.weight(1f), color = Color(0xFF2D2B55))
            if (url != null) {
                OutlinedButton(
                    onClick = {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Abrir") }
            } else {
                AssistChip(
                    onClick = {},
                    label = { Text("Archivo local") },
                    leadingIcon = { Icon(Icons.Outlined.CheckCircle, null) }
                )
            }
        }
    }
}
