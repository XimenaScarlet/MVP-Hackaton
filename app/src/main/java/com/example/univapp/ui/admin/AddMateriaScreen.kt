package com.example.univapp.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun AddMateriaScreen(
    carreraId: String,
    grupoId: String,
    onBack: () -> Unit,
    vm: AddMateriaViewModel = viewModel(factory = AddMateriaViewModelFactory(carreraId, grupoId))
) {
    val uiState by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var nombre by remember { mutableStateOf("") }
    var selectedProfesor by remember { mutableStateOf<Profesor?>(null) }
    var periodo by remember { mutableStateOf("3º Cuatrimestre") }
    var turno by remember { mutableStateOf("TSU") }
    var creditos by remember { mutableStateOf("0") }
    var clave by remember { mutableStateOf("A-001") }
    var aula by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

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
                title = { Text("Añadir Materia", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    vm.saveMateria(
                        nombre = nombre,
                        profesor = selectedProfesor,
                        periodo = periodo,
                        turno = turno,
                        creditos = creditos.toIntOrNull() ?: 0,
                        clave = clave,
                        aula = aula,
                        descripcion = descripcion
                    )
                },
                enabled = nombre.length >= 3 && !uiState.isSaving,
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Save, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Materia", color = Color.White)
                }
            }
        }
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(it)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Nombre de la materia
                FormLabel("NOMBRE DE LA MATERIA")
                StyledTextField(value = nombre, onValueChange = { nombre = it }, placeholder = "ej. Cálculo Diferencial")
                Row(verticalAlignment = Alignment.CenterVertically, modifier=Modifier.padding(top=4.dp)){
                    Icon(Icons.Default.Info, null, modifier=Modifier.size(14.dp), tint=Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text("Mínimo 3 caracteres", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Spacer(Modifier.height(16.dp))

                // Profesor
                FormLabel("PROFESOR ASIGNADO")
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !it }) {
                    StyledTextField(
                        value = selectedProfesor?.nombre ?: "Seleccionar profesor...",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        leadingIcon = { Icon(Icons.Default.Person, null, tint=Color.Gray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        uiState.profesores.forEach { profesor ->
                            DropdownMenuItem(
                                text = { Text(profesor.nombre) },
                                onClick = {
                                    selectedProfesor = profesor
                                    expanded = false
                                })
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Info Grupo y Carrera
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoCard(label = "GRUPO", value = grupoId, icon = Icons.Default.Groups, modifier = Modifier.weight(1f))
                    InfoCard(label = "CARRERA", value = carreraId, icon = Icons.Default.School, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(24.dp))

                // Detalles Académicos
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width(4.dp).height(20.dp).background(Color(0xFF007AFF))) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Detalles Académicos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))

                // Periodo
                FormLabel("PERIODO ACADÉMICO")
                var periodoExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = periodoExpanded, onExpandedChange = { periodoExpanded = !it }) {
                    StyledTextField(
                        value = periodo,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodoExpanded) }
                    )
                    ExposedDropdownMenu(expanded = periodoExpanded, onDismissRequest = { periodoExpanded = false }) {
                        (1..10).forEach { i ->
                            DropdownMenuItem(
                                text = { Text("$i° Cuatrimestre") },
                                onClick = {
                                    periodo = "$i° Cuatrimestre"
                                    periodoExpanded = false
                                })
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Turno
                FormLabel("TURNO")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TurnoButton("TSU", turno == "TSU", { turno = "TSU" }, modifier = Modifier.weight(1f), icon = Icons.Default.WbSunny)
                    TurnoButton("Ingeniería", turno == "Ingeniería", { turno = "Ingeniería" }, modifier = Modifier.weight(1f), icon = Icons.Default.Groups)
                }
                Spacer(Modifier.height(16.dp))

                // Créditos y Clave
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("CRÉDITOS")
                        StyledTextField(value = creditos, onValueChange = { creditos = it })
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("CLAVE MATERIA")
                        StyledTextField(value = clave, onValueChange = { clave = it })
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Aula
                FormLabel("AULA O LABORATORIO")
                StyledTextField(value = aula, onValueChange = { aula = it }, placeholder = "ej. Laboratorio 304")
                Spacer(Modifier.height(16.dp))

                // Descripción
                FormLabel("DESCRIPCIÓN CORTA (Opcional)")
                StyledTextField(value = descripcion, onValueChange = { descripcion = it }, placeholder = "Objetivo general de la materia...", minLines = 3)
                Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom bar
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(bottom=8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Color.Gray) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        minLines = minLines,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF5F6F8),
            focusedIndicatorColor = Color(0xFF007AFF),
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun InfoCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6F8))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color(0xFF007AFF))
            Spacer(Modifier.width(8.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(value, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TurnoButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, icon: ImageVector) {
    val (containerColor, contentColor) = if (selected) {
        Color(0xFF007AFF) to Color.White
    } else {
        Color.White to Color.Black
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        border = if (!selected) BorderStroke(1.dp, Color.LightGray) else null,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = if(selected) 4.dp else 0.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text(text)
    }
}
