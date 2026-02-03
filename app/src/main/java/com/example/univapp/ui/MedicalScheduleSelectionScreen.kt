package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalScheduleSelectionScreen(
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    vm: MedicalAppointmentViewModel,
    healthVm: HealthViewModel = viewModel(),
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    val availableTimes by healthVm.availableTimes.collectAsState()
    val isLoading by healthVm.loading.collectAsState()

    val bgColor = if (dark) Color(0xFF0F172A) else Color.White
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF111827)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val timeSlotBg = if (dark) Color(0xFF334155) else Color(0xFFF3F4F6)

    var currentMonthCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDateLocal by remember { mutableStateOf(Calendar.getInstance()) }
    val selectedTime by vm.time.collectAsState()

    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "MX"))
    val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(selectedDateLocal) {
        healthVm.loadAvailableSlots(selectedDateLocal.time, vm.service.value)
        vm.time.value = "" // Reset time when date changes
        vm.date.value = apiDateFormat.format(selectedDateLocal.time)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Atrás",
                        modifier = Modifier.size(28.dp),
                        tint = titleColor
                    )
                }
                Text(
                    text = "Seleccionar Horario",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Indicator
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(width = 24.dp, height = 8.dp).background(Color(0xFF2563EB), CircleShape))
                Box(modifier = Modifier.size(width = 24.dp, height = 8.dp).background(Color(0xFF2563EB), CircleShape))
                Box(modifier = Modifier.size(8.dp).background(if(dark) Color(0xFF334155) else Color(0xFFE5E7EB), CircleShape))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Calendar Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val newCal = currentMonthCalendar.clone() as Calendar
                            newCal.add(Calendar.MONTH, -1)
                            currentMonthCalendar = newCal
                        }) { Icon(Icons.Default.KeyboardArrowLeft, null, tint = titleColor) }
                        
                        Text(
                            text = monthYearFormat.format(currentMonthCalendar.time).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = titleColor
                        )
                        
                        IconButton(onClick = {
                            val newCal = currentMonthCalendar.clone() as Calendar
                            newCal.add(Calendar.MONTH, 1)
                            currentMonthCalendar = newCal
                        }) { Icon(Icons.Default.KeyboardArrowRight, null, tint = titleColor) }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        listOf("L", "M", "M", "J", "V", "S", "D").forEach { day ->
                            Text(day, fontSize = 11.sp, color = subtitleColor, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val today = Calendar.getInstance()
                    today.set(Calendar.HOUR_OF_DAY, 0)
                    today.set(Calendar.MINUTE, 0)
                    today.set(Calendar.SECOND, 0)
                    today.set(Calendar.MILLISECOND, 0)

                    val daysInMonth = currentMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val firstDayOfMonth = currentMonthCalendar.clone() as Calendar
                    firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
                    var dayOfWeekOffset = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 2 // Adjust for Monday start
                    if (dayOfWeekOffset < 0) dayOfWeekOffset = 6

                    Column {
                        var dayCounter = 1
                        for (row in 0..5) {
                            if (dayCounter > daysInMonth) break
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                for (col in 0..6) {
                                    if ((row == 0 && col < dayOfWeekOffset) || dayCounter > daysInMonth) {
                                        Box(modifier = Modifier.size(36.dp))
                                    } else {
                                        val dayToRender = dayCounter
                                        val dateToCompare = currentMonthCalendar.clone() as Calendar
                                        dateToCompare.set(Calendar.DAY_OF_MONTH, dayToRender)
                                        dateToCompare.set(Calendar.HOUR_OF_DAY, 0)
                                        dateToCompare.set(Calendar.MINUTE, 0)
                                        dateToCompare.set(Calendar.SECOND, 0)
                                        dateToCompare.set(Calendar.MILLISECOND, 0)

                                        val isSelected = selectedDateLocal.get(Calendar.YEAR) == dateToCompare.get(Calendar.YEAR) &&
                                                selectedDateLocal.get(Calendar.DAY_OF_YEAR) == dateToCompare.get(Calendar.DAY_OF_YEAR)
                                        
                                        val isPast = dateToCompare.before(today)
                                        val isWeekend = col >= 5

                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color(0xFF2563EB) else Color.Transparent)
                                                .clickable(enabled = !isPast && !isWeekend) {
                                                    val newSelected = currentMonthCalendar.clone() as Calendar
                                                    newSelected.set(Calendar.DAY_OF_MONTH, dayToRender)
                                                    selectedDateLocal = newSelected
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = dayToRender.toString(),
                                                color = when {
                                                    isSelected -> Color.White
                                                    isPast || isWeekend -> if (dark) Color(0xFF475569) else Color(0xFFD1D5DB)
                                                    else -> titleColor
                                                },
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 14.sp
                                            )
                                        }
                                        dayCounter++
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Horas Disponibles", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor)
                val dateFmt = SimpleDateFormat("d 'de' MMMM", Locale("es", "MX"))
                Text(
                    text = "Citas para el ${dateFmt.format(selectedDateLocal.time)}", 
                    fontSize = 14.sp, 
                    color = subtitleColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2563EB))
                }
            } else if (availableTimes.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("No hay horarios disponibles para este día.", color = subtitleColor, textAlign = TextAlign.Center)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(availableTimes) { time ->
                        val isSelected = selectedTime == time
                        Surface(
                            modifier = Modifier
                                .height(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { vm.time.value = time },
                            color = if (isSelected) Color(0xFF2563EB) else timeSlotBg,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = time,
                                    color = if (isSelected) Color.White else titleColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Continue Button
            Button(
                onClick = onContinue,
                enabled = selectedTime.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Continuar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
