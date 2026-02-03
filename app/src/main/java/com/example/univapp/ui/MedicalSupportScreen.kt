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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalSupportScreen(
    onBack: () -> Unit = {},
    onBook: () -> Unit = {},
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF111827)
    val textColor = if (dark) Color(0xFFE2E8F0) else Color(0xFF111827)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(top = 20.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart).shadow(2.dp, CircleShape).background(if(dark) Color(0xFF334155) else Color.White, CircleShape).size(40.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Atrás", modifier = Modifier.size(24.dp), tint = titleColor)
                }
                Text(text = "Información del Médico", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Image
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(modifier = Modifier.size(150.dp), shape = CircleShape, shadowElevation = 8.dp, color = cardBg) {
                    Image(painter = painterResource(id = R.drawable.logo_3_este_si), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().padding(4.dp).clip(CircleShape))
                }
                Box(modifier = Modifier.size(30.dp).background(if(dark) Color(0xFF1E293B) else Color.White, CircleShape).padding(3.dp).background(Color(0xFF22C55E), CircleShape))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Dr. Roberto Mendoza", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = titleColor)
            
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = if(dark) Color(0xFF1E3A8A).copy(alpha = 0.4f) else Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp)) {
                Text(text = "MÉDICO GENERAL", color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Sobre el médico", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = titleColor)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Especialista en medicina integral con más de 12 años de experiencia. Comprometido con el bienestar preventivo de la comunidad universitaria.", fontSize = 14.sp, color = subtitleColor, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 22.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // Cards de Información
            MedicalInfoCard(icon = Icons.Default.Schedule, label = "HORARIO DE ATENCIÓN", text = "Lun - Vie, 08:00 AM - 04:00 PM", dark = dark, cardBg = cardBg, titleColor = textColor, subtitleColor = subtitleColor)
            Spacer(modifier = Modifier.height(16.dp))
            MedicalInfoCard(icon = Icons.Default.LocationOn, label = "UBICACIÓN", text = "Consultorio A-102 (Bloque Salud)", dark = dark, cardBg = cardBg, titleColor = textColor, subtitleColor = subtitleColor)

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBook, modifier = Modifier.fillMaxWidth().height(54.dp).shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF2563EB)), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))) {
                Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Text("Agendar Cita", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MedicalInfoCard(icon:  ImageVector, label: String, text: String, dark: Boolean, cardBg: Color, titleColor: Color, subtitleColor: Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = cardBg), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(modifier = Modifier.size(42.dp), shape = RoundedCornerShape(12.dp), color = if(dark) Color(0xFF334155) else Color(0xFFF1F5F9)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = Color(0xFF2563EB), modifier = Modifier.size(22.dp)) }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = subtitleColor, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = titleColor)
        }
    }
}
