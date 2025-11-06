@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CardBg = Color(0xFFF4F7FA)
private val IconBlue = Color(0xFF1976D2)
private val ScreenBg = Color(0xFFF7F8FA)

private data class AdminItem(val title: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun AdminHomeScreen(
    onGoAlumnos: () -> Unit,
    onGoMaterias: () -> Unit,
    onGoGrupos: () -> Unit,
    onGoHorarios: () -> Unit,
    onGoProfesores: () -> Unit,
    onGoSettings: () -> Unit,
    onGoProfile: () -> Unit,
    onGoAnnouncements: (() -> Unit)? = null,
    onLogout: () -> Unit,
    userName: String = "Admin"
) {
    var showMenu by remember { mutableStateOf(false) }

    val items = listOf(
        AdminItem("Alumnos",       Icons.Outlined.People,   onGoAlumnos),
        AdminItem("Materias",      Icons.Outlined.MenuBook, onGoMaterias),
        AdminItem("Grupos",        Icons.Outlined.Groups,   onGoGrupos),
        AdminItem("Horarios",      Icons.Outlined.Schedule, onGoHorarios),
        AdminItem("Profesores",    Icons.Outlined.School,   onGoProfesores),
        AdminItem("Configuración", Icons.Outlined.Person,   onGoProfile), // o Settings (ajústalo a gusto)
        AdminItem("Perfil",        Icons.Outlined.Person,   onGoProfile),
    )

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE9EEF3)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(userName.firstOrNull()?.uppercase()?.toString() ?: "A")
                        }

                        Box {
                            Text(
                                text = "Hola, $userName",
                                fontSize = 20.sp,
                                modifier = Modifier.clickable { showMenu = true }
                            )
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(
                                    text = { Text("Cerrar sesión") },
                                    onClick = { showMenu = false; onLogout() }
                                )
                            }
                        }
                    }
                },
                actions = {
                    onGoAnnouncements?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Avisos")
                        }
                    }
                }
            )
        }
    ) { pv ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(pv)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item -> AdminCard(item.title, item.icon, item.onClick) }
        }
    }
}

@Composable
private fun AdminCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = IconBlue, modifier = Modifier.size(30.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, color = Color(0xFF2B2F33))
        }
    }
}
