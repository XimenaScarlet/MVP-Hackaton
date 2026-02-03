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
import androidx.compose.material.icons.automirrored.filled.Send
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
fun StudentEnrollmentFormScreen(
    onBack: () -> Unit = {},
    vm: StudentProceduresViewModel = viewModel(),
    onSubmit: (Boolean) -> Unit = {},
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    
    // Dynamic Colors
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF8F9FB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF74777F)
    val inputBg = if (dark) Color(0xFF334155) else Color.White
    val accentColor = if (dark) Color(0xFF60A5FA) else Color(0xFF2563EB)

    var tipoSimple by remember { mutableStateOf(true) }
    var selectedUsage by remember { mutableStateOf("Beca") }
    var formaDigital by remember { mutableStateOf(true) }
    var expandedUsage by remember { mutableStateOf(false) }
    val isLoading by vm.loading.collectAsState()

    val usageOptions = listOf("Beca", "Trámite Externo", "Seguro Médico", "Personal")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (dark) Color(0xFF1E293B) else Color.White,
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Atrás",
                            modifier = Modifier.size(32.dp),
                            tint = titleColor
                        )
                    }
                    Text(
                        text = "Formulario de Solicitud",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Constancia de estudios",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Text(
                    text = "Completa los detalles para tu solicitud oficial.",
                    fontSize = 14.sp,
                    color = subtitleColor,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Tipo de constancia
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Tipo de constancia", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (dark) Color.White else Color(0xFF44474E))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TypeButton(text = "Simple", selected = tipoSimple, dark = dark, modifier = Modifier.weight(1f)) { tipoSimple = true }
                        TypeButton(text = "Con calificaciones", selected = !tipoSimple, dark = dark, modifier = Modifier.weight(1f)) { tipoSimple = false }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Uso del documento
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Uso del documento", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (dark) Color.White else Color(0xFF44474E))
                    Spacer(modifier = Modifier.height(12.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedUsage,
                        onExpandedChange = { expandedUsage = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = selectedUsage,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUsage) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = inputBg,
                                unfocusedContainerColor = inputBg,
                                focusedTextColor = titleColor,
                                unfocusedTextColor = titleColor,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedUsage,
                            onDismissRequest = { expandedUsage = false },
                            modifier = Modifier.background(cardBg)
                        ) {
                            usageOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = titleColor) },
                                    onClick = {
                                        selectedUsage = option
                                        expandedUsage = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Forma de entrega
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Forma de entrega", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (dark) Color.White else Color(0xFF44474E))
                    Spacer(modifier = Modifier.height(12.dp))
                    DeliveryOption(
                        title = "Digital (PDF)",
                        subtitle = "Descarga inmediata del documento",
                        icon = Icons.Default.PictureAsPdf,
                        selected = formaDigital,
                        dark = dark,
                        onClick = { formaDigital = true }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    DeliveryOption(
                        title = "Presencial",
                        subtitle = "Recolección en ventanilla de servicios",
                        icon = Icons.Default.Person,
                        selected = !formaDigital,
                        dark = dark,
                        onClick = { formaDigital = false }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(40.dp))

                // Botón Enviar
                Button(
                    onClick = {
                        vm.requestCertificate(tipoSimple, selectedUsage, formaDigital) { isDigital ->
                            onSubmit(isDigital)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = accentColor),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (formaDigital) "Descargar PDF" else "Enviar solicitud", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(10.dp))
                            Icon(imageVector = if (formaDigital) Icons.Default.Download else Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (formaDigital) "El archivo se guardará en tu carpeta de descargas." else "Al enviar, confirmas que los datos son correctos. El tiempo de respuesta es de 2 a 3 días hábiles.",
                    fontSize = 12.sp,
                    color = subtitleColor,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun TypeButton(text: String, selected: Boolean, dark: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val accentColor = if (dark) Color(0xFF60A5FA) else Color(0xFF2563EB)
    val borderColor = if (selected) accentColor else if (dark) Color(0xFF334155) else Color(0xFFE5E7EB)
    val containerColor = if (selected) accentColor.copy(alpha = 0.15f) else if (dark) Color(0xFF1E293B) else Color.White
    val contentColor = if (selected) accentColor else if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)

    Surface(
        modifier = modifier
            .height(50.dp)
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

@Composable
private fun DeliveryOption(title: String, subtitle: String, icon: ImageVector, selected: Boolean, dark: Boolean, onClick: () -> Unit) {
    val accentColor = if (dark) Color(0xFF60A5FA) else Color(0xFF2563EB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF74777F)
    val iconBoxBg = if (dark) Color(0xFF334155) else Color(0xFFF8F9FB)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = if (selected) accentColor else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBoxBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = titleColor)
                Text(subtitle, fontSize = 12.sp, color = subtitleColor)
            }
            RadioButton(
                selected = selected, 
                onClick = onClick, 
                colors = RadioButtonDefaults.colors(selectedColor = accentColor, unselectedColor = if (dark) Color(0xFF475569) else Color(0xFFD1D5DB))
            )
        }
    }
}
