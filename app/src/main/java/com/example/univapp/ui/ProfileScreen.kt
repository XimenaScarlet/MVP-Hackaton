@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.ui.profile.ProfileViewModel
import com.example.univapp.ui.util.AppScaffold

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

    LaunchedEffect(Unit) { vm.load() }

    // Dynamic Colors
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val dividerColor = if (dark) Color(0xFF334155) else Color(0xFFF1F3F4)

    AppScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi perfil", fontWeight = FontWeight.Bold, color = titleColor) },
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() ?: backDispatcher?.onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Atrás",
                            modifier = Modifier.size(30.dp),
                            tint = titleColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = bgColor)
            )
        }
    ) { pv ->
        Box(modifier = Modifier.fillMaxSize().background(bgColor).padding(pv)) {
            when {
                err != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = err ?: "Error", color = MaterialTheme.colorScheme.error)
                }
                perfil == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                else -> {
                    val p = perfil!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp, 16.dp, 24.dp, 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Surface(
                                modifier = Modifier.size(120.dp),
                                shape = CircleShape,
                                color = cardBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, dividerColor)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF818CF8),
                                        modifier = Modifier.size(60.dp)
                                    )
                                }
                            }
                            
                            Spacer(Modifier.height(20.dp))
                            
                            Text(
                                text = p.nombre,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = titleColor,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(Modifier.height(32.dp))
                            
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(cardBg, RoundedCornerShape(24.dp))
                                    .padding(20.dp)
                            ) {
                                InfoRow(Icons.Outlined.Badge, "Matrícula", p.matricula, if (dark) Color(0xFF1E3A8A) else Color(0xFFEEF2FF), Color(0xFF3B82F6), titleColor, subtitleColor)
                                InfoRow(Icons.Outlined.AlternateEmail, "Correo", p.correo, if (dark) Color(0xFF064E3B) else Color(0xFFE2F9E9), Color(0xFF10B981), titleColor, subtitleColor)
                                InfoRow(Icons.Outlined.Cake, "Fecha de nacimiento", p.fechaNacimiento ?: "No disponible", if (dark) Color(0xFF881337) else Color(0xFFFFF1F2), Color(0xFFF43F5E), titleColor, subtitleColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String, bgColor: Color, iconColor: Color, titleColor: Color, subtitleColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = bgColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = subtitleColor)
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = titleColor)
        }
    }
}
