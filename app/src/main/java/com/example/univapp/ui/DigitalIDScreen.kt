package com.example.univapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.R

@Composable
fun DigitalIDScreen(
    onBack: () -> Unit = {},
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()

    // Dynamic Colors
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF8F9FB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color.Black
    val textColor = if (dark) Color.White else Color.Black
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF9CA3AF)
    val universityTextColor = if (dark) Color(0xFF60A5FA) else Color(0xFF2563EB)
    val dividerColor = if (dark) Color(0xFF334155) else Color(0xFFF3F4F6)
    val bottomInfoBg = if (dark) Color(0xFF0F172A) else Color(0xFFF9FAFB)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                        text = "Credencial Digital",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Digital ID Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    // ID Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(universityTextColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.School,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "UNIVERSIDAD",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = universityTextColor,
                                    lineHeight = 12.sp
                                )
                                Text(
                                    "TECNOLÓGICA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = universityTextColor,
                                    lineHeight = 12.sp
                                )
                                Text(
                                    "DE COAHUILA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = universityTextColor,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                        
                        Text(
                            "ESTUDIANTE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = subtitleColor,
                            letterSpacing = 1.sp
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = dividerColor)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Profile Image with Verification Badge
                    Box(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Surface(
                            modifier = Modifier.size(140.dp),
                            shape = CircleShape,
                            color = if (dark) Color(0xFF334155) else Color(0xFFE8F0FE)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                modifier = Modifier.padding(30.dp),
                                tint = universityTextColor
                            )
                        }
                        
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .offset(x = (-4).dp, y = (-4).dp),
                            shape = CircleShape,
                            color = if (dark) Color(0xFF1E293B) else Color.White
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Verificado",
                                tint = universityTextColor,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Student Info
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Cinthia Lizette Pinedo Acosta",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "TI e Innovación Digital",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = universityTextColor
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row {
                            Text(
                                "ID: ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = subtitleColor
                            )
                            Text(
                                "202400123",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Bottom info (Cuatrimestre & Vigencia)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bottomInfoBg)
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "CUATRIMESTRE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = subtitleColor
                            )
                            Text(
                                "9",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "VIGENCIA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = subtitleColor
                            )
                            Text(
                                "2025 - 2026",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}
