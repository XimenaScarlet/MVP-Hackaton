package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.Profesor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupDetailsScreen(
    onBack: () -> Unit,
    onNext: (String, String, String) -> Unit,
    vm: AdminGruposViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var groupName by remember { mutableStateOf("") }
    var programType by remember { mutableStateOf("TSU") }
    var selectedTutor by remember { mutableStateOf<Profesor?>(null) }
    var selectedCuatri by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    var tutorExpanded by remember { mutableStateOf(false) }
    var cuatriExpanded by remember { mutableStateOf(false) }

    // Reset selected cuatrimestre if program type changes and current selection is invalid
    LaunchedEffect(programType) {
        val currentNum = selectedCuatri.takeWhile { it.isDigit() }.toIntOrNull()
        if (currentNum != null) {
            if (programType == "TSU" && currentNum > 6) {
                selectedCuatri = ""
            } else if (programType == "ING" && currentNum < 7) {
                selectedCuatri = ""
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agregar Grupo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = { 
                    onNext(groupName, programType, selectedTutor?.id ?: "") 
                },
                enabled = groupName.isNotBlank() && selectedTutor != null && selectedCuatri.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E49B3))
            ) {
                Text("Siguiente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Nombre del Grupo
            CustomAddGroupField(
                value = groupName,
                onValueChange = { groupName = it },
                placeholder = "Nombre del Grupo"
            )

            Spacer(Modifier.height(24.dp))

            // Tipo de Programa
            Text("TIPO DE PROGRAMA", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFFF7F7F9), RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                listOf("TSU", "ING").forEach { type ->
                    val isSelected = programType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { programType = type },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            type, 
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF5E49B3) else Color.Gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Tutor
            Text("ASIGNAR TUTOR", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            DropdownFieldGroup(
                label = selectedTutor?.nombre ?: "Seleccionar un tutor docente",
                icon = Icons.Default.Person,
                isExpanded = tutorExpanded,
                onExpandChange = { tutorExpanded = it }
            ) {
                uiState.profesores.forEach { tutor ->
                    DropdownMenuItem(
                        text = { Text(tutor.nombre) },
                        onClick = { selectedTutor = tutor; tutorExpanded = false }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Cuatrimestre
            Text("CUATRIMESTRE", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            DropdownFieldGroup(
                label = if (selectedCuatri.isEmpty()) "Seleccionar cuatrimestre" else selectedCuatri,
                icon = Icons.Default.CalendarToday,
                isExpanded = cuatriExpanded,
                onExpandChange = { cuatriExpanded = it }
            ) {
                val cuatriRange = if (programType == "TSU") (1..6) else (7..11)
                cuatriRange.forEach { cuatri ->
                    DropdownMenuItem(
                        text = { Text("$cuatri° Cuatrimestre") },
                        onClick = { selectedCuatri = "$cuatri° Cuatrimestre"; cuatriExpanded = false }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Capacidad y Estado
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("CAPACIDAD MÁX.", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = capacidad,
                        onValueChange = { newValue -> 
                            if (newValue.length <= 2) {
                                capacidad = newValue.filter { c -> c.isDigit() }
                            }
                        },
                        placeholder = { Text("Ej. 35", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Groups, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF7F7F9),
                            focusedContainerColor = Color(0xFFF7F7F9),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color(0xFF5E49B3)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("ESTADO DEL GRUPO", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color(0xFFF7F7F9), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Activo", color = Color.Gray)
                        Switch(
                            checked = isActive, 
                            onCheckedChange = { isActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF5E49B3)
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(120.dp))
        }
    }
}

@Composable
private fun CustomAddGroupField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF7F7F9),
            focusedContainerColor = Color(0xFFF7F7F9),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color(0xFF5E49B3)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownFieldGroup(
    label: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = onExpandChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            leadingIcon = { Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF7F7F9),
                focusedContainerColor = Color(0xFFF7F7F9),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF5E49B3)
            )
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier.background(Color.White),
            content = content
        )
    }
}
