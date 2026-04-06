package com.example.univapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.R
import com.example.univapp.ui.util.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalSupportScreen(
    onBack: () -> Unit = {},
    onBook: () -> Unit = {},
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    
    val darkBg = Color(0xFF0B101F)
    val cardBg = Color(0xFF131A2C)
    val accentColor = Color(0xFF818CF8)
    val subtitleColor = Color(0xFF94A3B8)

    AppScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Informacion Médico", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = darkBg)
            )
        }
    ) { pv ->
        Box(modifier = Modifier.fillMaxSize().background(darkBg).padding(pv)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Profile Image with Ring - REDUCED SIZE
                Box(contentAlignment = Alignment.Center) {
                    // Gradient Ring
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .border(
                                width = 3.dp,
                                brush = Brush.sweepGradient(listOf(accentColor, Color(0xFFC084FC), accentColor)),
                                shape = CircleShape
                            )
                    )
                    // Image
                    Image(
                        painter = painterResource(id = R.drawable.logo_3_este_si), 
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(125.dp)
                            .clip(CircleShape)
                    )
                    // Online Badge
                    Surface(
                        modifier = Modifier.align(Alignment.BottomCenter).offset(y = 8.dp),
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFF22C55E), CircleShape))
                            Spacer(Modifier.width(4.dp))
                            Text("ONLINE", color = subtitleColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(text = "Dr. Roberto Mendoza", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = "MÉDICO GENERAL", 
                        color = subtitleColor, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 11.sp, 
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sobre el médico - CENTERED
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("SOBRE EL MÉDICO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentColor, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = cardBg,
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "Especialista en medicina integral con más de 12 años de experiencia. Comprometido con el bienestar preventivo de la comunidad universitaria.",
                            fontSize = 14.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Información de Consulta - CENTERED
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("INFORMACIÓN DE CONSULTA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentColor, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ConsultInfoCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Schedule,
                            label = "ATENCIÓN",
                            value = "Lun - Vie\n08:00 AM - 04:00 PM",
                            iconTint = Color(0xFFC084FC)
                        )
                        ConsultInfoCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.LocationOn,
                            label = "UBICACIÓN",
                            value = "Consultorio A-102\n(Bloque Salud)",
                            iconTint = accentColor,
                            trailingIcon = Icons.Default.Map
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }

            // Floating Action Button Style - Bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, darkBg.copy(alpha = 0.9f))))
                    .padding(24.dp)
            ) {
                Button(
                    onClick = onBook,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF818CF8))
                ) {
                    Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("AGENDAR CITA", fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
fun ConsultInfoCard(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color,
    trailingIcon: ImageVector? = null
) {
    Surface(
        modifier = modifier.height(140.dp),
        color = Color(0xFF131A2C),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.05f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
                    }
                }
                if (trailingIcon != null) {
                    Icon(trailingIcon, null, tint = iconTint.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 1.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White, lineHeight = 16.sp, textAlign = TextAlign.Center)
        }
    }
}
