package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Campaign
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
                    "title" to title,
                    "body" to body,
                    "category" to category,
                    "urgent" to isUrgent,
                    "expirationDate" to expirationDate,
                    "timestamp" to Timestamp.now(),
                    "author" to "Administración"
                )
                // Se guarda en la colección "avisos" que es la que lee la app de alumnos
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
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Académico") }
    var isUrgent by remember { mutableStateOf(false) }
    var expirationDate by remember { mutableStateOf("02/28/2026") } // Fecha de ejemplo
    
    val categories = listOf("Académico", "Eventos", "Pagos", "Talleres")
    val isSending by vm.isSending.collectAsState()
    val message by vm.message.collectAsState()

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color(0xFF1D2939))
                }
                Text("Nuevo Aviso", modifier = Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1D2939))
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
        ) {
            LabelText("TÍTULO DEL AVISO")
            TextField(
                value = title, onValueChange = { title = it },
                placeholder = { Text("Ej. Suspensión de clases", color = Color(0xFF94A3B8)) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF8FAFC), unfocusedContainerColor = Color(0xFFF8FAFC), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )

            Spacer(Modifier.height(32.dp))

            LabelText("CATEGORÍA")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF1D4ED8) else Color.White)
                            .border(1.dp, if (isSelected) Color(0xFF1D4ED8) else Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(cat, color = if (isSelected) Color.White else Color(0xFF64748B), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            LabelText("DESCRIPCIÓN")
            TextField(
                value = body, onValueChange = { body = it },
                placeholder = { Text("Escriba los detalles del anuncio aquí...", color = Color(0xFF94A3B8)) },
                modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF8FAFC), unfocusedContainerColor = Color(0xFFF8FAFC), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )

            Spacer(Modifier.height(32.dp))

            LabelText("FECHA DE EXPIRACIÓN")
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(24.dp)),
                color = Color(0xFFF8FAFC)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(expirationDate, color = Color(0xFF94A3B8))
                    Icon(Icons.Default.CalendarToday, null, tint = Color(0xFF64748B), modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            LabelText("PRIORIDAD")
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(24.dp)),
                color = Color(0xFFF8FAFC)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Aviso Urgente", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                    Switch(checked = isUrgent, onCheckedChange = { isUrgent = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF1D4ED8)))
                }
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = { vm.sendAnnouncement(title, body, selectedCategory, isUrgent, expirationDate) { onBack() } },
                enabled = title.isNotBlank() && body.isNotBlank() && !isSending,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
            ) {
                if (isSending) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Campaign, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Publicar Aviso", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }

    if (message != null) {
        AlertDialog(onDismissRequest = { vm.clearMessage() }, confirmButton = { TextButton(onClick = { vm.clearMessage() }) { Text("Aceptar") } }, title = { Text("Aviso") }, text = { Text(message!!) })
    }
}

@Composable
private fun LabelText(text: String) {
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
}
