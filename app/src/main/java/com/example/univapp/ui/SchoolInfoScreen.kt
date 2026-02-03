package com.example.univapp.ui

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SchoolInfoScreen(
    onBack: () -> Unit = {},
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()

    // Dynamic Colors
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF8F9FB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val textColor = if (dark) Color(0xFFE2E8F0) else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF74777F)
    val iconBoxBg = if (dark) Color(0xFF334155) else Color(0xFFE8F0FE)
    val iconTintColor = if (dark) Color(0xFF60A5FA) else Color(0xFF0056D2)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = cardBg,
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
                            tint = if (dark) Color.White else Color(0xFF004696)
                        )
                    }
                    Text(
                        text = "Información",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Card 1: Horario de Atención
                InfoDetailCard(
                    title = "Horario de Atención",
                    subtitle = "SERVICIOS ACADÉMICOS",
                    icon = Icons.Default.Schedule,
                    dark = dark,
                    cardBg = cardBg,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor,
                    iconBoxBg = iconBoxBg,
                    iconTintColor = iconTintColor,
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Lunes a Viernes", color = subtitleColor, fontSize = 15.sp)
                                Text("8:00 AM - 4:00 PM", fontWeight = FontWeight.Bold, color = titleColor, fontSize = 15.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Sábado y Domingo", color = subtitleColor, fontSize = 15.sp)
                                Text("Cerrado", color = Color(0xFFEF4444), fontWeight = FontWeight.Medium, fontSize = 15.sp)
                            }
                        }
                    }
                )

                // Card 2: Ubicación
                InfoDetailCard(
                    title = "Ubicación",
                    subtitle = "CAMPUS UTC",
                    icon = Icons.Default.LocationOn,
                    dark = dark,
                    cardBg = cardBg,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor,
                    iconBoxBg = iconBoxBg,
                    iconTintColor = iconTintColor,
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Escolares", fontWeight = FontWeight.Bold, color = titleColor, fontSize = 16.sp)
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, tint = subtitleColor, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Referencia: Por la entrada uno", color = subtitleColor, fontSize = 14.sp)
                            }
                        }
                    }
                )

                // Card 3: Contacto
                InfoDetailCard(
                    title = "Contacto",
                    subtitle = "ATENCIÓN DIRECTA",
                    icon = Icons.Default.Phone,
                    dark = dark,
                    cardBg = cardBg,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor,
                    iconBoxBg = iconBoxBg,
                    iconTintColor = iconTintColor,
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text("Línea Directa", color = subtitleColor, fontSize = 13.sp)
                                Text("8442883800", fontWeight = FontWeight.Bold, color = titleColor, fontSize = 20.sp)
                            }
                            Column {
                                Text("Correo Electrónico", color = subtitleColor, fontSize = 13.sp)
                                Text("ext.universitaria@utc.edu.mx", fontWeight = FontWeight.Bold, color = titleColor, fontSize = 17.sp)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun InfoDetailCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    dark: Boolean,
    cardBg: Color,
    titleColor: Color,
    subtitleColor: Color,
    iconBoxBg: Color,
    iconTintColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconBoxBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = subtitleColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            content()
        }
    }
}
