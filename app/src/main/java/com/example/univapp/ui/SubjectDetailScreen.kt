@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ------------------ Datos ------------------
data class SubjectDetail(
    val id: Long,
    val term: Int,
    val name: String,
    val professor: String,
    val room: String,
    val schedule: String,
    val credits: Int,
    val hoursPerWeek: Int,
    val about: String
)

data class UnitProgress(
    val index: Int,
    val title: String,
    val score: Int,
    val color: Color
)

// ------------------ Carga de datos ------------------
private fun loadSubjectDetail(term: Int, id: Long): SubjectDetail = when (id.toInt()) {
    101 -> SubjectDetail(
        id, term, "Fundamentos de Programación", "Mtra. Sofía Lozano",
        "A-203", "Lun & Mie · 07:00–08:40", 8, 4,
        "Lógica, variables, estructuras de control y funciones. Buenas prácticas y resolución de problemas."
    )
    102 -> SubjectDetail(
        id, term, "Matemáticas I", "Dr. Hugo Pérez",
        "B-104", "Mar & Jue · 09:00–10:40", 7, 4,
        "Álgebra y trigonometría aplicadas a la ingeniería de software."
    )
    else -> SubjectDetail(
        id, term, "Materia", "Por asignar", "—", "—", 6, 3,
        "Descripción no disponible."
    )
}

private fun unitsForSubject(id: Long): List<UnitProgress> = listOf(
    UnitProgress(1, "Unidad 1", 6, Color(0xFFE9D5FF)),
    UnitProgress(2, "Unidad 2", 8, Color(0xFFD1FAE5)),
    UnitProgress(3, "Unidad 3", 7, Color(0xFFD6F4FF)),
    UnitProgress(4, "Unidad 4", 9, Color(0xFFFDE68A))
)

// ------------------ UI ------------------
@Composable
fun SubjectDetailScreen(
    subjectId: Long,
    term: Int,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val detail = remember(subjectId, term) { loadSubjectDetail(term, subjectId) }
    val units = remember(subjectId) { unitsForSubject(subjectId) }

    val accent = Color(0xFF6C63FF)
    val bgScreen = Color(0xFFF5F7FB)
    val topGrad = Brush.verticalGradient(listOf(Color(0xFFEDEBFF), Color(0xFFF7F6FF)))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalles de la materia") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (backDispatcher != null) backDispatcher.onBackPressed() else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        val url = "https://aula.utc.edu.mx/login/index.php"
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Link, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Abrir Aula/Recursos", color = Color.White)
                }
            }
        }
    ) { pv ->
        val scroll = rememberScrollState()
        Column(
            Modifier
                .fillMaxSize()
                .background(bgScreen)
                .padding(pv)
                .verticalScroll(scroll)
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(3.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(topGrad)
                        .padding(18.dp)
                ) {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            detail.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2B55),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, null, tint = accent, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(detail.professor, color = Color(0xFF6D6A8A))
                        }
                        Spacer(Modifier.height(10.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text("Unidad actual: 2 • Control de flujo") }
                        )
                        Spacer(Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MetricChip(Icons.Outlined.School, detail.room, "Salón")
                            MetricChip(Icons.Outlined.Timer, "${detail.hoursPerWeek} h", "Por semana")
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val day = detail.schedule.substringBefore('·').trim()
                            val time = detail.schedule.substringAfter('·', "").trim()
                            MetricChip(Icons.Outlined.CalendarMonth, day, time)
                        }
                    }
                }
            }

            Text(
                "Sobre la materia",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color(0xFF2D2B55),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Text(
                detail.about,
                color = Color(0xFF5D6670),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 6.dp)
            )

            Spacer(Modifier.height(8.dp))

            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(1.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "Unidades",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D2B55),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    units.forEach { u ->
                        UnitScoreRow(
                            index = u.index,
                            title = u.title,
                            score = u.score,
                            colorBg = u.color
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            Spacer(Modifier.height(90.dp))
        }
    }
}

// ------------------ Subcomponentes ------------------
@Composable
private fun MetricChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDEBFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color(0xFF6C63FF), modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, color = Color(0xFF2D2B55))
                Text(subtitle, color = Color(0xFF6D6A8A), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun UnitScoreRow(index: Int, title: String, score: Int, colorBg: Color) {
    val barBg = Color(0xFFEFF1F6)
    val barFg = Color(0xFF2DD4BF)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colorBg.copy(alpha = .6f),
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(26.dp).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$index", fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                }
                Spacer(Modifier.width(10.dp))
                Text(title, modifier = Modifier.weight(1f), color = Color(0xFF111827))
                Spacer(Modifier.width(8.dp))
                Text("${score}/10", fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
            }
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(6.dp)).background(barBg)
            ) {
                Box(
                    Modifier.fillMaxHeight().fillMaxWidth((score.coerceIn(0, 10)) / 10f)
                        .background(barFg)
                )
            }
        }
    }
}
