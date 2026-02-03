package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
fun StudentProceduresScreen(
    onBack: () -> Unit = {},
    onOpenEnrollmentCertificate: () -> Unit = {},
    onOpenKardex: () -> Unit = {},
    onOpenIDReplacement: () -> Unit = {},
    onOpenInternshipCertificate: () -> Unit = {},
    onOpenBajaTemporal: () -> Unit = {},
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()

    // Dynamic Colors
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF8F9FB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF74777F)
    val iconBoxColor = if (dark) Color(0xFF334155) else Color(0xFFE8F0FE)
    val iconTintColor = if (dark) Color(0xFF60A5FA) else Color(0xFF0056D2)

    val procedures = listOf(
        ProcedureItem("Constancia de estudios", "Solicitud de documento oficial", Icons.Default.Description, onOpenEnrollmentCertificate),
        ProcedureItem("Kardex / Historial académico", "Consulta y descarga de notas", Icons.Default.BarChart, onOpenKardex),
        ProcedureItem("Reposición de credencial", "Extravío o daño de identificación", Icons.Default.Badge, onOpenIDReplacement),
        ProcedureItem("Constancia para Prácticas", "Documento para vinculación laboral", Icons.Default.BusinessCenter, onOpenInternshipCertificate),
        ProcedureItem("Baja temporal", "Suspensión de estudios", Icons.Default.Cancel, onOpenBajaTemporal)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (dark) Color(0xFF1E293B) else Color.White,
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Atrás",
                            modifier = Modifier.size(32.dp),
                            tint = titleColor
                        )
                    }
                    Text(
                        text = "Trámites",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                procedures.forEach { item ->
                    ProcedureCard(item, cardBg, titleColor, subtitleColor, iconBoxColor, iconTintColor)
                }
            }
        }
    }
}

private data class ProcedureItem(
    val title: String, 
    val subtitle: String, 
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

@Composable
private fun ProcedureCard(
    item: ProcedureItem,
    cardBg: Color,
    titleColor: Color,
    subtitleColor: Color,
    iconBoxColor: Color,
    iconTintColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBoxColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Text(
                    text = item.subtitle,
                    fontSize = 13.sp,
                    color = subtitleColor
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = subtitleColor.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
