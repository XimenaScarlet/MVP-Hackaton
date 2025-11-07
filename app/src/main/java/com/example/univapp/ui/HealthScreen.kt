@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.PsychologyAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------- Pantalla principal ---------- */

@Composable
fun HealthScreen(
    onBack: () -> Unit = {},
    onOpenPsychSupport: () -> Unit = {},
    onOpenMedicalSupport: () -> Unit = {}
) {
    val items = listOf(
        HealthCardData(
            title = "Servicio Médico",
            line1 = "Dr. Jorge Pérez • Ext. 234",
            line2 = "Edificio B • Planta baja • Consultorio 2",
            pill = "Atención general",
            icon = Icons.Outlined.LocalHospital,
            bg = Color(0xFFAAF27F),
            fg = Color(0xFF0B3D17),
            onClick = onOpenMedicalSupport
        ),
        HealthCardData(
            title = "Apoyo Psicológico",
            line1 = "Mtra. Ana Ramírez • Ext. 219",
            line2 = "Edificio C • Oficina de Bienestar • Cubículo 4",
            pill = "Citas confidenciales",
            icon = Icons.Outlined.PsychologyAlt,
            bg = Color(0xFFFF9AD5),
            fg = Color(0xFF3D0B2A),
            onClick = onOpenPsychSupport
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Salud y Bienestar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { pv ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F8FA))
                .padding(pv),
            contentAlignment = Alignment.Center // 🔹 centradas verticalmente
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items.forEach { data ->
                    HealthBigCard(data)
                }
            }
        }
    }
}

/* ---------- Datos y componente ---------- */

private data class HealthCardData(
    val title: String,
    val line1: String,
    val line2: String,
    val pill: String,
    val icon: ImageVector,
    val bg: Color,
    val fg: Color,
    val onClick: () -> Unit
)

@Composable
private fun HealthBigCard(data: HealthCardData) {
    ElevatedCard(
        onClick = data.onClick,
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = data.bg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícono
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = data.fg,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            // Título
            Text(
                text = data.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = data.fg,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            // Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    data.pill,
                    fontSize = 12.sp,
                    color = data.fg,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(14.dp))

            // Info
            Text(
                text = data.line1,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = data.fg,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = data.line2,
                fontSize = 14.sp,
                color = data.fg.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
