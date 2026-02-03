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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun KardexSelectionScreen(
    onBack: () -> Unit = {},
    onViewHistory: () -> Unit = {},
    onRequestOfficial: () -> Unit = {},
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()

    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF8F9FB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF74777F)
    val iconBoxColor = if (dark) Color(0xFF334155) else Color(0xFFE8F0FE)
    val iconTintColor = if (dark) Color(0xFF60A5FA) else Color(0xFF0056D2)

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
                        text = "Kardex Académico",
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¿Qué deseas realizar?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Selecciona una opción para continuar con tu trámite académico.",
                    fontSize = 15.sp,
                    color = subtitleColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Opción 1: Ver historial
                KardexOptionCard(
                    title = "Ver historial académico",
                    subtitle = "Consulta tus calificaciones y avance curricular de forma inmediata.",
                    icon = Icons.Default.MenuBook,
                    cardBg = cardBg,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor,
                    iconBoxColor = iconBoxColor,
                    iconTintColor = iconTintColor,
                    onClick = onViewHistory
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Opción 2: Solicitar oficial
                KardexOptionCard(
                    title = "Solicitar kardex oficial",
                    subtitle = "Obtén un documento digital en formato PDF con validez oficial.",
                    icon = Icons.Default.PictureAsPdf,
                    cardBg = cardBg,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor,
                    iconBoxColor = iconBoxColor,
                    iconTintColor = iconTintColor,
                    onClick = onRequestOfficial
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "El tiempo de procesamiento para documentos oficiales puede variar según la carga administrativa.",
                    fontSize = 13.sp,
                    color = subtitleColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun KardexOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    cardBg: Color,
    titleColor: Color,
    subtitleColor: Color,
    iconBoxColor: Color,
    iconTintColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(iconBoxColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = subtitleColor,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
