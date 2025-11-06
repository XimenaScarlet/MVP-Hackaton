package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    userName: String,
    onGoSubjects: () -> Unit,
    onGoGrades: () -> Unit,
    onGoTimetable: () -> Unit,
    onGoRoutes: () -> Unit,
    onGoAnnouncements: () -> Unit,
    onGoHealth: () -> Unit,
    onGoProfile: () -> Unit,
    onGoSettings: () -> Unit,
    onLogout: () -> Unit
) {
    // Colores base (estilo imagen)
    val bgDark = Color(0xFF121726)
    val tileBg = Color(0xFF1C2236)
    val labelDim = Color(0xFF8BA0B3)

    // Header con gradiente rosado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFF6DB3),
                            Color(0xFFFD8D6A)
                        )
                    ),
                    // gran redondeo abajo para emular la “gota” del mock
                    shape = RoundedCornerShape(bottomStart = 80.dp, bottomEnd = 80.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))

            // Top bar: saludo y logout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Hola,",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 18.sp
                    )
                    Text(
                        userName.ifBlank { "Alumno" },
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                FilledTonalButton(
                    onClick = onLogout,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0x33FFFFFF),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Outlined.Logout, contentDescription = "Cerrar sesión")
                    Spacer(Modifier.width(8.dp))
                    Text("Salir")
                }
            }

            Spacer(Modifier.height(22.dp))

            // Título principal (como “Classify transaction” del mock)
            Text(
                "Panel del alumno",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Accede a tus módulos",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )

            Spacer(Modifier.height(18.dp))

            // Grilla de 8 tarjetas
            val tiles = listOf(
                DashTile(
                    title = "Materias",
                    icon = Icons.Outlined.MenuBook,
                    gradient = listOf(Color(0xFF66D1FF), Color(0xFF3AA4FF)),
                    onClick = onGoSubjects
                ),
                DashTile(
                    title = "Calificaciones",
                    icon = Icons.Outlined.Leaderboard,
                    gradient = listOf(Color(0xFFB07CFF), Color(0xFF7D5BFF)),
                    onClick = onGoGrades
                ),
                DashTile(
                    title = "Horario",
                    icon = Icons.Outlined.Schedule,
                    gradient = listOf(Color(0xFFFFB067), Color(0xFFFF7A59)),
                    onClick = onGoTimetable
                ),
                DashTile(
                    title = "Transporte",
                    icon = Icons.Outlined.DirectionsBus,
                    gradient = listOf(Color(0xFF6DA8FF), Color(0xFF5C7CFF)),
                    onClick = onGoRoutes
                ),
                DashTile(
                    title = "Anuncios",
                    icon = Icons.Outlined.Campaign,
                    gradient = listOf(Color(0xFFFF7BB0), Color(0xFFFF5F86)),
                    onClick = onGoAnnouncements
                ),
                DashTile(
                    title = "Salud",
                    icon = Icons.Outlined.HealthAndSafety,
                    gradient = listOf(Color(0xFF74E39B), Color(0xFF29C46D)),
                    onClick = onGoHealth
                ),
                DashTile(
                    title = "Perfil",
                    icon = Icons.Outlined.Person,
                    gradient = listOf(Color(0xFF67D7FF), Color(0xFF3CC0F6)),
                    onClick = onGoProfile
                ),
                DashTile(
                    title = "Configuración",
                    icon = Icons.Outlined.Settings,
                    gradient = listOf(Color(0xFFBAC4D7), Color(0xFF8EA7C4)),
                    onClick = onGoSettings
                )
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp)
            ) {
                items(tiles) { t ->
                    TileCard(
                        title = t.title,
                        icon = t.icon,
                        bubbleGradient = t.gradient,
                        tileColor = tileBg,
                        labelColor = labelDim,
                        onClick = t.onClick
                    )
                }
            }
        }
    }
}

private data class DashTile(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val gradient: List<Color>,
    val onClick: () -> Unit
)

@Composable
private fun TileCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bubbleGradient: List<Color>,
    tileColor: Color,
    labelColor: Color,
    onClick: () -> Unit
) {
    // Card cuadrada con esquinas grandes (estilo mock)
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = tileColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Burbuja circular con gradiente y el ícono al centro
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            if (bubbleGradient.size >= 2) bubbleGradient else listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Text(
                title,
                color = labelColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
