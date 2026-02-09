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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.CameraUpdateFactory
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
    var selectedAlertId by remember { mutableStateOf<String?>(null) }
    
    // Encontramos la alerta actualizada basándonos en el ID seleccionado
    val selectedAlert = alerts.find { it.alumnoId == selectedAlertId }

    LaunchedEffect(Unit) {
        viewModel.startListening()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitoreo SOS Activo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White)
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 60.dp, horizontal = 24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Illustration
                            Box(
                                modifier = Modifier.size(220.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Background Circle
                                Surface(
                                    modifier = Modifier.size(170.dp),
                                    shape = CircleShape,
                                    color = Color(0xFFF2F4F7)
                                ) {}
                                
                                // Bell icon
                                Icon(
                                    imageVector = Icons.Default.NotificationsOff,
                                    contentDescription = null,
                                    tint = Color(0xFFD0D5DD),
                                    modifier = Modifier.size(90.dp)
                                )
                                
                                // Floating elements
                                // Green dot
                                Surface(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-60).dp, y = 50.dp),
                                    shape = CircleShape,
                                    color = Color(0xFF22C55E)
                                ) {}
                                
                                // Green shield card
                                Surface(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-15).dp, y = 30.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White,
                                    shadowElevation = 8.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = Color(0xFF22C55E),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                
                                // Blue verified card
                                Surface(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .align(Alignment.BottomStart)
                                        .offset(x = 15.dp, y = (-50).dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White,
                                    shadowElevation = 8.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = null,
                                            tint = Color(0xFF3B82F6),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Text(
                                text = "Todo está bajo control",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1D2939)
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "No hay alarmas activas en este momento.",
                                fontSize = 18.sp,
                                color = Color(0xFF667085),
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                            
                            Spacer(modifier = Modifier.height(48.dp))
                            
                            Button(
                                onClick = { viewModel.startListening() },
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F4F7)),
                                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFF344054),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Actualizar estado",
                                    color = Color(0xFF344054),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
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
                        SosAlertCard(alert) { selectedAlertId = alert.alumnoId }
                    }
                }
            }
        }
    }

    selectedAlert?.let { alert ->
        SosMapDialog(alert) { selectedAlertId = null }
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
                val displayName = if (alert.alumnoNombre.isNotBlank()) alert.alumnoNombre else alert.alumnoId
                Text(displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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

    // Efecto para mover la cámara si la ubicación cambia mientras el diálogo está abierto
    LaunchedEffect(alert.location) {
        alert.location?.let {
            val newPos = LatLng(it.latitude, it.longitude)
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(newPos, 17f))
        }
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
                        val displayName = if (alert.alumnoNombre.isNotBlank()) alert.alumnoNombre else "UBICACIÓN SOS"
                        Text(displayName.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterEnd)) {
                        Text("CERRAR", color = Color.White)
                    }
                }
                
                Box(modifier = Modifier.weight(1f)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        if (alert.location != null) {
                            Marker(
                                state = MarkerState(position = position),
                                title = alert.alumnoNombre.ifBlank { alert.alumnoId }
                            )
                        }
                    }
                    
                    if (alert.location == null) {
                        Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.8f)), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFFEF4444))
                                Spacer(Modifier.height(16.dp))
                                Text("Esperando señal GPS...", color = Color.Gray)
                            }
                        }
                    }
                }
                
                Box(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(
                        "Atención: El alumno requiere asistencia inmediata. El mapa se actualizará automáticamente en cuanto se reciba la señal GPS.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
