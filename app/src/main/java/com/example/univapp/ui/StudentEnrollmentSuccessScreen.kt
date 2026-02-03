package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StudentEnrollmentSuccessScreen(
    onClose: () -> Unit = {},
    onViewRequests: () -> Unit = {},
    onGoHome: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FB)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón cerrar (X) en la esquina superior izquierda
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFF1A1C1E)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.2f))

            // Icono de Éxito Circular
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFFE2F9E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = Color(0xFF149E61)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Título y Mensaje
            Text(
                text = "¡Solicitud enviada!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu trámite ha sido registrado correctamente en el sistema.",
                fontSize = 16.sp,
                color = Color(0xFF74777F),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Tarjeta de Detalles
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Folio de trámite", color = Color(0xFF74777F), fontSize = 15.sp)
                        Text("ESC-2026-0015", fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E), fontSize = 15.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Estado", color = Color(0xFF74777F), fontSize = 15.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFFF9A825), CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pendiente", fontWeight = FontWeight.Bold, color = Color(0xFFF9A825), fontSize = 15.sp)
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color(0xFFF1F3F4)
                    )

                    Text(
                        text = "Te avisaremos cuando esté lista a través de las notificaciones de la app y tu correo institucional.",
                        fontSize = 13.sp,
                        color = Color(0xFF9CA3AF),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botones de Acción Inferiores
            Button(
                onClick = onViewRequests,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
            ) {
                Text("Ver mis solicitudes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onGoHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Volver al inicio",
                    color = Color(0xFF74777F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
