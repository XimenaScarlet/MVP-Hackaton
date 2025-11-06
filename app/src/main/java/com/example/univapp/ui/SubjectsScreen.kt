@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------- Paleta / Estilos ---------- */
private val ScreenBg = Color(0xFFF6F3EF)
private val TitleColor = Color(0xFF243045)
private val Muted = Color(0xFF6C7A86)
private val Pastels = listOf(
    Color(0xFFEFE7FF),
    Color(0xFFDFF3FF),
    Color(0xFFFFF1D6),
    Color(0xFFE9F9E3),
    Color(0xFFFFE5EC),
    Color(0xFFE7FFF6)
)

/* ---------- Modelo simple ---------- */
data class SubjectLite(
    val id: Long,
    val name: String,
    val professor: String,
    val room: String,
    val schedule: String, // “Lun & Mie · 07:00–08:40”
    val credits: Int
)

/* Demo data por cuatrimestre */
private fun subjectsByTerm(term: Int): List<SubjectLite> = when (term) {
    1 -> listOf(
        SubjectLite(101, "Fundamentos de Programación", "Mtra. Sofía Lozano", "A-203", "Lun & Mie · 07:00–08:40", 8),
        SubjectLite(102, "Matemáticas I",               "Dr. Hugo Pérez",     "B-104", "Mar & Jue · 09:00–10:40", 7),
        SubjectLite(103, "Habilidades de Comunicación", "Mtro. Daniel Cortés","C-002", "Vie · 11:00–13:30",      5)
    )
    2 -> listOf(
        SubjectLite(201, "POO (Kotlin)",        "Mtra. Laura Rivas",  "Lab-2", "Lun & Mie · 09:00–10:40", 8),
        SubjectLite(202, "Estructuras de Datos","Dr. Luis Ortega",    "Lab-3", "Mar & Jue · 07:00–08:40", 8),
        SubjectLite(203, "Matemáticas II",      "Mtra. Ana Villarreal","B-201","Vie · 12:00–14:00",      6)
    )
    else -> emptyList()
}

/* ---------- Screen ---------- */
@Composable
fun SubjectsScreen(
    onBack: () -> Unit = {},
    onOpenSubject: (term: Int, subjectId: Long) -> Unit
) {
    var selectedTerm by remember { mutableStateOf(1) }
    var showPicker by remember { mutableStateOf(false) }
    val subjects = remember(selectedTerm) { subjectsByTerm(selectedTerm) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis materias", color = TitleColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showPicker = true },
                icon = { Icon(Icons.Outlined.Add, null) },
                text = { Text("Cuatrimestre $selectedTerm") },
                containerColor = Color(0xFF60A5FA),
                contentColor = Color.White
            )
        }
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .background(ScreenBg, RectangleShape)
                .padding(pv)
        ) {
            // Encabezado centrado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Materias de este período",
                    color = TitleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Cuatrimestre $selectedTerm",
                    color = Muted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (subjects.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sin materias para este cuatrimestre.", color = Muted)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subjects) { s ->
                        val bg = Pastels[(s.id % Pastels.size).toInt()]
                        SubjectCardPastel(
                            subject = s,
                            bg = bg,
                            onClick = { onOpenSubject(selectedTerm, s.id) }
                        )
                    }
                    item { Spacer(Modifier.height(76.dp)) }
                }
            }
        }
    }

    if (showPicker) {
        TermPickerSheet(
            current = selectedTerm,
            onSelect = { selectedTerm = it; showPicker = false },
            onDismiss = { showPicker = false }
        )
    }
}

/* ---------- Card pastel ---------- */
@Composable
private fun SubjectCardPastel(
    subject: SubjectLite,
    bg: Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = bg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 92.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                subject.name,
                fontWeight = FontWeight.SemiBold,
                color = TitleColor,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.School, contentDescription = null, tint = TitleColor.copy(alpha = .75f))
                Spacer(Modifier.width(8.dp))
                Text(subject.professor, color = TitleColor.copy(alpha = .8f), fontSize = 13.sp)
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Muted)
                Spacer(Modifier.width(8.dp))
                Text("${subject.schedule} • ${subject.room} • ${subject.credits} cr", color = Muted, fontSize = 12.sp)
            }
        }
    }
}

/* ---------- Picker cuatrimestre ---------- */
@Composable
private fun TermPickerSheet(
    current: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Selecciona cuatrimestre", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TitleColor)
            repeat(10) { idx ->
                val num = idx + 1
                val selected = num == current
                val bg = if (selected) Color(0xFF60A5FA).copy(.12f) else Color.Transparent
                val fg = if (selected) Color(0xFF2563EB) else TitleColor
                Surface(
                    color = bg,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(num) }
                ) {
                    Text(
                        "Cuatrimestre $num",
                        modifier = Modifier.padding(14.dp),
                        color = fg,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}
