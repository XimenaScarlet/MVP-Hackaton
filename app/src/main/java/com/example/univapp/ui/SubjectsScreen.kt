package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.univapp.data.Materia
import com.example.univapp.ui.util.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    onBack: () -> Unit,
    onOpenSubject: (Int, String) -> Unit,
    onGoGrades: () -> Unit,
    settingsVm: SettingsViewModel,
    subjectsVm: SubjectsViewModel = hiltViewModel()
) {
    val isDarkMode by settingsVm.darkMode.collectAsState()
    val subjects by subjectsVm.subjects.collectAsStateWithLifecycle()
    val isLoading by subjectsVm.loading.collectAsStateWithLifecycle()
    val currentSemester by subjectsVm.currentSemester.collectAsStateWithLifecycle()

    val darkBg = Color(0xFF0B101F)
    val cardBg = Color(0xFF131A2C)
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        subjectsVm.loadUserSemesterAndSubjects()
    }

    AppScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Materias", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                actions = {
                    Surface(
                        onClick = { expanded = true },
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CalendarToday, null, tint = Color(0xFF818CF8), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            // Muestra el número del semestre seleccionado (7, 8, 9 o 10)
                            Text(text = "$currentSemester", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(cardBg)
                    ) {
                        (7..10).forEach { term ->
                            DropdownMenuItem(
                                text = { Text("Cuatrimestre $term", color = Color.White) },
                                onClick = { 
                                    subjectsVm.setSemester(term) // Actualiza el cuatrimestre y carga materias de DB
                                    expanded = false 
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = darkBg,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(darkBg)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF818CF8))
                }
            } else if (subjects.isEmpty()) {
                EmptySubjectsView()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(subjects) { materia ->
                        val visual = mapMateriaToVisual(materia)
                        CourseCard(
                            name = materia.nombre,
                            aula = materia.aula,
                            icon = visual.first,
                            color = visual.second,
                            onClick = { onOpenSubject(currentSemester, materia.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptySubjectsView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(0xFFFFFFFF).copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFFB8BFFF)
                )
            }
            
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .offset(x = 10.dp, y = (-10).dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF4FC3F7)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFF0B101F)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "No tienes\nmaterias\nasignadas.",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Explora nuestros cursos disponibles para comenzar tu aprendizaje.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun CourseCard(name: String, aula: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2C)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            }
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                text = name, 
                color = Color.White, 
                fontWeight = FontWeight.Bold, 
                fontSize = 16.sp, 
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            
            Spacer(Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationCity, null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(text = aula, color = Color(0xFF64748B), fontSize = 13.sp)
            }
        }
    }
}

private fun mapMateriaToVisual(materia: Materia): Pair<androidx.compose.ui.graphics.vector.ImageVector, Color> {
    val n = materia.nombre.lowercase()
    return when {
        n.contains("programación") || n.contains("software") -> Icons.Default.Code to Color(0xFF6366F1)
        n.contains("cálculo") || n.contains("matemáticas") || n.contains("probabilidad") -> Icons.Default.Functions to Color(0xFFA855F7)
        n.contains("estructuras") || n.contains("metodologías") -> Icons.Default.Schema to Color(0xFF3B82F6)
        n.contains("inglés") -> Icons.Default.Language to Color(0xFF0EA5E9)
        n.contains("bases de datos") -> Icons.Default.Storage to Color(0xFF8B5CF6)
        n.contains("arquitectura") -> Icons.Default.Memory to Color(0xFFEC4899)
        else -> Icons.Default.Book to Color(0xFF10B981)
    }
}
