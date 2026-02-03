package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BajaTemporalScreen(
    onBack: () -> Unit = {},
    onFinish: () -> Unit = {},
    vm: StudentProceduresViewModel = viewModel(),
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    val isLoading by vm.loading.collectAsState()

    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF8F9FB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val textColor = if (dark) Color(0xFFE2E8F0) else Color(0xFF1F2937)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF74777F)

    var selectedReason by remember { mutableStateOf("Salud, Económico, Personal...") }
    var expandedReason by remember { mutableStateOf(false) }
    var reasonDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("2026-02-05") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val reasonOptions = listOf("Salud", "Económico", "Personal", "Cambio de residencia")

    if (showConfirmDialog) {
        BajaTemporalConfirmDialog(
            onDismiss = { showConfirmDialog = false },
            onConfirm = {
                showConfirmDialog = false
                vm.requestBajaTemporal(selectedReason, selectedDate, onFinish)
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = cardBg
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Atrás", modifier = Modifier.size(32.dp), tint = if (dark) Color.White else Color(0xFF004696))
                    }
                    Text("Baja Temporal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = titleColor)
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Motivo de la baja", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = titleColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Selecciona una opción", fontSize = 14.sp, color = subtitleColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedReason,
                            onExpandedChange = { expandedReason = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = selectedReason,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedReason) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = bgColor,
                                    unfocusedContainerColor = bgColor,
                                    focusedTextColor = titleColor,
                                    unfocusedTextColor = titleColor,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedReason,
                                onDismissRequest = { expandedReason = false },
                                modifier = Modifier.background(cardBg)
                            ) {
                                reasonOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = titleColor) },
                                        onClick = {
                                            selectedReason = option
                                            expandedReason = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text("Describe el motivo", fontSize = 14.sp, color = subtitleColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = reasonDescription,
                            onValueChange = { reasonDescription = it },
                            placeholder = { Text("Proporciona más detalles sobre tu solicitud...", color = subtitleColor.copy(alpha = 0.6f)) },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = bgColor,
                                unfocusedContainerColor = bgColor,
                                focusedTextColor = titleColor,
                                unfocusedTextColor = titleColor,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Fecha de cita", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = titleColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Selecciona el día para acudir a Servicios Escolares", fontSize = 14.sp, color = subtitleColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().clickable { },
                            trailingIcon = { Icon(Icons.Default.CalendarToday, null, tint = subtitleColor) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = subtitleColor.copy(alpha = 0.3f),
                                focusedTextColor = titleColor,
                                unfocusedTextColor = titleColor
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = if (dark) Color(0xFF1E3A8A).copy(alpha = 0.3f) else Color(0xFFEFF6FF)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Información importante", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (dark) Color.White else Color(0xFF1E3A8A))
                        }
                        InfoItem(Icons.Default.LocationOn, "Este trámite se realiza presencialmente en el campus.", dark)
                        InfoItem(Icons.Default.Badge, "Presenta identificación oficial y credencial universitaria vigente.", dark)
                        InfoItem(Icons.Default.Schedule, "Tiempo estimado de respuesta: 3 a 5 días hábiles.", dark)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, null)
                            Spacer(Modifier.width(12.dp))
                            Text("Agendar cita para baja temporal", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItem(icon: ImageVector, text: String, dark: Boolean) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp).padding(top = 2.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 14.sp, color = if (dark) Color(0xFFE2E8F0) else Color(0xFF1E3A8A), lineHeight = 20.sp)
    }
}

@Composable
fun BajaTemporalConfirmDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(64.dp).background(Color(0xFFEEF2FF), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.EventNote, null, tint = Color(0xFF2563EB), modifier = Modifier.size(32.dp))
                }
                Spacer(Modifier.height(20.dp))
                Text("¿Confirmar cita?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text("¿Confirmas tu cita para solicitar baja temporal? Recuerda que el trámite se realiza presencialmente en Servicios Escolares.", fontSize = 15.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))
                Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))) {
                    Text("Confirmar cita", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar")
                }
            }
        }
    }
}
