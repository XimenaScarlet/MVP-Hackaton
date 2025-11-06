@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CampusRoutesScreen(
    onTapSaltillo: () -> Unit = {},
    onTapRamos: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Rutas, 1: Mapa, 2: Ajustes

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.DirectionsBus,
                            contentDescription = null,
                            tint = Color(0xFF0F172A)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Selecciona tu Ruta")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(tonalElevation = 0.dp) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Outlined.DirectionsBus, null) },
                    label = { Text("Rutas") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Outlined.Map, null) },
                    label = { Text("Mapa") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Outlined.Settings, null) },
                    label = { Text("Ajustes") }
                )
            }
        }
    ) { pv ->
        when (selectedTab) {
            0 -> RoutesList(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF6F8FA))
                    .padding(pv)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                onTapSaltillo = onTapSaltillo,
                onTapRamos = onTapRamos
            )
            1 -> MapPlaceholder(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF6F8FA))
                    .padding(pv)
                    .padding(16.dp)
            )
            2 -> SettingsPlaceholder(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF6F8FA))
                    .padding(pv)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun RoutesList(
    modifier: Modifier = Modifier,
    onTapSaltillo: () -> Unit,
    onTapRamos: () -> Unit
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        RouteCard(title = "Ruta Saltillo", onClick = onTapSaltillo)
        RouteCard(title = "Ruta Ramos", onClick = onTapRamos)
    }
}

@Composable
private fun RouteCard(
    title: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF6EB7AE).copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DirectionsBus,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color(0xFF0F172A))
                Spacer(Modifier.height(4.dp))
                Text("Toque para ver paradas y horarios", color = Color(0xFF6B7280), fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun MapPlaceholder(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text("Mapa", style = MaterialTheme.typography.titleMedium, color = Color(0xFF0F172A))
        Spacer(Modifier.height(12.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().height(280.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aquí va el mapa", color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
private fun SettingsPlaceholder(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Ajustes", style = MaterialTheme.typography.titleMedium, color = Color(0xFF0F172A))
        ElevatedCard(shape = RoundedCornerShape(16.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notificaciones de ruta")
                Spacer(Modifier.weight(1f))
                var checked by remember { mutableStateOf(true) }
                Switch(checked = checked, onCheckedChange = { checked = it })
            }
        }
        ElevatedCard(shape = RoundedCornerShape(16.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Usar ubicación en tiempo real")
                Spacer(Modifier.weight(1f))
                var checked by remember { mutableStateOf(false) }
                Switch(checked = checked, onCheckedChange = { checked = it })
            }
        }
    }
}
