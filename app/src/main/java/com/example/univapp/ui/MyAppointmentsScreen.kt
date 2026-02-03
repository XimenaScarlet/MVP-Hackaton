package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.MedicalAppointment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen(
    onBack: () -> Unit = {},
    medicalVm: MedicalAppointmentViewModel,
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    val appointments by medicalVm.userAppointments.collectAsState()

    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF111827)
    val textColor = if (dark) Color(0xFFE2E8F0) else Color(0xFF1F2937)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Citas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bgColor,
                    titleContentColor = titleColor,
                    navigationIconContentColor = titleColor
                )
            )
        },
        containerColor = bgColor
    ) { padding ->
        if (appointments.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.EventBusy, null, modifier = Modifier.size(64.dp), tint = subtitleColor)
                    Spacer(Modifier.height(16.dp))
                    Text("No tienes citas agendadas", color = subtitleColor, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(appointments) { appt ->
                    AppointmentCard(appt, cardBg, textColor, subtitleColor, dark)
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appt: MedicalAppointment,
    bgColor: Color,
    textColor: Color,
    subtitleColor: Color,
    dark: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFF2563EB).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (appt.service.contains("Psic")) Icons.Default.Psychology else Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color(0xFF2563EB)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(appt.service, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                    Text("Folio: ${appt.id.take(8).uppercase()}", fontSize = 12.sp, color = subtitleColor)
                }
                StatusBadge(appt.status)
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = subtitleColor.copy(alpha = 0.2f))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow(Icons.Default.Description, appt.reason, textColor)
                InfoRow(Icons.Default.CalendarToday, appt.date, textColor)
                InfoRow(Icons.Default.AccessTime, appt.time, textColor)
                InfoRow(Icons.Default.LocationOn, appt.location, textColor)
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = color.copy(alpha = 0.6f))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = color)
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when(status) {
        "CONFIRMADA" -> Color(0xFF10B981)
        "CANCELADA" -> Color(0xFFEF4444)
        else -> Color(0xFFF59E0B)
    }
    Box(
        modifier = Modifier.background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(status, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
