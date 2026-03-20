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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.univapp.ui.util.AppScaffold

data class SubjectDetails(
    val id: String,
    val name: String,
    val description: String,
    val term: Int,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    onBack: () -> Unit,
    onOpenSubject: (Int, String) -> Unit,
    onGoGrades: () -> Unit,
    settingsVm: SettingsViewModel
) {
    val isDarkMode by settingsVm.darkMode.collectAsState()
    val teal = Color(0xFF0F6C6D)
    val darkBg = Color(0xFF101828)
    val menuBg = if (isDarkMode) Color(0xFF1D2939) else Color.White

    val allSubjects = remember {
        listOf(
            SubjectDetails("1", "Metodología de la Programación", "Fundamentos de lógica y algoritmos.", 1, Icons.Default.Code, Color(0xFF3F51B5)),
            SubjectDetails("2", "Introducción a las TI", "Panorama general de la tecnología actual.", 1, Icons.Default.Computer, Color(0xFF009688)),
            SubjectDetails("3", "Matemáticas para TI", "Cálculo y álgebra para computación.", 1, Icons.Default.Calculate, Color(0xFFFF9800)),
            SubjectDetails("4", "Inglés I", "Nivel inicial de inglés técnico.", 1, Icons.Default.Language, Color(0xFF9C27B0)),
            SubjectDetails("5", "Formación Sociocultural I", "Desarrollo humano y profesional.", 1, Icons.Default.Groups, Color(0xFFE91E63)),
            SubjectDetails("6", "Expresión Oral y Escrita I", "Habilidades de comunicación efectiva.", 1, Icons.Default.Campaign, Color(0xFF2196F3)),
            SubjectDetails("7", "Estructura de Datos", "Gestión eficiente de información en memoria.", 2, Icons.Default.Schema, Color(0xFF795548)),
            SubjectDetails("8", "Base de Datos I", "Modelado y diseño relacional SQL.", 2, Icons.Default.Storage, Color(0xFF607D8B)),
            SubjectDetails("9", "Programación Orientada a Objetos", "Paradigma de clases y objetos.", 2, Icons.Default.Extension, Color(0xFF4CAF50)),
            SubjectDetails("10", "Redes de Área Local", "Configuración de redes y protocolos.", 2, Icons.Default.Router, Color(0xFFF44336)),
            SubjectDetails("13", "Aplicaciones Web I", "Desarrollo frontend con HTML/JS.", 3, Icons.Default.Html, Color(0xFFFFC107)),
            SubjectDetails("18", "Aplicaciones Móviles I", "Desarrollo nativo para Android.", 4, Icons.Default.Smartphone, Color(0xFF3D5AFE)),
            SubjectDetails("25", "Seguridad Informática", "Criptografía y protección de datos.", 5, Icons.Default.Security, Color(0xFFD84315)),
            SubjectDetails("28", "Inteligencia Artificial", "Machine Learning y redes neuronales.", 6, Icons.Default.AutoGraph, Color(0xFFAD1457)),
            SubjectDetails("33", "Cloud Computing", "Servicios en la nube AWS y Azure.", 7, Icons.Default.Cloud, Color(0xFF039BE5)),
            SubjectDetails("38", "Ciberseguridad Avanzada", "Hacking ético e incidentes.", 8, Icons.Default.AdminPanelSettings, Color(0xFFB71C1C)),
            SubjectDetails("43", "Integradora II", "Proyecto final de titulación.", 9, Icons.Default.RocketLaunch, Color(0xFF1B5E20))
        )
    }

    var selectedTerm by remember { mutableIntStateOf(1) }
    val filteredSubjects = allSubjects.filter { it.term == selectedTerm }

    AppScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Materias", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDarkMode) darkBg else Color.White,
                    titleContentColor = if (isDarkMode) Color.White else Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(if (isDarkMode) darkBg else Color(0xFFF8F9FB))
        ) {
            var expanded by remember { mutableStateOf(false) }
            
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = "Cuatrimestre $selectedTerm",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = teal) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = teal,
                            unfocusedBorderColor = if (isDarkMode) Color.Gray.copy(alpha = 0.3f) else Color.LightGray
                        )
                    )
                    
                    // Estilo Popover Premium
                    MaterialTheme(
                        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(24.dp))
                    ) {
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(menuBg)
                        ) {
                            (1..9).forEach { term ->
                                val isSelected = selectedTerm == term
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Today, 
                                            null, 
                                            tint = if(isSelected) teal else Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    text = { 
                                        Text(
                                            "Cuatrimestre $term",
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) teal else if (isDarkMode) Color.White else Color.Black
                                        ) 
                                    },
                                    trailingIcon = {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, null, tint = teal, modifier = Modifier.size(18.dp))
                                        }
                                    },
                                    onClick = { 
                                        selectedTerm = term
                                        expanded = false
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) teal.copy(alpha = 0.1f) else Color.Transparent),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredSubjects) { subject ->
                    CourseCard(subject = subject, onClick = { onOpenSubject(subject.term, subject.id) })
                }
            }
        }
    }
}

@Composable
fun CourseCard(subject: SubjectDetails, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 260.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D2939)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(subject.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(subject.icon, null, tint = subject.color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text(text = subject.name, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(12.dp))
            Text(text = subject.description, color = Color(0xFF98A2B3), fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(Modifier.weight(1f))
        }
    }
}
