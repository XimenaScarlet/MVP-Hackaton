@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.Materia

@Composable
fun SubjectsScreen(
    onBack: () -> Unit,
    onOpenSubject: (term: Int, subjectId: String) -> Unit,
    onGoGrades: () -> Unit = {},
    vm: SubjectsViewModel = viewModel(),
    settingsVm: SettingsViewModel = viewModel()
) {
    val currentMaxSemester by vm.currentSemester.collectAsState()
    val subjects by vm.subjects.collectAsState()
    val isLoading by vm.loading.collectAsState()
    val dark by settingsVm.darkMode.collectAsState()
    
    var selectedTerm by remember { mutableIntStateOf(0) }
    var showTermPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadUserSemesterAndSubjects()
    }
    
    LaunchedEffect(currentMaxSemester) {
        if (currentMaxSemester > 0) {
            selectedTerm = currentMaxSemester
        }
    }

    // Dynamic Colors
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFFBF9F7)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF4A3F35)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFFB5A492)
    val iconBtnBg = if (dark) Color(0xFF334155) else Color.White

    val displayTerm = if (selectedTerm > 0) selectedTerm else currentMaxSemester

    val termName = when (displayTerm) {
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
        else -> "$displayTerm° Cuatrimestre"
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        if (isLoading && subjects.isEmpty()) {
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
                            .background(iconBtnBg, CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Atrás",
                            modifier = Modifier.size(28.dp),
                            tint = titleColor
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "RESUMEN ACADÉMICO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = subtitleColor,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Mi Cuatrimestre",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
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
                        color = cardBg,
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
                                color = titleColor
                            )
                            Icon(
                                imageVector = if (showTermPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = subtitleColor
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showTermPicker,
                        onDismissRequest = { showTermPicker = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(cardBg, RoundedCornerShape(16.dp))
                    ) {
                        for (termNum in 1..maxOf(currentMaxSemester, 1)) {
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
                                        fontWeight = if (displayTerm == termNum) FontWeight.Bold else FontWeight.Medium,
                                        color = if (displayTerm == termNum) titleColor else subtitleColor
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

                Spacer(modifier = Modifier.height(16.dp))

                // Boleta de Calificaciones Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clickable { onGoGrades() },
                    shape = RoundedCornerShape(20.dp),
                    color = cardBg,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFA67C52),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "BOLETA DE CALIFICACIONES",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (subjects.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("No tienes materias asignadas", color = Color.Gray)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(subjects) { subject ->
                            val uiData = SubjectDisplayData(
                                id = subject.id,
                                name = subject.nombre,
                                department = "MATERIA",
                                progressLabel = "100%",
                                progress = 1.0f,
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                color = getSubjectColor(subject.nombre, dark)
                            )
                            DetailedSubjectCard(uiData, cardBg, titleColor, dark) {
                                onOpenSubject(displayTerm, subject.id)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getSubjectColor(name: String, isDark: Boolean): Color {
    val colors = if (isDark) {
        listOf(Color(0xFF60A5FA), Color(0xFFFB923C), Color(0xFF34D399), Color(0xFFA5B4FC), Color(0xFFF472B6))
    } else {
        listOf(Color(0xFF3B82F6), Color(0xFFF97316), Color(0xFF10B981), Color(0xFF6366F1), Color(0xFFEC4899))
    }
    return colors[name.length % colors.size]
}

private data class SubjectDisplayData(
    val id: String,
    val name: String,
    val department: String,
    val progressLabel: String,
    val progress: Float,
    val icon: ImageVector,
    val color: Color
)

@Composable
private fun DetailedSubjectCard(data: SubjectDisplayData, cardBg: Color, titleColor: Color, isDark: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(data.color.copy(alpha = if (isDark) 0.2f else 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = null,
                        tint = data.color,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = if (isDark) Color(0xFF334155) else Color(0xFFF3F4F6),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = data.color,
                            startAngle = -90f,
                            sweepAngle = 360f * data.progress,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = data.progressLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = data.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = data.department,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF9CA3AF)
            )
        }
    }
}
