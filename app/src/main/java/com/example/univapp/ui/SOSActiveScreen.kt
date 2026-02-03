package com.example.univapp.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.location.LocationHelper

@Composable
fun SOSActiveScreen(
    viewModel: SOSViewModel,
    onCancel: () -> Unit = {},
    onCallEmergencies: () -> Unit = {},
    settingsVm: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val dark by settingsVm.darkMode.collectAsState()
    val locationHelper = remember { LocationHelper(context) }
    
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF8F9FA)
    val titleColor = if (dark) Color.White else Color(0xFF0F172A)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val badgeBg = if (dark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    val badgeText = if (dark) Color(0xFFE2E8F0) else Color(0xFF334155)
    val pulseColor = if (dark) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFFFFE4E6)

    // Launcher para pedir permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) viewModel.startTracking()
    }

    LaunchedEffect(Unit) {
        if (locationHelper.hasPermission()) viewModel.startTracking()
        else permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "scale"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).padding(top = 20.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { viewModel.stopTracking(); onCancel() }, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text(text = "Detener SOS", color = Color(0xFFEF4444), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // SOS Animado
            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(240.dp).scale(pulseScale).background(pulseColor, CircleShape))
                Box(modifier = Modifier.size(180.dp).background(pulseColor, CircleShape))
                Surface(modifier = Modifier.size(140.dp), shape = CircleShape, color = Color(0xFFEF4444), shadowElevation = 8.dp) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "SOS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 32.sp)
                        Text(text = "ACTIVO", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
            Text(text = "¡La ayuda está en camino!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = titleColor, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Tu ubicación real está siendo compartida con el centro de control en tiempo real.", fontSize = 16.sp, color = subtitleColor, textAlign = TextAlign.Center, lineHeight = 24.sp)

            Spacer(modifier = Modifier.height(40.dp))

            // Tracking Badge
            Surface(color = badgeBg, shape = RoundedCornerShape(24.dp)) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.FiberManualRecord, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    val isTracking by viewModel.isTracking.collectAsState()
                    Text(text = if (isTracking) "Rastreando ubicación en vivo" else "Iniciando rastreo...", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = badgeText)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onCallEmergencies, modifier = Modifier.fillMaxWidth().height(60.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))) {
                Icon(Icons.Default.Call, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Llamar a Emergencias (911)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
