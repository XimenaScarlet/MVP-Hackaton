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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

data class NoticeItem(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: com.google.firebase.Timestamp? = null,
    val categoria: String = "General",
    val urgente: Boolean = false
)

class AnnouncementsViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _notices = MutableStateFlow<List<NoticeItem>>(emptyList())
    val notices = _notices.asStateFlow()

    init {
        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        // Quitamos el filtro de "activo" para que se vean todos los publicados
        db.collection("avisos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                val list = snapshot?.documents?.mapNotNull { doc ->
                    // Mapeo manual para soportar campos en Inglés y Español
                    val title = doc.getString("title") ?: doc.getString("titulo") ?: "Sin título"
                    val body = doc.getString("body") ?: doc.getString("descripcion") ?: ""
                    val time = doc.getTimestamp("timestamp") ?: doc.getTimestamp("fecha")
                    val cat = doc.getString("category") ?: doc.getString("categoria") ?: "General"
                    val isUrgent = doc.getBoolean("urgent") ?: doc.getBoolean("urgente") ?: false
                    
                    NoticeItem(
                        id = doc.id,
                        titulo = title,
                        descripcion = body,
                        fecha = time,
                        categoria = cat,
                        urgente = isUrgent
                    )
                }?.sortedByDescending { it.fecha } ?: emptyList()
                
                _notices.value = list
            }
    }
}

@Composable
fun AnnouncementsScreen(
    onBack: () -> Unit = {},
    settingsVm: SettingsViewModel = viewModel(),
    announcementsVm: AnnouncementsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    val notices by announcementsVm.notices.collectAsState()

    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val bodyColor = if (dark) Color(0xFF94A3B8) else Color(0xFF4B5563)

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (dark) Color(0xFF1E293B) else Color.White,
                shadowElevation = 1.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                    IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = titleColor, modifier = Modifier.size(32.dp))
                    }
                    Text("Avisos Universitarios", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = titleColor)
                }
            }

            if (notices.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Campaign, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Spacer(Modifier.height(16.dp))
                        Text("No hay avisos publicados.", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(notices) { notice ->
                        AnnouncementCard(notice, cardBg, titleColor, bodyColor, dark)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnouncementCard(notice: NoticeItem, cardBg: Color, titleColor: Color, bodyColor: Color, isDark: Boolean) {
    val categoryColor = when (notice.categoria.lowercase()) {
        "pagos" -> Color(0xFF8B5CF6)
        "académico", "academico" -> Color(0xFF3B82F6)
        "eventos" -> Color(0xFF10B981)
        else -> Color(0xFF64748B)
    }

    val timeStr = if (notice.fecha != null) {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(notice.fecha.toDate())
    } else "Reciente"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(6.dp).fillMaxHeight().background(if (notice.urgente) Color.Red else categoryColor))
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = notice.titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor, modifier = Modifier.weight(1f))
                    if (notice.urgente) {
                        Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                    }
                }
                Text(text = timeStr, fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.height(12.dp))
                Text(text = notice.descripcion, fontSize = 14.sp, color = bodyColor, lineHeight = 20.sp)
                
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = categoryColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = notice.categoria.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = categoryColor
                    )
                }
            }
        }
    }
}
