package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSosMapScreen(
    viewModel: AdminSosViewModel,
    onBack: () -> Unit
) {
    val alerts by viewModel.alerts.collectAsState()
    var selectedAlert by remember { mutableStateOf<SosAlert?>(null) }

    LaunchedEffect(Unit) {
        viewModel.startListening()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitoreo SOS Activo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEF4444),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (alerts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Warning, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("No hay alarmas activas en este momento", color = Color.Gray)
                    }
                }
            } else {
                Text(
                    text = "EMERGENCIAS DETECTADAS: ${alerts.size}",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFEF4444)
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(alerts) { alert ->
                        SosAlertCard(alert) { selectedAlert = alert }
                    }
                }
            }
        }
    }

    // MAP POPUP
    selectedAlert?.let { alert ->
        SosMapDialog(alert) { selectedAlert = null }
    }
}

@Composable
fun SosAlertCard(alert: SosAlert, onShowMap: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(50.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFFFEE2E2)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = Color(0xFFEF4444))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(alert.email.ifEmpty { "ID: ${alert.alumnoId}" }, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Estado: URGENTE", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onShowMap,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Mapa", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SosMapDialog(alert: SosAlert, onDismiss: () -> Unit) {
    val position = LatLng(alert.location?.latitude ?: 0.0, alert.location?.longitude ?: 0.0)
    val cameraPositionState = rememberCameraPositionState {
        this.position = CameraPosition.fromLatLngZoom(position, 17f)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            Column {
                Box(Modifier.fillMaxWidth().height(60.dp).background(Color(0xFFEF4444)).padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text("UBICACIÓN EN TIEMPO REAL", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterEnd)) {
                        Text("CERRAR", color = Color.White)
                    }
                }
                
                GoogleMap(
                    modifier = Modifier.weight(1f),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(state = MarkerState(position = position), title = alert.email.ifEmpty { alert.alumnoId })
                }
                
                Box(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(
                        "Atención: El alumno (${alert.email}) requiere asistencia inmediata en esta ubicación.",
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
