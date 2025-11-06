@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.PsychologyAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HealthScreen(
    onOpenPsychSupport: () -> Unit = {},
    onOpenMedicalSupport: () -> Unit = {},   // <--- NUEVO
) {
    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("Salud y Bienestar") }) }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F8FA))
                .padding(pv)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ServiceCard(
                title = "Apoyo Psicológico",
                subtitle = "Acompañamiento y consejería para estudiantes",
                icon = Icons.Outlined.PsychologyAlt,
                tint = Color(0xFF1677FF),
                onClick = onOpenPsychSupport
            )
            ServiceCard(
                title = "Servicio Médico",
                subtitle = "Consulta general y primeros auxilios",
                icon = Icons.Outlined.LocalHospital,
                tint = Color(0xFF10B981),
                onClick = onOpenMedicalSupport   // <--- abre médico
            )
            ServiceCard(
                title = "Bienestar y Actividad Física",
                subtitle = "Talleres, deporte y vida saludable",
                icon = Icons.Outlined.MonitorHeart,
                tint = Color(0xFFF59E0B)
            )
        }
    }
}

@Composable
private fun ServiceCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = tint.copy(alpha = .15f), shape = RoundedCornerShape(12.dp)) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(subtitle, color = Color(0xFF6B7280), fontSize = 13.sp)
            }
        }
    }
}
