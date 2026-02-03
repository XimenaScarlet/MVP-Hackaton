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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IDReplacementScreen(
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

    var selectedReason by remember { mutableStateOf("Selecciona una opción") }
    var expandedReason by remember { mutableStateOf(false) }
    var reasonDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("2026-01-29") } // Fecha por defecto para el demo
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val reasonOptions = listOf("Extravío", "Robo", "Deterioro", "Actualización de datos")

    if (showConfirmationDialog) {
        ConfirmationDialog(
            onDismiss = { showConfirmationDialog = false },
            onConfirm = {
                showConfirmationDialog = false
                vm.requestIDReplacement(
                    reason = selectedReason,
                    description = reasonDescription,
                    date = selectedDate,
                    onSuccess = onFinish
                )
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
                color = cardBg,
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Atrás", modifier = Modifier.size(32.dp), tint = if (dark) Color.White else Color(0xFF004696))
                    }
                    Text("Reposición de Credencial", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = titleColor)
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Motivo de la reposición", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = titleColor)
                Spacer(modifier = Modifier.height(12.dp))
                
                ExposedDropdownMenuBox(
                    expanded = expandedReason,
                    onExpandedChange = { expandedReason = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = selectedReason,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null, tint = subtitleColor) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            focusedTextColor = titleColor,
                            unfocusedTextColor = titleColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
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

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = reasonDescription,
                    onValueChange = { reasonDescription = it },
                    placeholder = { Text("Describe brevemente el motivo", color = subtitleColor) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedTextColor = titleColor,
                        unfocusedTextColor = titleColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Info Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = if (dark) Color(0xFF1E3A8A).copy(alpha = 0.3f) else Color(0xFFEFF6FF)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Información del trámite", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (dark) Color.White else Color(0xFF1E3A8A))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Costo: $150 MXN.", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (dark) Color.White else Color(0xFF1E3A8A))
                            Text("El pago y la foto se realizan en ventanilla de servicios escolares. Es necesario presentarse con identificación oficial.", fontSize = 14.sp, color = if (dark) Color(0xFFE2E8F0) else Color(0xFF1E3A8A), lineHeight = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Fecha sugerida para acudir", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = titleColor)
                Spacer(modifier = Modifier.height(12.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth().height(56.dp).clickable { /* DatePicker logic */ },
                    shape = RoundedCornerShape(16.dp),
                    color = cardBg
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(selectedDate, color = titleColor, fontSize = 16.sp)
                        Icon(Icons.Default.CalendarToday, null, tint = subtitleColor, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { 
                        if (selectedReason != "Selecciona una opción") {
                            showConfirmationDialog = true 
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Agendar reposición", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(72.dp).background(Color(0xFFEEF2FF), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF2563EB), modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("¿Confirmar cita?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text("¿Confirmas tu cita para reposición de credencial? Recuerda que el pago y la foto se realizan presencialmente.", fontSize = 15.sp, textAlign = TextAlign.Center, lineHeight = 22.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))) {
                    Text("Confirmar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
