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
fun KardexRequestScreen(
    onBack: () -> Unit = {},
    vm: StudentProceduresViewModel = viewModel(),
    onFinish: (Boolean) -> Unit = {}
) {
    var selectedReason by remember { mutableStateOf("Trámite Externo") }
    var expandedReason by remember { mutableStateOf(false) }
    var formaDigital by remember { mutableStateOf(true) }
    val isLoading by vm.loading.collectAsState()

    val reasonOptions = listOf("Trámite Externo", "Beca", "Empleo", "Personal")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FB)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
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
                            tint = Color(0xFF004696)
                        )
                    }
                    Text(
                        text = "Solicitar Kardex",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
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
                // Icon Section
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFFE8F0FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Color(0xFF0056D2),
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Kardex Oficial",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Este documento contiene tu historial académico completo con sello y firma digital, válido para trámites institucionales y externos.",
                    fontSize = 15.sp,
                    color = Color(0xFF74777F),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Info Card
                Text(
                    text = "INFORMACIÓN DEL TRÁMITE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.fillMaxWidth(),
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(36.dp).background(Color(0xFFF8F9FB), CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Payments, null, tint = Color(0xFF0056D2), modifier = Modifier.size(20.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Text("Costo", color = Color(0xFF74777F), fontSize = 15.sp)
                            }
                            Text("$150.00 MXN", fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E), fontSize = 16.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(36.dp).background(Color(0xFFF8F9FB), CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Schedule, null, tint = Color(0xFF0056D2), modifier = Modifier.size(20.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Text("Tiempo de entrega", color = Color(0xFF74777F), fontSize = 15.sp)
                            }
                            Text("2 días hábiles", fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E), fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Reason Selector
                Text(
                    text = "MOTIVO DE SOLICITUD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.fillMaxWidth(),
                    letterSpacing = 1.sp
                )
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
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedReason) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedReason,
                        onDismissRequest = { expandedReason = false }
                    ) {
                        reasonOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedReason = option
                                    expandedReason = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Delivery Options
                Text(
                    text = "FORMA DE ENTREGA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.fillMaxWidth(),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                DeliveryCard(
                    title = "Digital (PDF)",
                    subtitle = "Descarga inmediata del documento",
                    icon = Icons.Default.PictureAsPdf,
                    selected = formaDigital,
                    onClick = { formaDigital = true }
                )
                Spacer(Modifier.height(12.dp))
                DeliveryCard(
                    title = "Impreso en Ventanilla",
                    subtitle = "Recoger en Servicios Escolares",
                    icon = Icons.Default.Store,
                    selected = !formaDigital,
                    onClick = { formaDigital = false }
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Submit Button
                Button(
                    onClick = {
                        vm.requestKardex(selectedReason, formaDigital) { isDigital ->
                            onFinish(isDigital)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (formaDigital) "Descargar Kardex" else "Enviar Solicitud", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(10.dp))
                            Icon(imageVector = if (formaDigital) Icons.Default.Download else Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Al enviar la solicitud, aceptas los términos de procesamiento y el cargo correspondiente a tu cuenta.",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun DeliveryCard(title: String, subtitle: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = if (selected) Color(0xFF2563EB) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2563EB)))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1C1E))
                Text(subtitle, fontSize = 12.sp, color = Color(0xFF74777F))
            }
            Icon(imageVector = icon, null, tint = Color(0xFF0056D2), modifier = Modifier.size(24.dp))
        }
    }
}
