package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PsychologistDetailScreen(
    onBack: () -> Unit = {},
    onBookAppointment: () -> Unit = {},
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF9FBFF)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1E293B)
    val textColor = if (dark) Color(0xFFE2E8F0) else Color(0xFF0F172A)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .shadow(elevation = 2.dp, shape = CircleShape)
                        .background(if (dark) Color(0xFF334155) else Color.White, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Atrás",
                        tint = titleColor
                    )
                }

                Text(
                    text = "Información del Psicólogo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Profile Image Placeholder
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(150.dp),
                    shape = CircleShape,
                    color = if (dark) Color(0xFF334155) else Color(0xFFE2E8F0),
                    border = androidx.compose.foundation.BorderStroke(4.dp, cardBg)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Photo", color = subtitleColor)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(cardBg, CircleShape)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF4ADE80), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Lic. Mariana Herrera",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = if (dark) Color(0xFF1E3A8A).copy(alpha = 0.4f) else Color(0xFFEFF6FF),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "PSICOLOGÍA CLÍNICA",
                    color = Color(0xFF60A5FA),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Sobre el especialista",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Especialista en salud mental con más de 8 años de experiencia brindando apoyo emocional a jóvenes universitarios. Experta en manejo de ansiedad, estrés académico y procesos de autoconocimiento.",
                fontSize = 14.sp,
                color = subtitleColor,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            DetailedInfoCard(
                icon = Icons.Default.Schedule,
                label = "HORARIO DE ATENCIÓN",
                text = "Lunes a Jueves, 09:00 AM - 04:00 PM",
                iconTintColor = Color(0xFF3B82F6),
                iconBgColor = if (dark) Color(0xFF1E3A8A).copy(alpha = 0.3f) else Color(0xFFDBEAFE),
                cardBg = cardBg,
                titleColor = titleColor,
                subtitleColor = subtitleColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            DetailedInfoCard(
                icon = Icons.Default.LocationOn,
                label = "UBICACIÓN",
                text = "Edificio D, Cubículo 202 (Bloque Salud)",
                iconTintColor = Color(0xFFF97316),
                iconBgColor = if (dark) Color(0xFF431407) else Color(0xFFFFEDD5),
                cardBg = cardBg,
                titleColor = titleColor,
                subtitleColor = subtitleColor
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onBookAppointment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(14.dp), spotColor = Color(0xFF2D6A4F)),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D6A4F))
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Agendar Cita", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetailedInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    text: String,
    iconTintColor: Color,
    iconBgColor: Color,
    cardBg: Color,
    titleColor: Color,
    subtitleColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 15.dp, shape = RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.1f)),
        color = cardBg,
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                color = iconBgColor,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon, 
                        contentDescription = null, 
                        tint = iconTintColor, 
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = label,
                fontSize = 10.sp,
                color = subtitleColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}
