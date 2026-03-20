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
import com.example.univapp.ui.util.AppScaffold
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

    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF8F9FF)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val textColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val highlightDayBg = if (dark) Color(0xFF334155) else Color(0xFFF0F7FF)
    val dividerColor = if (dark) Color(0xFF334155) else Color(0xFFF1F3F4)

    val days = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE")
    val calendar = Calendar.getInstance()
    val todayName = when(calendar.get(Calendar.DAY_OF_WEEK)) {
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
    
    // Lista de horas optimizada (5:00 PM a 9:30 PM)
    val displayHours = remember {
        listOf("5:00 PM", "5:30 PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM")
    }
    val logicHours = listOf("17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30")

    AppScaffold { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(pv)
        ) {
            Spacer(Modifier.height(8.dp)) // Margen extra para la status bar
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart).background(if (dark) Color(0xFF334155) else Color.White, CircleShape).size(40.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Atrás", modifier = Modifier.size(24.dp), tint = titleColor)
                }
                Text(text = "Horario Escolar", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = titleColor)
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = groupName.ifBlank { "9IDGSA" }, fontSize = 30.sp, fontWeight = FontWeight.Black, color = if (dark) Color(0xFF60A5FA) else Color(0xFF002855))
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(color = if (dark) Color(0xFF064E3B) else Color(0xFFE2F9E9), shape = RoundedCornerShape(8.dp)) {
                        Text(text = "9NO CUATRI", color = if (dark) Color(0xFF34D399) else Color(0xFF149E61), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
                Text(text = "Turno Vespertino (5:00 PM - 9:30 PM)", fontSize = 14.sp, color = subtitleColor, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 12.dp).padding(bottom = 20.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "HORA", modifier = Modifier.width(64.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = subtitleColor)
                        days.forEachIndexed { index, day ->
                            val isSelected = day == todayName
                            Column(modifier = Modifier.weight(1f).then(if (isSelected) Modifier.background(highlightDayBg, RoundedCornerShape(8.dp)) else Modifier), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = day, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color(0xFF3B82F6) else subtitleColor)
                                Text(text = dates[index], fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color(0xFF3B82F6) else textColor)
                            }
                        }
                    }
                    HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
                    
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column {
                            displayHours.forEachIndexed { index, hourLabel ->
                                val logicHour = logicHours[index]
                                Row(modifier = Modifier.fillMaxWidth().height(52.dp), verticalAlignment = Alignment.Top) {
                                    Text(text = hourLabel, modifier = Modifier.width(64.dp).padding(top = 10.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = subtitleColor)
                                    Row(modifier = Modifier.weight(1f)) {
                                        days.forEach { day ->
                                            Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.2.dp, dividerColor.copy(alpha = 0.3f))) {
                                                val match = findClassSpecific(day, logicHour)
                                                if (match != null) {
                                                    ScheduleCardCompact(match, dark)
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
}

private fun findClassSpecific(day: String, hour: String): Horario? {
    return when {
        // Lunes y Miércoles: Cloud
        (day == "LUN" || day == "MIÉ") && (hour == "17:00" || hour == "17:30") -> Horario(materiaNombre = "Cloud", salon = "L1")
        
        // Martes y Jueves: IoT
        (day == "MAR" || day == "JUE") && (hour == "17:00" || hour == "17:30") -> Horario(materiaNombre = "IoT", salon = "L3")
        
        // Viernes: HORARIO COMPLETO (Estadía)
        day == "VIE" && (hour == "17:00" || hour == "17:30" || hour == "18:00" || hour == "18:30" || hour == "19:00" || hour == "19:30" || hour == "20:00" || hour == "20:30" || hour == "21:00" || hour == "21:30") -> 
            Horario(materiaNombre = "Estadía", salon = "Audit.")
        
        // Lunes y Martes: Tendencias
        day == "LUN" && (hour == "19:00" || hour == "19:30") -> Horario(materiaNombre = "Tenden.", salon = "S2")
        day == "MAR" && (hour == "20:00" || hour == "20:30") -> Horario(materiaNombre = "Tenden.", salon = "S2")
        
        else -> null
    }
}

@Composable
private fun ScheduleCardCompact(horario: Horario, isDark: Boolean) {
    val color = when {
        horario.materiaNombre?.contains("Cloud", true) == true -> if (isDark) Color(0xFF60A5FA) else Color(0xFF3B82F6)
        horario.materiaNombre?.contains("IoT", true) == true -> if (isDark) Color(0xFF34D399) else Color(0xFF10B981)
        else -> if (isDark) Color(0xFFA5B4FC) else Color(0xFF818CF8)
    }
    
    Surface(
        modifier = Modifier.fillMaxSize().padding(2.dp),
        color = color.copy(alpha = if (isDark) 0.25f else 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.width(3.dp).fillMaxHeight().background(color, RoundedCornerShape(3.dp)))
            Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center) {
                Text(text = horario.materiaNombre ?: "", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = color, maxLines = 1)
                Text(text = horario.salon ?: "", fontSize = 7.sp, fontWeight = FontWeight.Medium, color = color.copy(alpha = 0.8f), maxLines = 1)
            }
        }
    }
}
