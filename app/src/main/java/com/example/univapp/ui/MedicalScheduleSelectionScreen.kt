package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.univapp.ui.util.AppScaffold
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalScheduleSelectionScreen(
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    vm: MedicalAppointmentViewModel,
    healthVm: HealthViewModel = hiltViewModel(),
    settingsVm: SettingsViewModel = hiltViewModel()
) {
    val availableTimes by healthVm.availableTimes.collectAsState()
    val isLoading by healthVm.loading.collectAsState()

    val darkBg = Color(0xFF0B101F)
    val cardBg = Color(0xFF131A2C)
    val accentColor = Color(0xFF818CF8)
    val subtitleColor = Color(0xFF94A3B8)

    var currentMonthCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDateLocal by remember { mutableStateOf(Calendar.getInstance()) }
    val selectedTime by vm.time.collectAsState()

    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "MX"))
    val dayNameFormat = SimpleDateFormat("EEEE, d 'de' MMM", Locale("es", "MX"))
    val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(selectedDateLocal) {
        healthVm.loadAvailableSlots(selectedDateLocal.time, vm.service.value)
        vm.time.value = "" 
        vm.date.value = apiDateFormat.format(selectedDateLocal.time)
    }

    AppScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Seleccionar Horario", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = darkBg)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(darkBg).padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "CALENDARIO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC084FC),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date Header with Nav
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthYearFormat.format(currentMonthCalendar.time).replaceFirstChar { it.uppercase() },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.05f),
                            onClick = {
                                val newCal = currentMonthCalendar.clone() as Calendar
                                newCal.add(Calendar.MONTH, -1)
                                currentMonthCalendar = newCal
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.05f),
                            onClick = {
                                val newCal = currentMonthCalendar.clone() as Calendar
                                newCal.add(Calendar.MONTH, 1)
                                currentMonthCalendar = newCal
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Horizontal Week Selector
                HorizontalCalendar(
                    selectedDate = selectedDateLocal,
                    onDateSelected = { selectedDateLocal = it },
                    accentColor = accentColor
                )

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("Horas Disponibles", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        dayNameFormat.format(selectedDateLocal.time).replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        color = subtitleColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accentColor)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(availableTimes) { time ->
                            val isSelected = selectedTime == time
                            val status = when(time) {
                                "11:00 AM" -> "Casi Lleno"
                                "02:00 PM" -> "Ocupado"
                                else -> "Disponible"
                            }
                            
                            NewScheduleItem(
                                time = time,
                                status = status,
                                isSelected = isSelected,
                                spaces = when(status) {
                                    "Casi Lleno" -> 2
                                    "Ocupado" -> 0
                                    else -> 8
                                },
                                onClick = { if (status != "Ocupado") vm.time.value = time }
                            )
                        }
                    }
                }
            }

            // Bottom Confirm Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, darkBg.copy(alpha = 0.9f))))
                    .padding(24.dp)
            ) {
                Button(
                    onClick = onContinue,
                    enabled = selectedTime.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Confirmar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun HorizontalCalendar(
    selectedDate: Calendar,
    onDateSelected: (Calendar) -> Unit,
    accentColor: Color
) {
    val days = remember {
        val list = mutableListOf<Calendar>()
        val cal = Calendar.getInstance()
        // Start from Monday of the current week
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        for (i in 0..13) { // Show 2 weeks
            list.add(cal.clone() as Calendar)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val dayFormat = SimpleDateFormat("EEE", Locale("es", "MX"))

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(days) { date ->
            val isSelected = selectedDate.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                    selectedDate.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width(60.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) accentColor else Color.Transparent)
                    .clickable { onDateSelected(date) }
            ) {
                Text(
                    text = dayFormat.format(date.time).uppercase(),
                    fontSize = 11.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = date.get(Calendar.DAY_OF_MONTH).toString(),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun NewScheduleItem(
    time: String,
    status: String,
    isSelected: Boolean,
    spaces: Int,
    onClick: () -> Unit
) {
    val accentColor = Color(0xFF818CF8)
    val isOccupied = status == "Ocupado"
    val statusColor = when(status) {
        "Casi Lleno" -> Color(0xFFC084FC)
        "Ocupado" -> Color(0xFF475569)
        else -> Color(0xFF38BDF8)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isOccupied) { onClick() },
        color = if (isOccupied) Color(0xFF131A2C).copy(alpha = 0.5f) else Color(0xFF131A2C),
        shape = RoundedCornerShape(24.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, accentColor) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Icon Box
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.05f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when(status) {
                            "Casi Lleno" -> Icons.Default.ElectricBolt
                            "Ocupado" -> Icons.Default.Block
                            else -> Icons.Default.Schedule
                        },
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                val endTime = when(time) {
                    "09:00 AM" -> "10:30"
                    "11:00 AM" -> "12:30"
                    "02:00 PM" -> "03:30"
                    "04:00 PM" -> "05:30"
                    else -> "Fin"
                }
                Text(
                    text = "${time.substringBefore(" ")} - $endTime",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOccupied) Color(0xFF475569) else Color.White
                )
                Text(
                    text = status,
                    fontSize = 13.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
            }

            // Right Spaces Column
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$spaces espacios",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Progress Bar
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (isOccupied) 0f else spaces / 15f)
                            .fillMaxHeight()
                            .background(statusColor)
                    )
                }
            }
        }
    }
}
