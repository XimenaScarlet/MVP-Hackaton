package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.ui.util.AppScaffold
import com.example.univapp.ui.util.ValidatedTextField
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminAnnouncementsViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _isSending = MutableStateFlow(false)
    val isSending = _isSending.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun sendAnnouncement(title: String, body: String, category: String, isUrgent: Boolean, expirationDate: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSending.value = true
            try {
                val announcement = hashMapOf(
                    "title" to title.trim(),
                    "body" to body.trim(),
                    "category" to category,
                    "urgent" to isUrgent,
                    "expirationDate" to expirationDate,
                    "timestamp" to Timestamp.now(),
                    "author" to "Administración"
                )
                db.collection("avisos").add(announcement).await()
                _message.value = "Aviso publicado con éxito"
                onComplete()
            } catch (e: Exception) {
                _message.value = "Error al publicar: ${e.message}"
            } finally {
                _isSending.value = false
            }
        }
    }

    fun clearMessage() { _message.value = null }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnnouncementsScreen(
    onBack: () -> Unit,
    vm: AdminAnnouncementsViewModel = viewModel()
) {
    val isDark = isSystemInDarkTheme()
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Académico") }
    var isUrgent by remember { mutableStateOf(false) }
    var expirationDate by remember { mutableStateOf("12/31/2024") }
    
    val isSending by vm.isSending.collectAsState()
    val message by vm.message.collectAsState()

    val bgColor = if (isDark) Color(0xFF121212) else Color.White
    val surfaceColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF8FAFC)
    val headerColor = Color(0xFF0F172A)
    val textColor = if (isDark) Color.White else Color(0xFF1D2939)
    val labelColor = Color(0xFF64748B)

    AppScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuevo Aviso", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = headerColor)
            )
        },
        bottomBar = {
            Button(
                onClick = { vm.sendAnnouncement(title, body, selectedCategory, isUrgent, expirationDate) { onBack() } },
                enabled = title.isNotBlank() && body.isNotBlank() && !isSending,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = headerColor)
            ) {
                if (isSending) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Publicar Aviso", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            LabelHeader("Título del aviso")
            ValidatedTextField(
                value = title,
                onValueChange = { title = it },
                label = "Título",
                maxLength = 60,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            LabelHeader("Categoría")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryChip("Académico", selectedCategory == "Académico", Color(0xFF2563EB), isDark) { selectedCategory = it }
                CategoryChip("Eventos", selectedCategory == "Eventos", Color(0xFF10B981), isDark) { selectedCategory = it }
                CategoryChip("Pagos", selectedCategory == "Pagos", Color(0xFFA855F7), isDark) { selectedCategory = it }
            }
            Spacer(Modifier.height(8.dp))
            CategoryChip("Talleres", selectedCategory == "Talleres", Color(0xFFF59E0B), isDark) { selectedCategory = it }

            Spacer(Modifier.height(24.dp))

            LabelHeader("Descripción del contenido")
            ValidatedTextField(
                value = body,
                onValueChange = { body = it },
                label = "Detalles del anuncio",
                maxLength = 500,
                singleLine = false,
                modifier = Modifier.heightIn(min = 120.dp)
            )

            Spacer(Modifier.height(24.dp))

            LabelHeader("Fecha de expiración")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(surfaceColor)
                    .border(1.dp, labelColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(expirationDate, color = labelColor)
                    Icon(Icons.Default.CalendarMonth, null, tint = labelColor)
                }
            }

            Spacer(Modifier.height(24.dp))

            LabelHeader("Prioridad del aviso")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PriorityHigh, null, tint = Color(0xFFF97316), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Aviso Urgente", color = textColor, fontWeight = FontWeight.Medium)
                    }
                    Switch(
                        checked = isUrgent,
                        onCheckedChange = { isUrgent = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFF97316)
                        )
                    )
                }
            }
        }
    }

    if (message != null) {
        AlertDialog(
            onDismissRequest = { vm.clearMessage() },
            confirmButton = { TextButton(onClick = { vm.clearMessage() }) { Text("Aceptar") } },
            title = { Text("Aviso") },
            text = { Text(message ?: "") }
        )
    }
}

@Composable
private fun LabelHeader(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1D2939).copy(alpha = 0.8f),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun CategoryChip(
    text: String,
    isSelected: Boolean,
    color: Color,
    isDark: Boolean,
    onClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(text) },
        color = if (isSelected) color else color.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f)) else null
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (isSelected) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (isSelected) Color.White else color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
