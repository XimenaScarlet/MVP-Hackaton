@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* Paleta */
private val ScreenBg = Color(0xFFF6F8FA)
private val CardBg   = Color.White
private val Line     = Color(0xFFE9E6F0)
private val Muted    = Color(0xFF6B7280)

/* Modelo */
private data class Notice(
    val title: String,
    val category: String,
    val description: String,
    val timeAgo: String
)

/* Avisos universitarios */
private val demoNotices = listOf(
    Notice(
        title = "Entrega de proyectos finales",
        category = "Académico",
        description = "Recuerda que la entrega de proyectos finales es el 20 de noviembre. Sube tus archivos antes de las 11:59 PM.",
        timeAgo = "2 h"
    ),
    Notice(
        title = "Evento de bienvenida a nuevos alumnos",
        category = "Evento universitario",
        description = "Miércoles 13: 10:00 AM en el auditorio principal. Participa y conoce a tus compañeros.",
        timeAgo = "1 día"
    ),
    Notice(
        title = "Pago de reinscripción",
        category = "Pagos",
        description = "Fecha límite: 12 de noviembre. Puedes pagar en caja o en línea.",
        timeAgo = "5 días"
    ),
    Notice(
        title = "Nuevo taller: Introducción a Kotlin",
        category = "Capacitación",
        description = "Viernes 15: 4:00–6:00 PM en Lab B-204. Inscripciones abiertas en el portal.",
        timeAgo = "1 sem"
    )
)

@Composable
fun AnnouncementsScreen(onBack: (() -> Unit)? = null) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val notices = remember { mutableStateListOf(*demoNotices.toTypedArray()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Avisos universitarios", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() ?: backDispatcher?.onBackPressed() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        containerColor = ScreenBg
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (notices.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes avisos universitarios", color = Muted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notices, key = { it.hashCode() }) { n ->
                        NoticeCard(n = n, onDelete = { notices.remove(n) })
                    }
                }
            }
        }
    }
}

@Composable
private fun NoticeCard(n: Notice, onDelete: () -> Unit) {
    // Colores/ícono por categoría
    val (icon, tint) = when (n.category) {
        "Académico" -> Icons.Outlined.Notifications to Color(0xFF4CB2F5)
        "Evento universitario" -> Icons.Outlined.Event to Color(0xFF10B981)
        "Sistema" -> Icons.Outlined.Campaign to Color(0xFFF59E0B)
        "Pagos" -> Icons.Outlined.Campaign to Color(0xFF6366F1)
        "Capacitación" -> Icons.Outlined.Notifications to Color(0xFF8B5CF6)
        else -> Icons.Outlined.Notifications to Color(0xFF94A3B8)
    }

    // Misma apariencia para TODOS los avisos (como el primero de tu captura)
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono en pastilla suave
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = .16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint)
            }

            Spacer(Modifier.width(12.dp))

            // Contenido texto
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = n.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFF111827)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(n.timeAgo, color = Muted, fontSize = 12.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = n.description,
                    color = Muted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Botón eliminar alineado a la derecha
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444))
            }
        }
    }
}
