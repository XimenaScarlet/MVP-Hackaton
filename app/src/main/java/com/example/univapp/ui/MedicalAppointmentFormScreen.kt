package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalAppointmentFormScreen(
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    vm: MedicalAppointmentViewModel,
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    val service by vm.service.collectAsState()
    val isPsicologia = service == "Psicología"
    
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF111827)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val inputBg = if (dark) Color(0xFF334155) else Color.White

    // --- ESTADOS PSICOLOGÍA ---
    var tipoAtencion by remember { mutableStateOf("Primera vez") }
    var motivoPsiSeleccionado by remember { mutableStateOf("Selecciona un motivo") }
    var expandedMotivo by remember { mutableStateOf(false) }
    var modalidad by remember { mutableStateOf("Presencial") }
    var notaPsi by remember { mutableStateOf("") }

    // --- ESTADOS MÉDICO GENERAL (RESTAURADOS) ---
    val motivoMed by vm.reason.collectAsState()
    var urgenciaMedNormal by remember { mutableStateOf(true) }
    var tieneAlergia by remember { mutableStateOf(false) }
    var alergiaTexto by remember { mutableStateOf("") }
    var tomaMed by remember { mutableStateOf(false) }
    var medTexto by remember { mutableStateOf("") }

    val motivosPsicologia = listOf(
        "Estrés académico", "Ansiedad", "Depresión / estado de ánimo",
        "Problemas personales / familiares", "Orientación emocional", "Otro"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(top = 20.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Atrás", modifier = Modifier.size(28.dp), tint = titleColor)
                }
                Text(text = if(isPsicologia) "Cita Psicológica" else "Cita Médica", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isPsicologia) {
                /* ================= FORMULARIO PSICOLOGÍA ================= */
                FormSectionTitle("1. Tipo de atención", titleColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SelectionButton(text = "Primera vez", selected = tipoAtencion == "Primera vez", dark = dark, modifier = Modifier.weight(1f)) { tipoAtencion = "Primera vez" }
                    SelectionButton(text = "Seguimiento", selected = tipoAtencion == "Seguimiento", dark = dark, modifier = Modifier.weight(1f)) { tipoAtencion = "Seguimiento" }
                }

                Spacer(modifier = Modifier.height(24.dp))

                FormSectionTitle("2. Motivo de consulta", titleColor)
                ExposedDropdownMenuBox(expanded = expandedMotivo, onExpandedChange = { expandedMotivo = it }, modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = motivoPsiSeleccionado, onValueChange = {}, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMotivo) },
                        colors = TextFieldDefaults.colors(focusedContainerColor = inputBg, unfocusedContainerColor = inputBg, focusedTextColor = titleColor, unfocusedTextColor = titleColor, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expandedMotivo, onDismissRequest = { expandedMotivo = false }, modifier = Modifier.background(cardBg)) {
                        motivosPsicologia.forEach { option ->
                            DropdownMenuItem(text = { Text(option, color = titleColor) }, onClick = { motivoPsiSeleccionado = option; expandedMotivo = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                FormSectionTitle("3. Modalidad", titleColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SelectionButton(text = "Presencial", selected = modalidad == "Presencial", dark = dark, modifier = Modifier.weight(1f)) { modalidad = "Presencial" }
                    SelectionButton(text = "En línea", selected = modalidad == "En línea", dark = dark, modifier = Modifier.weight(1f)) { modalidad = "En línea" }
                }

                Spacer(modifier = Modifier.height(24.dp))

                FormSectionTitle("4. Nota opcional", titleColor)
                TextField(
                    value = notaPsi, onValueChange = { notaPsi = it },
                    placeholder = { Text("¿Algo que quieras que el psicólogo sepa antes?", color = subtitleColor.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(focusedContainerColor = inputBg, unfocusedContainerColor = inputBg, focusedTextColor = titleColor, unfocusedTextColor = titleColor, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )

            } else {
                /* ================= FORMULARIO MÉDICO (RESTAURADO) ================= */
                FormSectionTitle("Motivo de la consulta", titleColor)
                TextField(
                    value = motivoMed, onValueChange = { vm.reason.value = it },
                    placeholder = { Text("Ej: Dolor de cabeza o Revisión", color = subtitleColor.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(focusedContainerColor = inputBg, unfocusedContainerColor = inputBg, focusedTextColor = titleColor, unfocusedTextColor = titleColor, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )

                Spacer(modifier = Modifier.height(24.dp))

                FormSectionTitle("Nivel de urgencia", titleColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    UrgencyCard(title = "Normal", selected = urgenciaMedNormal, icon = Icons.Default.CheckCircle, iconColor = Color(0xFF3B82F6), cardBg = cardBg, textColor = titleColor, modifier = Modifier.weight(1f)) { urgenciaMedNormal = true; vm.priority.value = "Normal" }
                    UrgencyCard(title = "Urgente", selected = !urgenciaMedNormal, icon = Icons.Default.Error, iconColor = Color(0xFFEF4444), cardBg = cardBg, textColor = titleColor, modifier = Modifier.weight(1f)) { urgenciaMedNormal = false; vm.priority.value = "Urgente" }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Alergias
                FormSectionTitle("¿Tienes alguna alergia?", titleColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SelectionButton(text = "No", selected = !tieneAlergia, dark = dark, modifier = Modifier.weight(1f)) { tieneAlergia = false; alergiaTexto = "" }
                    SelectionButton(text = "Sí", selected = tieneAlergia, dark = dark, modifier = Modifier.weight(1f)) { tieneAlergia = true }
                }
                if (tieneAlergia) {
                    Spacer(Modifier.height(8.dp))
                    TextField(value = alergiaTexto, onValueChange = { alergiaTexto = it }, placeholder = { Text("¿A qué eres alérgico?") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = inputBg, unfocusedContainerColor = inputBg, focusedTextColor = titleColor, unfocusedTextColor = titleColor))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Medicamentos
                FormSectionTitle("¿Tomas algún medicamento?", titleColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SelectionButton(text = "No", selected = !tomaMed, dark = dark, modifier = Modifier.weight(1f)) { tomaMed = false; medTexto = "" }
                    SelectionButton(text = "Sí", selected = tomaMed, dark = dark, modifier = Modifier.weight(1f)) { tomaMed = true }
                }
                if (tomaMed) {
                    Spacer(Modifier.height(8.dp))
                    TextField(value = medTexto, onValueChange = { medTexto = it }, placeholder = { Text("¿Qué medicamentos?") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = inputBg, unfocusedContainerColor = inputBg, focusedTextColor = titleColor, unfocusedTextColor = titleColor))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (isPsicologia) {
                        vm.reason.value = "[$tipoAtencion] $motivoPsiSeleccionado. Modalidad: $modalidad. Nota: $notaPsi"
                        vm.priority.value = "Normal"
                    } else {
                        val alergiasInfo = if(tieneAlergia) " Alergias: $alergiaTexto." else ""
                        val medInfo = if(tomaMed) " Medicamentos: $medTexto." else ""
                        vm.reason.value = vm.reason.value + alergiasInfo + medInfo
                    }
                    onContinue()
                },
                enabled = if(isPsicologia) motivoPsiSeleccionado != "Selecciona un motivo" else motivoMed.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Continuar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FormSectionTitle(text: String, color: Color) {
    Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
}

@Composable
fun UrgencyCard(title: String, selected: Boolean, icon: ImageVector, iconColor: Color, cardBg: Color, textColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(modifier = modifier.height(90.dp).clickable { onClick() }.border(width = if (selected) 2.dp else 0.dp, color = if (selected) Color(0xFF2563EB) else Color.Transparent, shape = RoundedCornerShape(16.dp)), shape = RoundedCornerShape(16.dp), color = cardBg, shadowElevation = 2.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, fontSize = 14.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, color = if (selected) Color(0xFF2563EB) else textColor)
        }
    }
}

@Composable
fun SelectionButton(text: String, selected: Boolean, dark: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val accentColor = Color(0xFF2563EB)
    val borderColor = if (selected) accentColor else if (dark) Color(0xFF334155) else Color(0xFFE5E7EB)
    val containerColor = if (selected) accentColor.copy(alpha = 0.1f) else if (dark) Color(0xFF1E293B) else Color.White
    val contentColor = if (selected) accentColor else if (dark) Color.White else Color(0xFF6B7280)
    Surface(modifier = modifier.height(48.dp).clickable { onClick() }.border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(24.dp)), shape = RoundedCornerShape(24.dp), color = containerColor) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text = text, fontSize = 14.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, color = contentColor)
        }
    }
}
