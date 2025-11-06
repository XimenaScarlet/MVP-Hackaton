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
private val HeaderGreen = Color(0xFF15C46A)
private val CardBg = Color.White
private val Muted = Color(0xFF6B7280)

/* ----------------- Modelo simple ----------------- */

data class ClassItem(
    val name: String,
    val type: String = "Clase", // Clase/Laboratorio/Examen
    val start: String,         // “09:00”
    val end: String,           // “09:50”
    val room: String,          // “A-204”
    val teacher: String = "",
    val dayOfWeek: Int         // Calendar.SUNDAY..SATURDAY
)

/* Datos mock (materias por cada día) */
private val mockSchedule = listOf(
    // DOMINGO (ejemplo vacío)
    ClassItem("Repaso", "Clase", "11:00", "11:50", "B-101", "—", Calendar.SUNDAY),

    // LUNES
    ClassItem("Matemáticas", "Clase", "09:00", "09:50", "A-101", "Adam Spencer", Calendar.MONDAY),
    ClassItem("Inglés", "Clase", "10:00", "10:50", "A-103", "Isabella", Calendar.MONDAY),
    ClassItem("Matemáticas", "Examen", "11:00", "11:50", "A-101", "", Calendar.MONDAY),
    ClassItem("Ciencias", "Laboratorio", "13:00", "13:50", "A-202", "Katharine Brown", Calendar.MONDAY),

    // MARTES
    ClassItem("POO", "Clase", "08:00", "08:50", "Lab 3", "Luis Díaz", Calendar.TUESDAY),
    ClassItem("Bases de Datos", "Clase", "10:00", "10:50", "A-210", "M. Torres", Calendar.TUESDAY),

    // MIÉRCOLES
    ClassItem("Redes", "Clase", "09:00", "09:50", "B-201", "C. Pérez", Calendar.WEDNESDAY),
    ClassItem("Inglés", "Clase", "12:00", "12:50", "A-103", "Isabella", Calendar.WEDNESDAY),

    // JUEVES
    ClassItem("Cálculo", "Clase", "08:00", "08:50", "A-204", "M. Torres", Calendar.THURSDAY),
    ClassItem("Ciencias", "Laboratorio", "13:00", "13:50", "A-202", "Katharine Brown", Calendar.THURSDAY),

    // VIERNES
    ClassItem("Programación", "Clase", "09:00", "09:50", "Lab 2", "L. Díaz", Calendar.FRIDAY),
    ClassItem("Tutorías", "Clase", "11:30", "12:20", "A-204", "M. Torres", Calendar.FRIDAY),

    // SÁBADO
    ClassItem("Proyecto", "Clase", "10:00", "11:30", "A-110", "Equipo", Calendar.SATURDAY)
)

/* ----------------- Pantalla ----------------- */
@Composable
fun TimetableScreen(
    onBack: (() -> Unit)? = null
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // Fecha seleccionada (hoy como inicio)
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    val sdfMonthYear = remember { SimpleDateFormat("MMMM yyyy", Locale("es", "MX")) }
    val classesForDay = remember(selectedDate.timeInMillis) {
        mockSchedule.filter { it.dayOfWeek == selectedDate.get(Calendar.DAY_OF_WEEK) }
            .sortedBy { it.start }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Horario") },
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
            // Header verde en español y con el día seleccionado al centro
            HeaderSemana(
                selectedDate = selectedDate,
                title = if (isToday(selectedDate)) "Hoy" else sdfMonthYear.format(selectedDate.time).replaceFirstChar { it.uppercase() },
                onPrev = { selectedDate = (selectedDate.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) } },
                onNext = { selectedDate = (selectedDate.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) } },
                onPickDate = { selectedDate = it }
            )

            // Lista de clases del día
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
                        ClassRowCard(cls)
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
        // Mover para que el índice 3 (centro) quede visible
        scope.launch { listState.scrollToItem(0) }
    }

    Surface(color = HeaderGreen, contentColor = Color.White) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(week.size) { i ->
                    val day = week[i]
                    val isSelected = i == 3 // el del centro
                    Column(
                        modifier = Modifier
                            .width(48.dp)
                            .clickable {
                                onPickDate(day)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(dayLabels[day.get(Calendar.DAY_OF_WEEK) - 1].uppercase(), fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
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
                                color = if (isSelected) HeaderGreen else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildWeekCentered(center: Calendar): List<Calendar> {
    return List(7) { i ->
        (center.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, i - 3) }
    }
}

private fun isToday(c: Calendar): Boolean {
    val now = Calendar.getInstance()
    return now.get(Calendar.YEAR) == c.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == c.get(Calendar.DAY_OF_YEAR)
}

/* ----------------- Fila de clase ----------------- */
@Composable
private fun ClassRowCard(cls: ClassItem) {
    val stripeColor = colorFromName(cls.name)

    Surface(
        color = CardBg,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth()) {
            // Columna de horas a la izquierda
            Column(
                modifier = Modifier
                    .widthIn(min = 86.dp)
                    .padding(start = 14.dp, top = 12.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(cls.start, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                Text(cls.end, fontSize = 12.sp, color = Muted)
            }

            // Línea vertical de color
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(stripeColor)
            )

            // Contenido de la tarjeta
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(cls.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    AssistChip(onClick = {}, label = { Text(cls.type) })
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Place, contentDescription = null, tint = Muted, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${cls.room}", color = Muted, fontSize = 12.sp)
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
    val r = 100 + (h and 0x7F)
    val g = 100 + ((h shr 8) and 0x7F)
    val b = 100 + ((h shr 16) and 0x7F)
    return Color(r, g, b)
}
