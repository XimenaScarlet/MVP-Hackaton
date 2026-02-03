package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudentRequestsScreen(
    onBack: () -> Unit = {},
    onStartProcedure: () -> Unit = {},
    vm: StudentProceduresViewModel,
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    val requests by vm.allRequests.collectAsState()
    val isLoading by vm.loading.collectAsState()

    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF8F9FB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF74777F)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = cardBg,
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
                            tint = if (dark) Color.White else Color(0xFF004696)
                        )
                    }
                    Text(
                        text = "Mis Solicitudes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
            }

            if (isLoading && requests.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2563EB))
                }
            } else if (requests.isEmpty()) {
                EmptyRequestsState(onStartProcedure, dark, titleColor, subtitleColor)
            } else {
                RequestsList(requests, cardBg, titleColor, subtitleColor, dark)
            }
        }
    }
}

@Composable
private fun EmptyRequestsState(onStartProcedure: () -> Unit, dark: Boolean, titleColor: Color, subtitleColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(if (dark) Color(0xFF1E293B) else Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HistoryEdu,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(80.dp)
                )
            }
            Surface(
                modifier = Modifier.size(56.dp).offset(x = 10.dp, y = 10.dp),
                shape = CircleShape,
                color = if (dark) Color(0xFF334155) else Color(0xFFE8F0FE),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Search, null, tint = Color(0xFF2563EB), modifier = Modifier.size(28.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Sin solicitudes aún",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Aquí aparecerán los trámites que realices, como constancias o kardex.",
            fontSize = 16.sp,
            color = subtitleColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onStartProcedure,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF2563EB)),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddCircle, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Iniciar un trámite", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RequestsList(requests: List<StudentProceduresViewModel.RequestRecord>, cardBg: Color, titleColor: Color, subtitleColor: Color, dark: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(requests) { item ->
            RequestCard(item, cardBg, titleColor, subtitleColor, dark)
        }
    }
}

@Composable
private fun RequestCard(item: StudentProceduresViewModel.RequestRecord, cardBg: Color, titleColor: Color, subtitleColor: Color, dark: Boolean) {
    val (statusColor, statusBg) = when (item.status) {
        "COMPLETADO" -> Color(0xFF10B981) to if (dark) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFDCFCE7)
        "PENDIENTE" -> Color(0xFFF59E0B) to if (dark) Color(0xFFF59E0B).copy(alpha = 0.15f) else Color(0xFFFEF3C7)
        else -> Color(0xFF3B82F6) to if (dark) Color(0xFF3B82F6).copy(alpha = 0.15f) else Color(0xFFDBEAFE)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (dark) Color(0xFF334155) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.title.contains("Credencial")) Icons.Default.Badge else Icons.Default.Description,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                    
                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = item.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Text(
                    text = "Folio: ${item.folio}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (dark) Color(0xFFE2E8F0) else Color(0xFF4B5563),
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                Text(
                    text = "Solicitado el ${item.date}",
                    fontSize = 13.sp,
                    color = subtitleColor
                )
            }
        }
    }
}
