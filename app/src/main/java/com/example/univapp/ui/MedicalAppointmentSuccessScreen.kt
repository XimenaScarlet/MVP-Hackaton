package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MedicalAppointmentSuccessScreen(
    onGoHome: () -> Unit = {},
    vm: MedicalAppointmentViewModel
) {
    val confirmed by vm.confirmed.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9FAFB)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono de éxito
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFDCFCE7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF166534),
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "¡Cita Agendada!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(12.dp))

            confirmed?.let { appt ->
                Text(
                    text = "Tu cita para ${appt.service} ha sido confirmada.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Motivo: ${appt.reason}", fontSize = 14.sp, color = Color(0xFF4B5563))
                        Text("Fecha: ${appt.date}", fontSize = 14.sp, color = Color(0xFF4B5563))
                        Text("Hora: ${appt.time}", fontSize = 14.sp, color = Color(0xFF4B5563))
                        Text("Lugar: ${appt.location}", fontSize = 14.sp, color = Color(0xFF4B5563))
                        Text("Folio: ${appt.id.take(8).uppercase()}", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                    }
                }
            } ?: run {
                Text(
                    text = "Tu cita ha sido confirmada con éxito. Puedes revisar los detalles en tu perfil.",
                    fontSize = 16.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onGoHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Volver al Inicio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
