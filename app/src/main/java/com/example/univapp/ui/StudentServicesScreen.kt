package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudentServicesScreen(
    onBack: () -> Unit = {},
    onOpenInfo: () -> Unit = {},
    onOpenProcedures: () -> Unit = {},
    onOpenRequests: () -> Unit = {},
    onOpenDocuments: () -> Unit = {},
    onOpenDigitalID: () -> Unit = {},
    settingsVm: SettingsViewModel = viewModel()
) {
    val isDarkMode by settingsVm.darkMode.collectAsState()

    // Dynamic Colors
    val bgColor = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF8F9FB)
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val titleColor = if (isDarkMode) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF74777F)
    val iconBoxColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFE8F0FE)
    val iconTintColor = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF0056D2)

    val item1 = ServiceItem("Información", "Ver perfil escolar", Icons.Default.Info, onOpenInfo)
    val item2 = ServiceItem("Trámites", "Iniciar proceso", Icons.Default.Description, onOpenProcedures)
    val item3 = ServiceItem("Mis Solicitudes", "Seguimiento", Icons.Default.HistoryEdu, onOpenRequests)
    val item4 = ServiceItem("Credencial Digital", "Ver identificación", Icons.Default.Badge, onOpenDigitalID)
    val itemCenter = ServiceItem("Documentos", "Descargas PDF", Icons.Default.Folder, onOpenDocuments)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Atrás",
                            modifier = Modifier.size(32.dp),
                            tint = if (isDarkMode) Color.White else Color(0xFF004696)
                        )
                    }
                    Text("Escolares", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = titleColor)
                }
            }

            // Contenedor centrado verticalmente
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Fila 1
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            ServiceCard(item1, cardBg, titleColor, subtitleColor, iconBoxColor, iconTintColor)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ServiceCard(item2, cardBg, titleColor, subtitleColor, iconBoxColor, iconTintColor)
                        }
                    }
                    
                    // Fila 2
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            ServiceCard(item3, cardBg, titleColor, subtitleColor, iconBoxColor, iconTintColor)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ServiceCard(item4, cardBg, titleColor, subtitleColor, iconBoxColor, iconTintColor)
                        }
                    }

                    // Fila 3: Documentos centrado (50% del ancho)
                    Box(modifier = Modifier.fillMaxWidth(0.5f)) {
                        ServiceCard(itemCenter, cardBg, titleColor, subtitleColor, iconBoxColor, iconTintColor)
                    }
                }
            }
        }
    }
}

private data class ServiceItem(
    val title: String, 
    val subtitle: String, 
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

@Composable
private fun ServiceCard(
    item: ServiceItem,
    cardBg: Color,
    titleColor: Color,
    subtitleColor: Color,
    iconBoxColor: Color,
    iconTintColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable { item.onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(iconBoxColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = item.icon, contentDescription = null, tint = iconTintColor, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = item.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = titleColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.subtitle, fontSize = 13.sp, color = subtitleColor)
        }
    }
}
