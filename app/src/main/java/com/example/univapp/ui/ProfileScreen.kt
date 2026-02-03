@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.ui.profile.ProfileViewModel
import com.example.univapp.ui.util.qrBitmap

@Composable
fun ProfileScreen(
    onBack: (() -> Unit)? = null,
    settingsVm: SettingsViewModel = viewModel()
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val vm: ProfileViewModel = viewModel()
    val perfil by vm.perfil.collectAsState()
    val err by vm.err.collectAsState()
    val dark by settingsVm.darkMode.collectAsState()
    val mostrarCorreo by settingsVm.showEmail.collectAsState()
    var showQr by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.load() }

    // Dynamic Colors
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val dividerColor = if (dark) Color(0xFF334155) else Color(0xFFF1F3F4)
    val iconBtnBg = if (dark) Color(0xFF334155) else Color.White

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 12.dp, 8.dp, 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBack?.invoke() ?: backDispatcher?.onBackPressed() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Atrás",
                        modifier = Modifier.size(30.dp),
                        tint = titleColor
                    )
                }
                Text(
                    text = "Mi perfil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            }
        },
        bottomBar = {
            perfil?.let { p ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp, 24.dp, 24.dp, 24.dp)
                ) {
                    Button(
                        onClick = { showQr = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFF6366F1)),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Mostrar QR", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { pv ->
        when {
            err != null -> Box(Modifier.fillMaxSize().padding(pv), contentAlignment = Alignment.Center) {
                Text(text = err ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
            }
            perfil == null -> Box(Modifier.fillMaxSize().padding(pv), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            else -> {
                val p = perfil ?: return@Scaffold
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv),
                    contentPadding = PaddingValues(24.dp, 16.dp, 24.dp, 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(Modifier.height(10.dp))
                        
                        // Profile Avatar
                        Surface(
                            modifier = Modifier.size(140.dp),
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, dividerColor),
                            color = cardBg
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = if (dark) Color(0xFF818CF8) else Color(0xFF818CF8),
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        Text(
                            text = p.nombre.ifBlank { "Nombre Alumno" },
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = titleColor,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = p.carrera.ifBlank { "Tecnologías de la Información e Innovación Digital" },
                            fontSize = 15.sp,
                            color = subtitleColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp, 6.dp, 16.dp, 0.dp)
                        )
                        
                        Spacer(Modifier.height(32.dp))
                        
                        // Semester & Group Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 20.dp, 0.dp, 20.dp)
                                    .border(1.dp, dividerColor, RoundedCornerShape(24.dp)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatItem(Modifier.weight(1f), "SEMESTRE", p.semestre.ifBlank { "9°" }, titleColor)
                                Box(modifier = Modifier.height(40.dp).width(1.dp).background(dividerColor))
                                StatItem(Modifier.weight(1f), "GRUPO", p.grupoId.ifBlank { "IDGSA" }, titleColor)
                            }
                        }
                        
                        Spacer(Modifier.height(32.dp))
                        
                        // Academic Data List
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(cardBg, RoundedCornerShape(28.dp))
                                .padding(20.dp, 20.dp, 20.dp, 20.dp)
                        ) {
                            Text(
                                "DATOS ACADÉMICOS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = subtitleColor,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp)
                            )
                            
                            InfoRow(Icons.Outlined.Badge, "Matrícula", p.matricula, if (dark) Color(0xFF1E3A8A) else Color(0xFFEEF2FF), Color(0xFF3B82F6), titleColor, subtitleColor)
                            
                            if (mostrarCorreo) {
                                InfoRow(Icons.Outlined.AlternateEmail, "Correo institucional", p.correo, if (dark) Color(0xFF064E3B) else Color(0xFFE2F9E9), Color(0xFF10B981), titleColor, subtitleColor)
                            }
                            
                            InfoRow(Icons.Outlined.Smartphone, "Teléfono", p.telefono ?: "8441321985", if (dark) Color(0xFF7C2D12) else Color(0xFFFFF7ED), Color(0xFFF97316), titleColor, subtitleColor)
                            InfoRow(Icons.Outlined.LocationOn, "Dirección", p.direccion ?: "Saltillo, Coahuila", if (dark) Color(0xFF4C1D95) else Color(0xFFF5F3FF), Color(0xFF8B5CF6), titleColor, subtitleColor)
                            InfoRow(Icons.Outlined.Cake, "Fecha de nacimiento", p.fechaNacimiento ?: "01/09/2004", if (dark) Color(0xFF881337) else Color(0xFFFFF1F2), Color(0xFFF43F5E), titleColor, subtitleColor)
                        }
                        
                        Spacer(Modifier.height(100.dp))
                    }
                }
                
                if (showQr) {
                    val qrPayload = "univapp://alumno?id=${p.matricula}&v=1"
                    AlertDialog(
                        onDismissRequest = { showQr = false },
                        containerColor = cardBg,
                        titleContentColor = titleColor,
                        confirmButton = { TextButton(onClick = { showQr = false }) { Text("Cerrar") } },
                        title = { Text("Tu código QR") },
                        text = {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Image(
                                    bitmap = qrBitmap(qrPayload, size = 512),
                                    contentDescription = "QR",
                                    modifier = Modifier.size(240.dp)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(modifier: Modifier, label: String, value: String, textColor: Color) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
        Spacer(Modifier.height(4.dp))
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = if (textColor == Color.White) Color(0xFF60A5FA) else Color(0xFF6366F1))
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String, bgColor: Color, iconColor: Color, titleColor: Color, subtitleColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 12.dp, 0.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(12.dp),
            color = bgColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = subtitleColor)
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = titleColor)
        }
    }
}
