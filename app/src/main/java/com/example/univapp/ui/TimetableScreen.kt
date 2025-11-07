@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/* --------- Paleta --------- */
private val ScreenBg = Color(0xFFF6F8FA)
private val HeaderBlueTrans = Color(0xAA2563EB) // azul más oscuro con transparencia
private val CardBg = Color.White
private val Muted = Color(0xFF6B7280)

/* ----------------- Modelo ----------------- */
data class ClassItem(
    val id: Long,
    val name: String,
    val type: String = "Clase",
    val start: String,
    val end: String,
    val room: String,
    val teacher: String = "",
    val dayOfWeek: Int
)

/* --------- Mock ampliado (ajústalo a tu fuente real después) --------- */
private val mockSchedule = listOf(
    // Lunes
    ClassItem(101, "Fundamentos de Programación", "Clase", "07:00", "08:40", "A-203", "Mtra. Sofía Lozano", Calendar.MONDAY),
    ClassItem(104, "Introducción a Bases de Datos", "Laboratorio", "12:00", "14:00", "Lab-1", "Ing. M. Torres", Calendar.MONDAY),

    // Martes
    ClassItem(102, "Matemáticas I", "Clase", "09:00", "10:40", "B-104", "Dr. Hugo Pérez", Calendar.TUESDAY),
    ClassItem(206, "Programación Orientada a Objetos (Kotlin)", "Clase", "11:00", "12:40", "Lab-2", "Mtra. Laura Rivas", Calendar.TUESDAY),

    // Miércoles
    ClassItem(207, "Redes de Computadoras", "Clase", "08:00", "09:40", "B-201", "Ing. C. Pérez", Calendar.WEDNESDAY),
    ClassItem(208, "Habilidades de Comunicación", "Taller", "12:00", "13:30", "C-002", "Mtro. Daniel Cortés", Calendar.WEDNESDAY),

    // Jueves
    ClassItem(105, "Inglés A1", "Clase", "11:00", "12:40", "D-101", "Lic. P. Ramos", Calendar.THURSDAY),
    ClassItem(209, "Cálculo Diferencial", "Clase", "08:00", "09:40", "A-204", "Mtra. A. Villarreal", Calendar.THURSDAY),

    // Viernes
    ClassItem(210, "Estructuras de Datos", "Laboratorio", "09:00", "10:40", "Lab-3", "Dr. Luis Ortega", Calendar.FRIDAY),
    ClassItem(211, "Ingeniería de Software", "Clase", "11:30", "13:10", "A-110", "Mtro. J. Herrera", Calendar.FRIDAY),

    // Sábado
    ClassItem(212, "Proyecto Integrador", "Seminario", "10:00", "12:00", "Sala Proy-1", "Equipo", Calendar.SATURDAY)
)

/* ----------------- Pantalla ----------------- */
@Composable
fun TimetableScreen(
    term: Int = 1,
    onBack: (() -> Unit)? = null,
    onOpenSubject: (subjectId: Long, term: Int) -> Unit = { _, _ -> }
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    val sdfMonthYear = remember { SimpleDateFormat("MMMM yyyy", Locale("es", "MX")) }
    val monthYear = remember(selectedDate.timeInMillis) { sdfMonthYear.format(selectedDate.time) }
    val headerTitle = remember(monthYear, selectedDate.timeInMillis) {
        if (isToday(selectedDate)) "Hoy"
        else monthYear.replaceFirstChar { ch ->
            if (ch.isLowerCase()) ch.titlecase(Locale("es", "MX")) else ch.toString()
        }
    }

    val classesForDay = remember(selectedDate.timeInMillis) {
        mockSchedule.filter { it.dayOfWeek == selectedDate.get(Calendar.DAY_OF_WEEK) }
            .sortedBy { it.start }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Horario", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() ?: backDispatcher?.onBackPressed() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(pv)
        ) {
            HeaderSemana(
                selectedDate = selectedDate,
                title = headerTitle,
                onPrev = { selectedDate = (selectedDate.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) } },
                onNext = { selectedDate = (selectedDate.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) } },
                onPickDate = { selectedDate = it }
            )

            if (classesForDay.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay clases para este día", color = Muted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(classesForDay) { cls ->
                        ClassRowCard(
                            cls = cls,
                            onClick = { onOpenSubject(cls.id, term) }
                        )
                    }
                }
            }
        }
    }
}

/* ----------------- Header: semana con día centrado ----------------- */
@Composable
private fun HeaderSemana(
    selectedDate: Calendar,
    title: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onPickDate: (Calendar) -> Unit
) {
    val week = remember(selectedDate.timeInMillis) { buildWeekCentered(selectedDate) }
    val listState: LazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(week.first().timeInMillis) {
        scope.launch { listState.scrollToItem(0) }
    }

    Surface(color = HeaderBlueTrans, contentColor = Color.White) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrev, colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "Anterior")
                }
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onNext, colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)) {
                    Icon(Icons.Outlined.ChevronRight, contentDescription = "Siguiente")
                }
            }

            val dayLabels = listOf("dom","lun","mar","mié","jue","vie","sáb")

            androidx.compose.foundation.lazy.LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(week.size) { i ->
                    val day = week[i]
                    val isSelected = i == 3 // centro
                    Column(
                        modifier = Modifier
                            .width(48.dp)
                            .clickable { onPickDate(day) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            dayLabels[day.get(Calendar.DAY_OF_WEEK) - 1].uppercase(Locale("es","MX")),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.get(Calendar.DAY_OF_MONTH).toString(),
                                fontSize = 12.sp,
                                color = if (isSelected) HeaderBlueTrans else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildWeekCentered(center: Calendar): List<Calendar> =
    List(7) { i -> (center.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, i - 3) } }

private fun isToday(c: Calendar): Boolean {
    val now = Calendar.getInstance()
    return now.get(Calendar.YEAR) == c.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == c.get(Calendar.DAY_OF_YEAR)
}

/* ----------------- Fila de clase ----------------- */
@Composable
private fun ClassRowCard(cls: ClassItem, onClick: () -> Unit) {
    val stripeColor = colorFromName(cls.name)
    val displayName = remember(cls.name) { abbreviateTitle(cls.name, maxChars = 28) }

    Surface(
        color = CardBg,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .widthIn(min = 86.dp)
                    .padding(start = 14.dp, top = 12.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(cls.start, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                Text(cls.end, fontSize = 12.sp, color = Muted)
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(stripeColor)
            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(displayName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    AssistChip(onClick = {}, label = { Text(cls.type) })
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Place, contentDescription = null, tint = Muted, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(cls.room, color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.width(12.dp))
                    if (cls.teacher.isNotBlank()) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = Muted, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(cls.teacher, color = Muted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/* ----------------- Utils ----------------- */
private fun colorFromName(name: String): Color {
    val h = name.hashCode()
    val r = 90 + (h and 0x6F)
    val g = 110 + ((h shr 8) and 0x5F)
    val b = 150 + ((h shr 16) and 0x4F) // tiende más a azul
    return Color(r, g, b)
}

/**
 * Abrevia títulos largos de forma “natural”.
 * - Si contiene " de " mantiene "<Primera> de …"
 * - Si no, recorta por caracteres y agrega "…"
 */
private fun abbreviateTitle(title: String, maxChars: Int = 26): String {
    val clean = title.trim()
    if (clean.length <= maxChars) return clean

    val words = clean.split(' ')
    if (words.size >= 3) {
        val first = words[0]
        // Mantener la estructura “<Primera> de …” si hay “de”
        val idxDe = words.indexOfFirst { it.equals("de", ignoreCase = true) }
        if (idxDe in 1 until words.size) {
            val prefix = "$first de"
            val out = "$prefix …"
            return if (out.length <= maxChars + 3) out else "$first …"
        }
    }
    // fallback: recorte duro
    return clean.take(maxChars).trimEnd() + "…"
}

