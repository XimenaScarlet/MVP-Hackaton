package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.univapp.data.Horario
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyCalendar(
    horarios: List<Horario>,
    modifier: Modifier = Modifier
) {
    val days = listOf("LUN", "MAR", "MIE", "JUE", "VIE")
    val hours = (7..21).toList()

    Column(modifier = modifier) {
        // --- Days Header ---
        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Spacer(modifier = Modifier.width(50.dp))
            days.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                hours.forEach { hour ->
                    item {
                        Row(Modifier.fillMaxWidth().height(60.dp)) {
                            // Time label
                            Text(
                                text = String.format("%02d:00", hour),
                                modifier = Modifier.width(50.dp).padding(top = 4.dp),
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            
                            // Grid and Items
                            Row(Modifier.weight(1f).fillMaxHeight()) {
                                days.forEach { day ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(Color.White)
                                            .padding(1.dp)
                                    ) {
                                        // Background lines
                                        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF2F4F7)))
                                        
                                        // Render matches
                                        val match = horarios.find { h ->
                                            h.dias.contains(day) && h.horaInicio?.startsWith(String.format("%02d", hour)) == true
                                        }
                                        
                                        if (match != null) {
                                            ScheduleItem(match)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (horarios.isEmpty()) {
                EmptyStateCalendar()
            }
        }
    }
}

@Composable
private fun ScheduleItem(horario: Horario) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)),
        modifier = Modifier.fillMaxSize().padding(2.dp)
    ) {
        Column(Modifier.padding(4.dp)) {
            Text(
                text = horario.materiaNombre ?: "Clase",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0369A1),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 11.sp
            )
            Text(
                text = horario.salon ?: "",
                fontSize = 9.sp,
                color = Color(0xFF0369A1).copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun EmptyStateCalendar() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.EventBusy, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("Sin horarios registrados", color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}
