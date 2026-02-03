@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PsychologicalSupportScreen(
    onBack: () -> Unit = {},
    onBook: () -> Unit = {},     // <-- NUEVO: navega a la pantalla de agenda
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Apoyo Psicológico") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F8FA))
                .padding(pv)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // CTA
            Button(
                onClick = onBook,   // <-- ahora sí abre la agenda
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1677FF))
            ) {
                Text("Agendar Cita", fontSize = 18.sp)
            }

            Spacer(Modifier.height(20.dp))

            // Avatar (placeholder con iniciales)
            Surface(
                color = Color.White,
                shape = CircleShape,
                tonalElevation = 1.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ER", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Dra. Elena Ríos", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
            Text("Psicóloga Clínica", fontSize = 18.sp, color = Color(0xFF64748B))
            Text(
                "Especialista en Ansiedad y Estrés Académico",
                fontSize = 16.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Mi enfoque es proporcionar un espacio seguro y de apoyo para que los estudiantes puedan navegar los desafíos académicos y personales. Estoy aquí para ayudarte a desarrollar estrategias de afrontamiento y mejorar tu bienestar general.",
                color = Color(0xFF334155),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(20.dp))

            // Tarjeta de info (horario + contacto)
            ElevatedCard(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Horarios de Atención", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF0F172A))
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFEAF2FF), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Color(0xFF1677FF), modifier = Modifier.padding(10.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Lunes a Viernes", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                            Text("8:00 - 17:00", color = Color(0xFF94A3B8))
                        }
                    }

                    Divider(Modifier.padding(vertical = 14.dp))

                    Text("Contacto", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF0F172A))
                    Spacer(Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFEAF2FF), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = Color(0xFF1677FF), modifier = Modifier.padding(10.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Correo Electrónico", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                            Text("e.rios@universidad.edu", color = Color(0xFF64748B))
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFEAF2FF), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Outlined.Call, contentDescription = null, tint = Color(0xFF1677FF), modifier = Modifier.padding(10.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Extensión", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                            Text("#4352", color = Color(0xFF64748B))
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFF64748B))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Todas las consultas son confidenciales y seguras.",
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
