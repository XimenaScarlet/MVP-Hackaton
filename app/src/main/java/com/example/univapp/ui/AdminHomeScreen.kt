@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkBg = Color(0xFF1C1C2E)
private val Pink = Color(0xFFE91E63)
private val Purple = Color(0xFF9C27B0)

private data class AdminItem(val title: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun AdminHomeScreen(
    onGoAlumnos: () -> Unit,
    onGoMaterias: () -> Unit,
    onGoGrupos: () -> Unit,
    onGoHorarios: () -> Unit,
    onGoProfesores: () -> Unit,
    onGoAnnouncements: (() -> Unit)? = null,
    onLogout: () -> Unit,
    userName: String = "Admin"
) {
    val items = listOf(
        AdminItem("Alumnos",    Icons.Outlined.People,    onGoAlumnos),
        AdminItem("Materias",   Icons.Outlined.MenuBook,  onGoMaterias),
        AdminItem("Grupos",     Icons.Outlined.Groups,    onGoGrupos),
        AdminItem("Horarios",   Icons.Outlined.Schedule,  onGoHorarios),
        AdminItem("Profesores", Icons.Outlined.School,    onGoProfesores),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(Purple, Pink)),
                        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Hola,", color = Color.White, fontSize = 24.sp)
                            Text(userName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir", tint = Color.White)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Panel de administrador", color = Color.White.copy(alpha = 0.8f))
                    Text("Accede a los módulos", color = Color.White.copy(alpha = 0.8f))
                }
            }

            // Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { item -> 
                    AdminCard(title = item.title, icon = item.icon, onClick = item.onClick) 
                }
            }
        }
    }
}

@Composable
private fun AdminCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C4E)),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0x33E91E63)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Pink, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(title, color = Color.White.copy(alpha = 0.8f))
        }
    }
}
