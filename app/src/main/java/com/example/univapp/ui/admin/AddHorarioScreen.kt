package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.Materia
import com.example.univapp.data.Profesor
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHorarioScreen(
    carreraId: String,
    grupoId: String,
    onBack: () -> Unit,
    vm: AddHorarioViewModel = viewModel(
        factory = AddHorarioViewModelFactory(carreraId, grupoId)
    )
) {
    val uiState by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // State for the form fields
    var selectedMateria by remember { mutableStateOf<Materia?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredMaterias = remember(searchQuery, uiState.materias) {
        if (searchQuery.isBlank()) {
            uiState.materias.take(5) 
        } else {
            uiState.materias.filter {
                it.nombre?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }
    
    var selectedDays by remember { mutableStateOf(setOf<String>()) }
    var horaInicio by remember { mutableStateOf("07:00") }
    var horaFin by remember { mutableStateOf("09:00") }
    var salon by remember { mutableStateOf("") }
    var selectedProfesor by remember { mutableStateOf<Profesor?>(null) }
    var profesorMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            onBack()
            vm.onDoneNavigating()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agregar Clase", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                Button(
                    onClick = {
                        selectedMateria?.let { materia ->
                            vm.saveHorario(
                                materia = materia,
                                dias = selectedDays.toList(),
                                horaInicio = horaInicio,
                                horaFin = horaFin,
                                salon = salon,
                                profesor = selectedProfesor
                            )
                        }
                    },
                    enabled = selectedMateria != null && selectedDays.isNotEmpty() && salon.isNotBlank() && !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0), contentColor = Color.Gray)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(color = Color.Gray, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Guardar Clase", fontWeight = FontWeight.Bold)
                    }
                }
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar", color = Color(0xFF673AB7))
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF673AB7))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(Modifier.height(16.dp))
                
                // Materia
                Text("MATERIA", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar materia o código...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF7F7F9),
                        focusedContainerColor = Color(0xFFF7F7F9),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color(0xFF673AB7)
                    )
                )
                
                Spacer(Modifier.height(8.dp))
                
                // Chips de selección de materia
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredMaterias) { materia ->
                        val isSelected = selectedMateria?.id == materia.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMateria = if (isSelected) null else materia },
                            label = { Text(materia.nombre ?: "") },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF673AB7).copy(alpha = 0.1f),
                                selectedLabelColor = Color(0xFF673AB7)
                            )
                        )
                    }
                }

                // Días de la Semana
                Spacer(Modifier.height(24.dp))
                Text("DÍAS DE LA SEMANA", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                DaySelector(selectedDays) { day ->
                    selectedDays = if (selectedDays.contains(day)) selectedDays - day else selectedDays + day
                }

                // Horas
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TimePicker(label = "INICIO", time = horaInicio, onTimeChange = { horaInicio = it }, modifier = Modifier.weight(1f))
                    TimePicker(label = "FIN", time = horaFin, onTimeChange = { horaFin = it }, modifier = Modifier.weight(1f))
                }

                // Salón
                Spacer(Modifier.height(24.dp))
                Text("SALÓN / UBICACIÓN", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = salon,
                    onValueChange = { salon = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Lab. de Computación 3") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color.Gray) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF7F7F9),
                        focusedContainerColor = Color(0xFFF7F7F9),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color(0xFF673AB7)
                    )
                )

                // Profesor
                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("PROFESOR", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Text("OPCIONAL", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                }
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = profesorMenuExpanded,
                    onExpandedChange = { profesorMenuExpanded = !it }
                ) {
                    OutlinedTextField(
                        value = selectedProfesor?.nombre ?: "Seleccionar profesor...",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        leadingIcon = { Icon(Icons.Default.PersonOutline, null, tint = Color.Gray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = profesorMenuExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF7F7F9),
                            focusedContainerColor = Color(0xFFF7F7F9),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color(0xFF673AB7)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = profesorMenuExpanded,
                        onDismissRequest = { profesorMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ninguno") },
                            onClick = {
                                selectedProfesor = null
                                profesorMenuExpanded = false
                            }
                        )
                        uiState.profesores.forEach { profesor ->
                            DropdownMenuItem(
                                text = { Text(profesor.nombre ?: "") },
                                onClick = {
                                    selectedProfesor = profesor
                                    profesorMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(120.dp))
            }
        }
    }
}

@Composable
fun DaySelector(selectedDays: Set<String>, onDayClick: (String) -> Unit) {
    val days = listOf("L", "M", "M", "J", "V", "S")
    val dayKeys = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SAB")
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        dayKeys.forEachIndexed { index, key ->
            val isSelected = selectedDays.contains(key)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clickable { onDayClick(key) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFF673AB7) else Color(0xFFF0F0F0),
                contentColor = if (isSelected) Color.White else Color.Black
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(days[index], fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TimePicker(label: String, time: String, onTimeChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = time,
            onValueChange = onTimeChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Schedule, null, tint = Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF7F7F9),
                focusedContainerColor = Color(0xFFF7F7F9),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF673AB7)
            )
        )
    }
}
