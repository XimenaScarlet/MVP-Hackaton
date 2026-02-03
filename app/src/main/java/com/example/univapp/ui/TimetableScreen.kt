@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.Horario
import java.util.Calendar

@Composable
fun TimetableScreen(
    onBack: () -> Unit = {},
    vm: TimetableViewModel = viewModel(),
    settingsVm: SettingsViewModel = viewModel()
) {
    val horarios by vm.horarios.collectAsState()
    val groupName by vm.groupName.collectAsState()
    val carreraName by vm.carreraName.collectAsState()
    val dark by settingsVm.darkMode.collectAsState()

    LaunchedEffect(Unit) {
        vm.load()
    }

    // Dynamic Colors
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF8F9FF)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val textColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val highlightDayBg = if (dark) Color(0xFF334155) else Color(0xFFF0F7FF)
    val dividerColor = if (dark) Color(0xFF334155) else Color(0xFFF1F3F4)

    val days = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE")
    val calendar = Calendar.getInstance()
    val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val todayName = when(currentDayOfWeek) {
        Calendar.MONDAY -> "LUN"
        Calendar.TUESDAY -> "MAR"
        Calendar.WEDNESDAY -> "MIÉ"
        Calendar.THURSDAY -> "JUE"
        Calendar.FRIDAY -> "VIE"
        else -> ""
    }

    val dates = remember {
        val list = mutableListOf<String>()
        val tempCal = Calendar.getInstance()
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        for (i in 0..4) {
            list.add(tempCal.get(Calendar.DAY_OF_MONTH).toString())
            tempCal.add(Calendar.DAY_OF_MONTH, 1)
        }
        list
    }
    
    val hoursList = remember(horarios) {
        if (horarios.isEmpty()) {
            val list = mutableListOf<String>()
            for (h in 17..21) {
                list.add(String.format("%02d:00", h))
                list.add(String.format("%02d:30", h))
            }
            list.add("22:00")
            list
        } else {
            val start = horarios.mapNotNull { it.horaInicio?.split(":")?.firstOrNull()?.toIntOrNull() }.minOrNull() ?: 11
            val end = horarios.mapNotNull { it.horaFin?.split(":")?.firstOrNull()?.toIntOrNull() }.maxOrNull() ?: 18
            val list = mutableListOf<String>()
            for (h in start..end) {
                list.add(String.format("%02d:00", h))
                list.add(String.format("%02d:30", h))
            }
            list
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .background(if (dark) Color(0xFF334155) else Color.White, CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Atrás",
                        modifier = Modifier.size(28.dp),
                        tint = titleColor
                    )
                }
                Text(
                    text = "Horarios",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            }

            // Header Info
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = groupName.ifBlank { "9IDGSA" },
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = if (dark) Color(0xFF60A5FA) else Color(0xFF002855)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        color = if (dark) Color(0xFF064E3B) else Color(0xFFE2F9E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "ACTIVO",
                            color = if (dark) Color(0xFF34D399) else Color(0xFF149E61),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = carreraName.ifBlank { "Tecnologías de la Información e Innovación Digital" },
                    fontSize = 16.sp,
                    color = subtitleColor,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Enero - Junio 2024",
                    fontSize = 14.sp,
                    color = subtitleColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Timetable Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Grid Header: Days
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HORA",
                            modifier = Modifier.width(64.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = subtitleColor
                        )
                        days.forEachIndexed { index, day ->
                            val isSelected = day == todayName
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .then(if (isSelected) Modifier.background(highlightDayBg, RoundedCornerShape(8.dp)) else Modifier),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = day,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF3B82F6) else subtitleColor
                                )
                                Text(
                                    text = dates[index],
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF3B82F6) else textColor
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = dividerColor)

                    // Grid Body
                    Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        Column {
                            hoursList.forEach { hour ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Time Label
                                    Text(
                                        text = hour,
                                        modifier = Modifier
                                            .width(64.dp)
                                            .padding(top = 12.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 11.sp,
                                        color = subtitleColor
                                    )
                                    // Grid Cells
                                    Row(modifier = Modifier.weight(1f)) {
                                        days.forEach { day ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                                    .border(0.5.dp, dividerColor)
                                            ) {
                                                val match = findClass(day, hour, horarios)
                                                if (match != null) {
                                                    ScheduleCardExact(match, dark)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Legend at Bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendDot(Color(0xFF3B82F6), "ARQUITECTURA", subtitleColor)
                Spacer(Modifier.width(12.dp))
                LegendDot(Color(0xFF10B981), "DISEÑO", subtitleColor)
                Spacer(Modifier.width(12.dp))
                LegendDot(Color(0xFF818CF8), "PROYECTOS", subtitleColor)
            }
        }
    }
}

private fun findClass(day: String, hour: String, databaseHorarios: List<Horario>): Horario? {
    if (databaseHorarios.isNotEmpty()) {
        return databaseHorarios.find { it.dias.contains(day) && it.horaInicio == hour }
    }
    return when {
        day == "LUN" && hour == "11:00" -> Horario(materiaNombre = "Arquitectura", salon = "Salón A", dias = listOf("LUN"))
        day == "VIE" && hour == "12:00" -> Horario(materiaNombre = "Arquitectura", salon = "Salón B", dias = listOf("VIE"))
        day == "MAR" && hour == "13:00" -> Horario(materiaNombre = "Diseño", salon = "Lab B", dias = listOf("MAR"))
        day == "JUE" && hour == "13:00" -> Horario(materiaNombre = "Diseño", salon = "Lab C", dias = listOf("JUE"))
        day == "MIÉ" && hour == "15:00" -> Horario(materiaNombre = "Proyectos", salon = "Sala J", dias = listOf("MIÉ"))
        else -> null
    }
}

@Composable
private fun ScheduleCardExact(horario: Horario, isDark: Boolean) {
    val color = when {
        horario.materiaNombre?.contains("Arq", true) == true -> if (isDark) Color(0xFF60A5FA) else Color(0xFF3B82F6)
        horario.materiaNombre?.contains("Dis", true) == true -> if (isDark) Color(0xFF34D399) else Color(0xFF10B981)
        else -> if (isDark) Color(0xFFA5B4FC) else Color(0xFF818CF8)
    }
    
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        color = color.copy(alpha = if (isDark) 0.2f else 0.12f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(color, RoundedCornerShape(4.dp)))
            Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.Center) {
                Text(
                    text = horario.materiaNombre?.take(4) + "...",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    maxLines = 1
                )
                Text(
                    text = horario.salon?.take(7) ?: "",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = color.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, text: String, textColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(text = text, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}
