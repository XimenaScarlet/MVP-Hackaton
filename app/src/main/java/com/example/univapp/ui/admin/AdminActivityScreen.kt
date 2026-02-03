package com.example.univapp.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.ActivityLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminActivityScreen(
    onBack: () -> Unit,
    vm: AdminActivityViewModel = viewModel()
) {
    val logs by vm.logs.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registro de Actividad", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (logs.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { /* TODO: Filter */ },
                    containerColor = Color(0xFF673AB7),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF673AB7))
            } else if (logs.isEmpty()) {
                EmptyActivityState(onRefresh = { /* vm.refresh() */ })
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(logs, key = { it.id }) { log ->
                        ActivityLogItem(log = log)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyActivityState(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Visual circular con iconos
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
            // Círculo punteado de fondo
            androidx.compose.foundation.Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }
            
            // Icono de Historia central (Morado)
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF958BCE)
            )
            
            // Card de Check (Verde) - Superior Derecha
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .offset(x = 50.dp, y = (-40).dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFD1FADF),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF039855), modifier = Modifier.size(24.dp))
                }
            }
            
            // Card de Documento (Azul) - Inferior Izquierda
            Surface(
                modifier = Modifier
                    .size(50.dp)
                    .offset(x = (-60).dp, y = 40.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFD1E9FF),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Description, null, tint = Color(0xFF1570EF), modifier = Modifier.size(22.dp))
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        
        Text(
            "Sin actividad reciente",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF101828)
        )
        
        Spacer(Modifier.height(12.dp))
        
        Text(
            "Aún no se han realizado acciones en el sistema. Las actualizaciones aparecerán aquí.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF667085),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(Modifier.height(48.dp))
        
        OutlinedButton(
            onClick = onRefresh,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.height(48.dp).padding(horizontal = 16.dp),
            border = BorderStroke(1.dp, Color(0xFFEAECF0))
        ) {
            Icon(Icons.Outlined.Refresh, null, modifier = Modifier.size(18.dp), tint = Color(0xFF344054))
            Spacer(Modifier.width(8.dp))
            Text("Actualizar registro", color = Color(0xFF344054), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ActivityLogItem(log: ActivityLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = BorderStroke(1.dp, Color(0xFFEAECF0))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF673AB7), modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(log.type, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(log.timestamp, color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Text(log.description, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}
