package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternshipCertificateScreen(
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

    var companyName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var directedTo by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        InternshipConfirmDialog(
            onDismiss = { showConfirmDialog = false },
            onConfirm = {
                showConfirmDialog = false
                vm.requestInternshipCertificate(companyName, city, directedTo, onFinish)
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
                    Text("Constancia para Prácticas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = titleColor)
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
            ) {
                Text("Datos de la empresa", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor)

                Spacer(modifier = Modifier.height(20.dp))

                Text("Nombre de la empresa (Requerido)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = subtitleColor)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    placeholder = { Text("Ingresa el nombre de la empresa", color = subtitleColor.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedTextColor = titleColor,
                        unfocusedTextColor = titleColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Ciudad (Opcional)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = subtitleColor)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = city,
                    onValueChange = { city = it },
                    placeholder = { Text("Ingresa la ciudad", color = subtitleColor.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedTextColor = titleColor,
                        unfocusedTextColor = titleColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("A quién va dirigida", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor)

                Spacer(modifier = Modifier.height(20.dp))

                Text("Nombre del responsable o departamento", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = subtitleColor)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = directedTo,
                    onValueChange = { directedTo = it },
                    placeholder = { Text("Ej. Recursos Humanos, Lic. Juan Pérez, etc", color = subtitleColor.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedTextColor = titleColor,
                        unfocusedTextColor = titleColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Info Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = if (dark) Color(0xFF1E3A8A).copy(alpha = 0.3f) else Color(0xFFEEF2FF)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Este documento se entrega firmado y sellado por la institución.", fontSize = 14.sp, color = if (dark) Color.White else Color(0xFF1E3A8A))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Tiempo estimado: 3 a 5 días hábiles.", fontSize = 14.sp, color = if (dark) Color.White else Color(0xFF1E3A8A))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payments, null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Costo: Sin costo.", fontSize = 14.sp, color = if (dark) Color.White else Color(0xFF1E3A8A))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { if (companyName.isNotBlank()) showConfirmDialog = true },
                    enabled = !isLoading && companyName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Solicitar constancia", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun InternshipConfirmDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(64.dp).background(Color(0xFFEEF2FF), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Description, null, tint = Color(0xFF2563EB), modifier = Modifier.size(32.dp))
                }
                Spacer(Modifier.height(20.dp))
                Text("¿Confirmar solicitud?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text("¿Confirmas solicitar la constancia para prácticas? Podrás recogerla en Servicios Escolares.", fontSize = 15.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))
                Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))) {
                    Text("Confirmar", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar")
                }
            }
        }
    }
}
