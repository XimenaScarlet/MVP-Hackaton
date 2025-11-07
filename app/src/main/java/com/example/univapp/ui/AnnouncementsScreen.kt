
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
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
private val Muted    = Color(0xFF374151)

/* Modelo */
private data class Notice(
    val title: String,
    val category: String,
    val description: String,
    val timeAgo: String
)

/* Demo */
private val demoNotices = listOf(
    Notice(
        title = "Entrega de proyectos finales",
        category = "Académico",
        description = "La entrega es el 20 de noviembre. Sube tus archivos antes de las 11:59 PM.",
        timeAgo = "2 h"
    ),
    Notice(
        title = "Evento de bienvenida",
        category = "Evento",
        description = "Miércoles 13 • 10:00 AM • Auditorio principal. ¡Te esperamos!",
        timeAgo = "1 día"
    ),
    Notice(
        title = "Pago de reinscripción",
        category = "Pagos",
        description = "Fecha límite: 12 de noviembre. Paga en caja o en línea.",
        timeAgo = "5 días"
    ),
    Notice(
        title = "Taller: Introducción a Kotlin",
        category = "Capacitación",
        description = "Viernes 15 • 4:00–6:00 PM • Lab B-204. Cupo limitado.",
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
                title = { Text("Avisos universitarios") },
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() ?: backDispatcher?.onBackPressed() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        containerColor = ScreenBg
    ) { pv ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(pv)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp)
        ) {
            items(notices, key = { it.hashCode() }) { n ->
                NoticeBigCard(
                    n = n,
                    onDelete = { notices.remove(n) }
                )
            }
        }
    }
}

/* ---------- Card grande estilo “Salud” ---------- */

@Composable
private fun NoticeBigCard(n: Notice, onDelete: () -> Unit) {
    val (icon, bg, fg, pill) = when (n.category) {
        "Académico"     -> Quad(Icons.Outlined.Notifications, Color(0xFFB8E1FF), Color(0xFF0E1B4D), "Académico")
        "Evento"        -> Quad(Icons.Outlined.Event,          Color(0xFFAAF27F), Color(0xFF0B3D17), "Evento")
        "Pagos"         -> Quad(Icons.Outlined.Campaign,       Color(0xFFD6C4FF), Color(0xFF2D0E4D), "Pagos")
        "Capacitación"  -> Quad(Icons.Outlined.Notifications,  Color(0xFFFFC0E6), Color(0xFF3D0B2A), "Capacitación")
        else            -> Quad(Icons.Outlined.Notifications,  Color(0xFFE5E7EB), Color(0xFF111827), n.category)
    }

    ElevatedCard(
        onClick = {},
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = bg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp)) {

            // Encabezado: icono + título + pill + tiempo
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = fg)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(n.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color.White.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(pill, fontSize = 12.sp, color = fg)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(n.timeAgo, fontSize = 12.sp, color = fg.copy(alpha = 0.75f))
                    }
                }
                // puntito decorativo + borrar
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.55f))
                )
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Campaign, contentDescription = "Eliminar", tint = fg)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Descripción
            Text(
                n.description,
                fontSize = 14.sp,
                color = fg.copy(alpha = 0.9f),
                lineHeight = 19.sp
            )
        }
    }
}

/* Helper para devolver 4 valores */
private data class Quad<A,B,C,D>(val a: A, val b: B, val c: C, val d: D)
private operator fun <A,B,C,D> Quad<A,B,C,D>.component1() = a
private operator fun <A,B,C,D> Quad<A,B,C,D>.component2() = b
private operator fun <A,B,C,D> Quad<A,B,C,D>.component3() = c
private operator fun <A,B,C,D> Quad<A,B,C,D>.component4() = d
