@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.univapp.ui.util.AppScaffold
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
        db.collection("avisos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val list = snapshot?.documents?.mapNotNull { doc ->
                    val title = doc.getString("title") ?: doc.getString("titulo") ?: "Sin título"
                    val body = doc.getString("body") ?: doc.getString("descripcion") ?: ""
                    val time = doc.getTimestamp("timestamp") ?: doc.getTimestamp("fecha")
                    val cat = doc.getString("category") ?: doc.getString("categoria") ?: "General"
                    val isUrgent = doc.getBoolean("urgent") ?: doc.getBoolean("urgente") ?: false
                    NoticeItem(doc.id, title, body, time, cat, isUrgent)
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

    val darkBg = Color(0xFF0B101F)
    val cardBg = Color(0xFF131A2C)

    AppScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Avisos Universitarios", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = darkBg)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(darkBg).padding(padding)) {
            if (notices.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay avisos publicados.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(notices) { notice ->
                        AnnouncementPremiumCard(notice, cardBg)
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementPremiumCard(notice: NoticeItem, cardBg: Color) {
    val accentColor = when (notice.categoria.lowercase()) {
        "pagos", "pago" -> Color(0xFFFF6B6B)
        "entrega", "académico" -> Color(0xFF4FC3F7)
        else -> Color(0xFF818CF8)
    }

    val icon = when (notice.categoria.lowercase()) {
        "pagos", "pago" -> Icons.Default.Error
        "entrega" -> Icons.Default.Inventory2
        else -> Icons.Default.Campaign
    }

    val timeStr = if (notice.fecha != null) {
        SimpleDateFormat("dd MMM, hh:mm a", Locale("es", "MX")).format(notice.fecha.toDate()).uppercase()
    } else "RECIENTE"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = cardBg,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Línea de acento lateral - Solo a la izquierda
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .padding(vertical = 16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )

            Column(modifier = Modifier.padding(24.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = notice.titulo,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (notice.urgente) {
                                Box(modifier = Modifier.padding(start = 8.dp).size(6.dp).background(accentColor, CircleShape))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$timeStr  •  HACE UN MOMENTO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.05f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(icon, null, tint = accentColor, modifier = Modifier.size(22.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = notice.descripcion,
                    fontSize = 14.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = notice.categoria.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = accentColor,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
