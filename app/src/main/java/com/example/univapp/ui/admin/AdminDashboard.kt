package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminDashboard(
    onLogout: () -> Unit,
    onOpenAlumnos: () -> Unit = {},
    onOpenMaterias: () -> Unit = {},
    onOpenGrupos: () -> Unit = {},
    onOpenHorarios: () -> Unit = {},
    onOpenProfesores: () -> Unit = {},
    onOpenActivity: () -> Unit = {},
    onOpenAnnouncements: () -> Unit = {},
    onOpenSosMap: () -> Unit = {}
) {
    val bgDark = Color(0xFF121726)
    val tileBg = Color(0xFF1C2236)
    val labelDim = Color(0xFF8BA0B3)

    val items = listOf(
        AdminDashboardItem(
            title = "Alumnos",
            icon = Icons.Outlined.People,
            gradient = listOf(Color(0xFF66D1FF), Color(0xFF3AA4FF)),
            onClick = onOpenAlumnos
        ),
        AdminDashboardItem(
            title = "Materias",
            icon = Icons.AutoMirrored.Outlined.MenuBook,
            gradient = listOf(Color(0xFFB07CFF), Color(0xFF7D5BFF)),
            onClick = onOpenMaterias
        ),
        AdminDashboardItem(
            title = "Grupos",
            icon = Icons.Outlined.Groups,
            gradient = listOf(Color(0xFFFFB067), Color(0xFFFF7A59)),
            onClick = onOpenGrupos
        ),
        AdminDashboardItem(
            title = "Horarios",
            icon = Icons.Outlined.Schedule,
            gradient = listOf(Color(0xFF6DA8FF), Color(0xFF5C7CFF)),
            onClick = onOpenHorarios
        ),
        AdminDashboardItem(
            title = "Monitoreo SOS",
            icon = Icons.Outlined.Map,
            gradient = listOf(Color(0xFFEF4444), Color(0xFF991B1B)),
            onClick = onOpenSosMap
        ),
        AdminDashboardItem(
            title = "Anuncios",
            icon = Icons.Outlined.Campaign,
            gradient = listOf(Color(0xFF74E39B), Color(0xFF29C46D)),
            onClick = onOpenAnnouncements
        )
    )

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
                    Brush.verticalGradient(listOf(Color(0xFF9C27B0), Color(0xFFE91E63))),
                    shape = RoundedCornerShape(bottomStart = 80.dp, bottomEnd = 80.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenActivity)
                ) {
                    Text("Hola,", color = Color.White.copy(alpha = 0.9f), fontSize = 18.sp)
                    Text("Admin", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
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
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    Spacer(Modifier.width(8.dp))
                    Text("Salir")
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                "Panel de administrador",
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp)
            ) {
                itemsIndexed(items) { _, item ->
                    DashboardCard(
                        title = item.title,
                        icon = item.icon,
                        bubbleGradient = item.gradient,
                        tileColor = tileBg,
                        labelColor = labelDim,
                        onClick = item.onClick
                    )
                }
            }
        }
    }
}

private data class AdminDashboardItem(
    val title: String,
    val icon: ImageVector,
    val gradient: List<Color>,
    val onClick: () -> Unit
)

@Composable
private fun DashboardCard(
    title: String,
    icon: ImageVector,
    bubbleGradient: List<Color>,
    tileColor: Color,
    labelColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = tileColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(bubbleGradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
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
