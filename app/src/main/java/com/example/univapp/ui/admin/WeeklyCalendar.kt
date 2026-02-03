package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val days = (0..4).map { LocalDate.now().plusDays(it.toLong()) }
    val hours = (5..22)

    Column(modifier = modifier) {
        // --- Days Header ---
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(60.dp)) // For time labels
            days.forEach { day ->
                DayHeader(
                    day = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    date = day.dayOfMonth.toString(),
                    isToday = day.isEqual(LocalDate.now()),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Schedule Grid ---
        Box {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
            ) {
                item {
                    Row(Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(60.dp)) {
                             Text("GMT-6", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }
                        // Divider lines for days
                        (0..4).forEach {
                            Spacer(modifier = Modifier.weight(1f))
                            if (it < 4) {
                                // This is not a real divider, just to space things out
                            }
                        }
                    }
                }
                hours.forEach { hour ->
                    item {
                        Row(Modifier.fillMaxWidth().height(60.dp)) {
                            // --- Time Label ---
                            Box(modifier = Modifier.width(60.dp).fillMaxHeight()) {
                                Text(
                                    text = String.format("%02d:00", hour),
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )
                            }
                            // --- Hour Divider ---
                            (0..4).forEach {
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(start = 8.dp)) {
                                     Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Gray.copy(alpha = 0.2f)))
                                }
                            }
                        }
                    }
                }
            }

            if (horarios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(top = 120.dp), contentAlignment = Alignment.TopCenter) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(imageVector = Icons.Default.EventBusy, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(80.dp))
                        Text("No hay clases programadas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("¡Agrega un horario para comenzar!", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
            // TODO: Render actual schedule items on the grid
        }
    }
}

@Composable
private fun DayHeader(day: String, date: String, isToday: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = day, color = if (isToday) Color.White else Color.Gray, fontSize = 12.sp)
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isToday) Color(0xFF673AB7) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(text = date, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
