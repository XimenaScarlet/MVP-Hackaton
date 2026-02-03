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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun AcademicHistoryScreen(
    onBack: () -> Unit = {},
    onSelectTerm: (Int) -> Unit = {}
) {
    val cycles = (9 downTo 1).toList()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FB)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
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
                            tint = Color(0xFF004696)
                        )
                    }
                    Text(
                        text = "Kardex",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main Average Card with Gradient
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .shadow(12.dp, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1A1C1E), Color(0xFF0D2B33))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "PROMEDIO GENERAL",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.6f),
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "8.9",
                                        fontSize = 56.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                                
                                // Chart Icon Button
                                Surface(
                                    modifier = Modifier.size(56.dp),
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.BarChart, null, tint = Color(0xFF2DD4BF), modifier = Modifier.size(32.dp))
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            Column {
                                Text(
                                    text = "ESTATUS ACADÉMICO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.6f),
                                    letterSpacing = 0.5.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF2DD4BF), CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ALUMNO REGULAR",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "SELECCIONAR CUATRIMESTRE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9CA3AF),
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Grid of Cycles
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cycles) { cycle ->
                        CycleCard(cycle) { onSelectTerm(cycle) }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Footer
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color(0xFFE5E7EB),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Historial Académico Oficial",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = "SINCRONIZACIÓN EN TIEMPO REAL • SE",
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CycleCard(num: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$num",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
            Text(
                text = "CICLO",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}
