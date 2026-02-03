@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AnnouncementsScreen(
    onBack: () -> Unit = {},
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()

    // Dynamic Colors
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val textColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val bodyColor = if (dark) Color(0xFF94A3B8) else Color(0xFF4B5563)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF9CA3AF)

    val notices = listOf(
        NoticeItem(
            title = "Entrega de proyectos finales",
            category = "ACADÉMICO",
            description = "La entrega final de proyectos es el próximo 20 de noviembre. Asegúrate de subir tus archivos antes de las 11:59 PM.",
            timeAgo = "hace 2 h",
            color = if (dark) Color(0xFF60A5FA) else Color(0xFF3B82F6),
            icon = Icons.Default.Notifications
        ),
        NoticeItem(
            title = "Evento de bienvenida",
            category = "EVENTO",
            description = "Miércoles 13 • 10:00 AM • Auditorio principal. ¡Te esperamos para celebrar el inicio del semestre!",
            timeAgo = "hace 1 día",
            color = if (dark) Color(0xFF34D399) else Color(0xFF10B981),
            icon = Icons.Default.CalendarMonth
        ),
        NoticeItem(
            title = "Pago de reinscripción",
            category = "PAGOS",
            description = "Fecha límite: 12 de noviembre. Puedes realizar tu pago directamente en caja o a través de nuestro portal en línea.",
            timeAgo = "hace 5 días",
            color = if (dark) Color(0xFFA5B4FC) else Color(0xFF8B5CF6),
            icon = Icons.Default.Payments
        ),
        NoticeItem(
            title = "Taller: Introducción a Kotlin",
            category = "CAPACITACIÓN",
            description = "Viernes 15 • 4:00–6:00 PM • Laboratorio B-204. Cupo limitado a 20 estudiantes, ¡Inscríbete hoy!",
            timeAgo = "hace 1 sem",
            color = if (dark) Color(0xFFF472B6) else Color(0xFFEC4899),
            icon = Icons.Default.School
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (dark) Color(0xFF1E293B) else Color.White,
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
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
                    Text(
                        text = "Avisos Universitarios",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notices) { notice ->
                    AnnouncementCard(notice, cardBg, textColor, bodyColor, subtitleColor, dark)
                }
            }
        }
    }
}

private data class NoticeItem(
    val title: String,
    val category: String,
    val description: String,
    val timeAgo: String,
    val color: Color,
    val icon: ImageVector
)

@Composable
private fun AnnouncementCard(
    notice: NoticeItem,
    cardBg: Color,
    textColor: Color,
    bodyColor: Color,
    subtitleColor: Color,
    isDark: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(notice.color)
            )
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(notice.color.copy(alpha = if (isDark) 0.2f else 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = notice.icon,
                            contentDescription = null,
                            tint = notice.color,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = notice.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = notice.color.copy(alpha = if (isDark) 0.2f else 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = notice.category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = notice.color,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = notice.timeAgo,
                                fontSize = 12.sp,
                                color = subtitleColor
                            )
                        }
                    }
                    
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = subtitleColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = notice.description,
                    fontSize = 14.sp,
                    color = bodyColor,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
