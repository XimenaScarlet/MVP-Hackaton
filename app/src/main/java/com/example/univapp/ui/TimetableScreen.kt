package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.univapp.data.Materia
import com.example.univapp.ui.util.AppScaffold
import java.util.Calendar

@Composable
fun TimetableScreen(
    onBack: () -> Unit = {},
    onMateriaClick: (Int, String) -> Unit = { _, _ -> },
    subjectsVm: SubjectsViewModel = hiltViewModel(),
    settingsVm: SettingsViewModel = hiltViewModel()
) {
    val materias by subjectsVm.subjects.collectAsStateWithLifecycle()
    val currentSemester by subjectsVm.currentSemester.collectAsStateWithLifecycle()
    val dark by settingsVm.darkMode.collectAsState()

    LaunchedEffect(Unit) {
        subjectsVm.loadUserSemesterAndSubjects()
    }

    val bgColor = if (dark) Color(0xFF0B101F) else Color(0xFFF8F9FF)
    val cardBg = if (dark) Color(0xFF131A2C) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val textColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val highlightDayBg = if (dark) Color(0xFF1E293B) else Color(0xFFF0F7FF)
    val dividerColor = if (dark) Color(0xFF334155) else Color(0xFFF1F3F4)

    val daysMap = mapOf("LUN" to "Lunes", "MAR" to "Martes", "MIE" to "Miercoles", "JUE" to "Jueves", "VIE" to "Viernes")
    val days = daysMap.keys.toList()
    
    val calendar = Calendar.getInstance()
    val todayName = when(calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "LUN"
        Calendar.TUESDAY -> "MAR"
        Calendar.WEDNESDAY -> "MIE"
        Calendar.THURSDAY -> "JUE"
        Calendar.FRIDAY -> "VIE"
        else -> ""
    }

    // Horas de clase de la tarde con intervalos de 45 minutos (5:00 PM a 10:15 PM)
    val timeSlots = listOf("17:00", "17:45", "18:30", "19:15", "20:00", "20:45", "21:30")

    AppScaffold { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(pv)
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart).background(if (dark) Color(0xFF1E293B) else Color.White, CircleShape).size(40.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = titleColor)
                }
                Text("Horario Escolar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor)
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${currentSemester}mo Cuatrimestre", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF818CF8))
                    Spacer(Modifier.width(12.dp))
                    Surface(color = Color(0xFF818CF8).copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                        Text("TARDE", color = Color(0xFF818CF8), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
                Text("Horario de la Tarde (05:00 PM - 10:15 PM)", fontSize = 14.sp, color = subtitleColor)
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 12.dp).padding(bottom = 20.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Text("HORA", modifier = Modifier.width(60.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = subtitleColor)
                        days.forEach { day ->
                            val isToday = day == todayName
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(day, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isToday) Color(0xFF818CF8) else subtitleColor)
                                if (isToday) Box(modifier = Modifier.size(4.dp).background(Color(0xFF818CF8), CircleShape))
                            }
                        }
                    }
                    HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
                    
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(timeSlots) { time ->
                            // Altura ajustada a 80.dp: un punto medio ideal para visibilidad y scroll
                            Row(modifier = Modifier.fillMaxWidth().height(87.dp), verticalAlignment = Alignment.Top) {
                                Text(time, modifier = Modifier.width(60.dp).padding(top = 10.dp), textAlign = TextAlign.Center, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = subtitleColor)
                                Row(modifier = Modifier.weight(1f)) {
                                    days.forEach { day ->
                                        val dayFull = daysMap[day]
                                        val materia = materias.find { it.dia == dayFull && it.horaInicio == time }
                                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.1.dp, dividerColor.copy(alpha = 0.1f))) {
                                            if (materia != null) {
                                                MateriaScheduleCard(
                                                    materia = materia,
                                                    onClick = { onMateriaClick(currentSemester, materia.id) }
                                                )
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
    }
}

@Composable
fun MateriaScheduleCard(materia: Materia, onClick: () -> Unit) {
    val color = when {
        materia.nombre.contains("Software") -> Color(0xFF6366F1)
        materia.nombre.contains("Audit") -> Color(0xFFA855F7)
        materia.nombre.contains("Cloud") -> Color(0xFF3B82F6)
        else -> Color(0xFFF43F5E)
    }
    
    Surface(
        modifier = Modifier.fillMaxSize().padding(2.dp).clickable { onClick() },
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center) {
            Text(materia.nombre, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 2, lineHeight = 10.sp)
            Text(materia.aula, fontSize = 7.sp, color = color.copy(alpha = 0.8f), maxLines = 1)
        }
    }
}
