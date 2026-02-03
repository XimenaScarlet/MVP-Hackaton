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
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    userName: String,
    onGoSubjects: () -> Unit,
    onGoTimetable: () -> Unit,
    onGoStudentServices: () -> Unit,
    onGoAnnouncements: () -> Unit,
    onGoHealth: () -> Unit,
    onGoProfile: () -> Unit,
    onGoSettings: () -> Unit,
    onLogout: () -> Unit,
    settingsVm: SettingsViewModel = viewModel()
) {
    val isDarkMode by settingsVm.darkMode.collectAsState()
    
    // Colores dinámicos según el modo
    val bgColor = if (isDarkMode) Color(0xFF121726) else Color(0xFFF8F9FB)
    val tileBg = if (isDarkMode) Color(0xFF1C2236) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF1A1C1E)
    val subtitleColor = if (isDarkMode) Color.White.copy(alpha = 0.85f) else Color(0xFF74777F)
    val tileLabelColor = if (isDarkMode) Color(0xFF8BA0B3) else Color(0xFF44474E)

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        // Background Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFFF6DB3), Color(0xFFFD8D6A))),
                    shape = RoundedCornerShape(bottomStart = 80.dp, bottomEnd = 80.dp)
                )
        )

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(24.dp))
            
            // Header: User Greeting & Logout
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onGoProfile() }
                ) {
                    Text("Hola,", color = Color.White.copy(alpha = 0.9f), fontSize = 18.sp)
                    Text(
                        text = userName.ifBlank { "Alumno" }, 
                        color = Color.White, 
                        fontSize = 32.sp, 
                        fontWeight = FontWeight.Black
                    )
                }
                
                Surface(
                    onClick = onLogout,
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.Logout, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Salir", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            
            // Title
            Text("Panel del alumno", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Accede a tus módulos", color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp)
            
            Spacer(Modifier.height(24.dp))

            val tiles = listOf(
                DashTile("Materias", Icons.Outlined.MenuBook, listOf(Color(0xFF66D1FF), Color(0xFF3AA4FF)), onGoSubjects),
                DashTile("Horario", Icons.Outlined.Schedule, listOf(Color(0xFFFFB067), Color(0xFFFF7A59)), onGoTimetable),
                DashTile("Escolares", Icons.Outlined.School, listOf(Color(0xFF6DA8FF), Color(0xFF5C7CFF)), onGoStudentServices),
                DashTile("Anuncios", Icons.Outlined.Campaign, listOf(Color(0xFFFF7BB0), Color(0xFFFF5F86)), onGoAnnouncements),
                DashTile("Salud", Icons.Outlined.HealthAndSafety, listOf(Color(0xFF74E39B), Color(0xFF29C46D)), onGoHealth),
                DashTile("Configuración", Icons.Outlined.Settings, listOf(Color(0xFFBAC4D7), Color(0xFF8EA7C4)), onGoSettings)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize().padding(bottom = 20.dp)
            ) {
                items(tiles) { t ->
                    TileCard(t.title, t.icon, t.gradient, tileBg, tileLabelColor, t.onClick)
                }
            }
        }
    }
}

private data class DashTile(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val gradient: List<Color>, val onClick: () -> Unit)

@Composable
private fun TileCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, bubbleGradient: List<Color>, tileColor: Color, labelColor: Color, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = tileColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp), 
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(bubbleGradient)),
                contentAlignment = Alignment.Center
            ) { 
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp)) 
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = title, 
                color = labelColor, 
                fontSize = 16.sp, 
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
