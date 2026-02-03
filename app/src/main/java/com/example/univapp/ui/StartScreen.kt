@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF0F5B4A)      // verde profundo
private val CardGreen = Color(0xFF1A7A63)

@Composable
fun StartScreen(
    onStart: () -> Unit
) {
    Surface(color = Bg) {
        Box(Modifier.fillMaxSize()) {

            // Líneas decorativas (ondas) tipo mockup
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 24.dp)
            ) {
                val w = size.width
                val h = size.height
                fun wave(yStart: Float, amp: Float, color: Color) {
                    val path = Path()
                    path.moveTo(0f, yStart)
                    var x = 0f
                    while (x <= w) {
                        val y = yStart + amp * kotlin.math.sin((x / w) * 8 * Math.PI).toFloat()
                        path.lineTo(x, y)
                        x += 12f
                    }
                    drawPath(path, color = color, alpha = 0.18f, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                }
                wave(h * .25f, 26f, Color.White)
                wave(h * .50f, 24f, Color.White)
                wave(h * .75f, 22f, Color.White)
            }

            // Contenido
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(Modifier.height(8.dp))

                // “Manitas” simplificadas: dos chips redondeados simulando el arte
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = Color(0xFFEDEADE),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.size(width = 96.dp, height = 64.dp)
                    ) {}
                    Surface(
                        color = Color(0xFFEDEADE),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.size(width = 96.dp, height = 64.dp)
                    ) {}
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "MiUTC",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Una aplicación multiplataforma diseñada para centralizar de forma eficiente las múltiples tareas académicas de todos los estudiantes. Su objetivo es ofrecer un acceso rápido y organizado a los distintos servicios institucionales, así como facilitar el control de horarios, calificaciones y demás actividades escolares.",
                        color = Color(0xFFE6FFF7),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                // Botón “Comenzar”
                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CardGreen)
                ) {
                    Text("Comenzar", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Outlined.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}
