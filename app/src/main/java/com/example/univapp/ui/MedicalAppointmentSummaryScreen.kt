package com.example.univapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalAppointmentSummaryScreen(
    onBack: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onEdit: () -> Unit = {},
    vm: MedicalAppointmentViewModel,
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF111827)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val textColor = if (dark) Color(0xFFE2E8F0) else Color(0xFF1F2937)

    val reason by vm.reason.collectAsState()
    val date by vm.date.collectAsState()
    val time by vm.time.collectAsState()
    val service by vm.service.collectAsState()
    val priority by vm.priority.collectAsState()
    val location by vm.location.collectAsState()
    
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Observar errores y mostrarlos
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                // Opcional: limpiar error en VM después de mostrarlo si no tienes una función específica
                // Aunque es mejor llamar a vm.clearError() manualmente si fuera necesario.
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
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
                    text = "Resumen de Cita",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Indicator
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(width = 32.dp, height = 8.dp).background(Color(0xFF2563EB), CircleShape))
                Box(modifier = Modifier.size(width = 48.dp, height = 8.dp).background(Color(0xFF2563EB), CircleShape))
                Box(modifier = Modifier.size(width = 32.dp, height = 8.dp).background(Color(0xFF2563EB), CircleShape))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "DETALLES DE LA CITA",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = subtitleColor,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    SummaryRow(icon = Icons.Default.MedicalServices, label = "SERVICIO", value = service, dark = dark, textColor = textColor)
                    SummaryRow(icon = Icons.Default.Description, label = "MOTIVO", value = reason.ifBlank { "No especificado" }, dark = dark, textColor = textColor)
                    SummaryRow(
                        icon = Icons.Default.PriorityHigh,
                        label = "PRIORIDAD",
                        value = priority,
                        dark = dark,
                        textColor = textColor,
                        extraContent = {
                            val isUrgent = priority == "Urgente"
                            val badgeBg = if (isUrgent) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                            val badgeText = if (isUrgent) Color(0xFF991B1B) else Color(0xFF166534)
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .background(badgeBg, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(if (isUrgent) "ALTA" else "BAJA", color = badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    SummaryRow(icon = Icons.Default.CalendarToday, label = "FECHA", value = date, dark = dark, textColor = textColor)
                    SummaryRow(icon = Icons.Default.AccessTime, label = "HORA", value = time, dark = dark, textColor = textColor)
                    SummaryRow(icon = Icons.Default.LocationOn, label = "LUGAR", value = location, dark = dark, textColor = textColor)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Map Placeholder
            Image(
                painter = painterResource(id = R.drawable.logo_3_este_si),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(70.dp))
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Confirm Button
            Button(
                onClick = { vm.confirmAppointment(onConfirm) },
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Confirmar Cita", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Edit Button
            OutlinedButton(
                onClick = onEdit,
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                border = null,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = titleColor)
            ) {
                Text("Editar detalles", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Al confirmar, recibirás una notificación de recordatorio 15 minutos antes de tu cita.",
                fontSize = 12.sp,
                color = subtitleColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun SummaryRow(
    icon: ImageVector,
    label: String,
    value: String,
    dark: Boolean,
    textColor: Color,
    extraContent: @Composable (() -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(if (dark) Color(0xFF1E293B) else Color(0xFFEFF6FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = if (dark) Color(0xFF94A3B8) else Color(0xFF9CA3AF), fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                extraContent?.invoke()
            }
        }
    }
}
