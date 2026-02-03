@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GradesScreen(
    onBack: () -> Unit = {},
    vm: SubjectsViewModel = viewModel()
) {
    val currentMaxSemester by vm.currentSemester.collectAsState()
    val grades by vm.grades.collectAsState()
    val isLoading by vm.loading.collectAsState()
    
    var selectedTerm by remember { mutableIntStateOf(1) }
    var showTermPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadUserSemesterAndSubjects()
    }
    
    LaunchedEffect(currentMaxSemester) {
        selectedTerm = currentMaxSemester
    }

    val termName = when (selectedTerm) {
        1 -> "Primer Cuatrimestre"
        2 -> "Segundo Cuatrimestre"
        3 -> "Tercer Cuatrimestre"
        4 -> "Cuarto Cuatrimestre"
        5 -> "Quinto Cuatrimestre"
        6 -> "Sexto Cuatrimestre"
        7 -> "Séptimo Cuatrimestre"
        8 -> "Octavo Cuatrimestre"
        9 -> "Noveno Cuatrimestre"
        10 -> "Décimo Cuatrimestre"
        else -> "$selectedTerm° Cuatrimestre"
    }

    val average = if (grades.isNotEmpty()) {
        grades.map { it.score }.average()
    } else 0.0

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFBF9F7)
    ) {
        if (isLoading && grades.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFA67C52))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .background(Color.White, CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Atrás",
                            modifier = Modifier.size(28.dp),
                            tint = Color(0xFF4A3F35)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "REPORTE FINAL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB5A492),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Boleta de Calificaciones",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A3F35)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Term Selector
                Box {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clickable { showTermPicker = true },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Spacer(Modifier.width(24.dp))
                            Text(
                                text = termName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A3F35)
                            )
                            Icon(
                                imageVector = if (showTermPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFFB5A492)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showTermPicker,
                        onDismissRequest = { showTermPicker = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(Color.White, RoundedCornerShape(16.dp))
                    ) {
                        repeat(maxOf(currentMaxSemester, 1)) { index ->
                            val termNum = index + 1
                            val name = when (termNum) {
                                1 -> "Primer Cuatrimestre"
                                2 -> "Segundo Cuatrimestre"
                                3 -> "Tercer Cuatrimestre"
                                4 -> "Cuarto Cuatrimestre"
                                5 -> "Quinto Cuatrimestre"
                                6 -> "Sexto Cuatrimestre"
                                7 -> "Séptimo Cuatrimestre"
                                8 -> "Octavo Cuatrimestre"
                                9 -> "Noveno Cuatrimestre"
                                10 -> "Décimo Cuatrimestre"
                                else -> "$termNum° Cuatrimestre"
                            }
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        name, 
                                        fontWeight = if (selectedTerm == termNum) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTerm == termNum) Color(0xFF4A3F35) else Color(0xFF74777F)
                                    ) 
                                },
                                onClick = {
                                    selectedTerm = termNum
                                    showTermPicker = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Main Average Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(130.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawArc(
                                    color = Color(0xFFF3F4F6),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = Color(0xFFA67C52),
                                    startAngle = -90f,
                                    sweepAngle = 360f * (average.toFloat() / 10f),
                                    useCenter = false,
                                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = String.format("%.1f", average),
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A3F35)
                                )
                                Text(
                                    text = "PROMEDIO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB5A492)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "PROMEDIO CUATRIMESTRAL",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A3F35),
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (grades.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("No hay calificaciones registradas", color = Color.Gray)
                    }
                } else {
                    // Grades List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(grades) { item ->
                            GradeRowCard(GradeItem(item.materiaName, item.score, item.approved))
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            // Global Average Bar
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                color = Color(0xFF4A3F35),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(48.dp),
                                        color = Color.White.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(24.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text("GLOBAL", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                                        Text("Promedio General", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(String.format("%.1f", average), fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

private data class GradeItem(val name: String, val score: Double, val approved: Boolean)

@Composable
private fun GradeRowCard(item: GradeItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A3F35)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (item.approved) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (item.approved) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (item.approved) "APROBADO" else "REPROBADO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.approved) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }
            
            Surface(
                color = Color(0xFFFBF9F7),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = String.format("%.1f", item.score),
                    color = Color(0xFFA67C52),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
