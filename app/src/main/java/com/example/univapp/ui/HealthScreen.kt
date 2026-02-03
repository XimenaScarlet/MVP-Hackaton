package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun HealthScreen(
    onBack: () -> Unit = {},
    onOpenPsychSupport: () -> Unit = {},
    onOpenMedicalSupport: () -> Unit = {},
    onTriggerSOS: () -> Unit = {},
    onViewAppointments: () -> Unit = {}, // Nueva acción
    settingsVm: SettingsViewModel = viewModel(),
    medicalVm: MedicalAppointmentViewModel = viewModel() // Acceso a las citas
) {
    val dark by settingsVm.darkMode.collectAsState()
    val appointments by medicalVm.userAppointments.collectAsState()

    // Dynamic Colors
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val titleColor = if (dark) Color.White else Color(0xFF111827)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val cardTextColor = if (dark) Color.White else Color(0xFF111827)
    val cardSubtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF4B5563)
    val iconCircleBg = if (dark) Color(0xFF334155) else Color.White

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
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

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Salud",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                    Text(
                        text = "Bienestar Universitario",
                        fontSize = 15.sp,
                        color = subtitleColor
                    )
                }

                // ICONO DE CITAS (Solo si tiene citas)
                if (appointments.isNotEmpty()) {
                    IconButton(
                        onClick = onViewAppointments,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .background(Color(0xFF2563EB).copy(alpha = 0.1f), CircleShape)
                    ) {
                        BadgedBox(
                            badge = { 
                                Badge { Text(appointments.size.toString()) } 
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventNote,
                                contentDescription = "Ver mis citas",
                                tint = Color(0xFF2563EB)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Doctor Card
            HealthActionCard(
                title = "Doctor",
                subtitle = "Atención médica general",
                icon = Icons.Filled.MedicalServices,
                containerColor = if (dark) Color(0xFF1E293B) else Color(0xFFEEF2FF),
                iconTintColor = if (dark) Color(0xFF60A5FA) else Color(0xFF4F46E5),
                iconCircleBg = iconCircleBg,
                textColor = cardTextColor,
                subtitleColor = cardSubtitleColor,
                onClick = onOpenMedicalSupport
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Psicología Card
            HealthActionCard(
                title = "Psicología",
                subtitle = "Apoyo emocional y mental",
                icon = Icons.Filled.Psychology,
                containerColor = if (dark) Color(0xFF1E293B) else Color(0xFFECFDF5),
                iconTintColor = if (dark) Color(0xFF34D399) else Color(0xFF10B981),
                iconCircleBg = iconCircleBg,
                textColor = cardTextColor,
                subtitleColor = cardSubtitleColor,
                onClick = onOpenPsychSupport
            )

            Spacer(modifier = Modifier.weight(1f))

            // Emergencias Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "EMERGENCIAS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = subtitleColor,
                    letterSpacing = 1.2.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(124.dp)
                        .background(if (dark) Color(0xFF450A0A) else Color(0xFFFFE4E6), CircleShape)
                ) {
                    Surface(
                        onClick = onTriggerSOS,
                        modifier = Modifier.size(98.dp),
                        shape = CircleShape,
                        color = Color(0xFFEF4444),
                        shadowElevation = 6.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "SOS",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text = "sos",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HealthActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    iconTintColor: Color,
    iconCircleBg: Color,
    textColor: Color,
    subtitleColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(32.dp))
            .clickable { onClick() },
        color = containerColor,
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(elevation = 10.dp, shape = CircleShape, spotColor = Color.Black.copy(alpha = 0.2f)),
                shape = CircleShape,
                color = iconCircleBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = subtitle,
                fontSize = 15.sp,
                color = subtitleColor
            )
        }
    }
}
