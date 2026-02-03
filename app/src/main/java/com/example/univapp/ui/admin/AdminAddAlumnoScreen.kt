package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddAlumnoScreen(
    onBack: () -> Unit,
    vm: AdminAddAlumnoViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var matricula by remember { mutableStateOf("") }
    var semestre by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf<String?>(null) }
    var edad by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var selectedCarrera by remember { mutableStateOf<Carrera?>(null) }
    var selectedGrupo by remember { mutableStateOf<Grupo?>(null) }
    var estatus by remember { mutableStateOf("Activo") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    var isCarreraExpanded by remember { mutableStateOf(false) }
    var isGrupoExpanded by remember { mutableStateOf(false) }
    var isGeneroExpanded by remember { mutableStateOf(false) }
    var isEstatusExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.onSaveSuccess) {
        if (uiState.onSaveSuccess) {
            onBack()
        }
    }

    // Auto-generate email from matricula
    LaunchedEffect(matricula) {
        email = if (matricula.isNotBlank()) "$matricula@uts.edu.mx" else ""
    }

    // Auto-generate semestre from grupo
    LaunchedEffect(selectedGrupo) {
        semestre = selectedGrupo?.nombre?.firstOrNull { it.isDigit() }?.toString() ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Alumno") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF7F7FB))
            )
        },
        bottomBar = {
            val canSave = matricula.isNotBlank() && nombre.isNotBlank() && selectedCarrera != null && selectedGrupo != null && genero != null
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        vm.saveAlumno(
                            matricula = matricula,
                            nombre = nombre,
                            carreraId = selectedCarrera!!.id, // Carrera is selected from dropdown
                            grupoId = selectedGrupo!!.id,     // Grupo is selected from dropdown
                            genero = genero!!,
                            estatus = estatus
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .height(50.dp),
                    enabled = canSave,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Guardar Alumno", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color(0xFFF7F7FB)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { ProfilePicture() }

            // --- Basic Info ---
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(value = matricula, onValueChange = { matricula = it }, label = { Text("Matrícula") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = semestre, onValueChange = {}, readOnly = true, label = { Text("Semestre") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface))
                }
            }
            item { OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) }
            item { OutlinedTextField(value = email, onValueChange = {}, readOnly = true, label = { Text("Correo Electrónico") }, leadingIcon = { Icon(Icons.Default.Email, null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)) }
            
            // --- Personal Data Section ---
            item { 
                Section(title = "DATOS PERSONALES") {
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)){
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)){
                             Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                DropdownSelector(label = "Género", options = listOf("Masculino", "Femenino"), selectedOption = genero, onOptionSelected = { genero = it }, modifier = Modifier.weight(1f), expanded = isGeneroExpanded, onExpandedChange = { isGeneroExpanded = it })
                                OutlinedTextField(value = edad, onValueChange = { edad = it }, label = { Text("Edad") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                            }
                            OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha de Nacimiento") }, placeholder = { Text("mm/dd/yyyy") }, trailingIcon = { Icon(Icons.Default.DateRange, null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        }
                    }
                }
            }

            // --- Academic Section ---
            item {
                Section(title = "ACADÉMICO") {
                     DropdownSelector(label = "Carrera", options = uiState.carreras.mapNotNull { it.nombre }, selectedOption = selectedCarrera?.nombre, onOptionSelected = {
                        val carrera = uiState.carreras.find { c -> c.nombre == it }
                        if (carrera != null) {
                            selectedCarrera = carrera
                            selectedGrupo = null // Reset group selection
                            vm.onCarreraSelected(carrera.id)
                        }
                    }, expanded = isCarreraExpanded, onExpandedChange = { isCarreraExpanded = it })
                    
                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)){
                        DropdownSelector(label = "Grupo", options = uiState.grupos.mapNotNull { it.nombre }, selectedOption = selectedGrupo?.nombre, onOptionSelected = {
                            selectedGrupo = uiState.grupos.find { g -> g.nombre == it }
                        }, modifier = Modifier.weight(1f), enabled = selectedCarrera != null, expanded = isGrupoExpanded, onExpandedChange = { isGrupoExpanded = it })
                        
                        DropdownSelector(label = "Estatus Académico", options = listOf("Activo", "Inactivo", "Graduado"), selectedOption = estatus, onOptionSelected = { estatus = it }, modifier = Modifier.weight(1f), expanded = isEstatusExpanded, onExpandedChange = { isEstatusExpanded = it })
                    }
                }
            }

            // --- Optional Contact Section ---
            item {
                Section(title = "CONTACTO (OPCIONAL)") {
                    OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }
            }

            // --- Loading/Error State ---
            if (uiState.isLoading && !uiState.onSaveSuccess) {
                item { Box(Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            }
            uiState.error?.let { item { Text(it, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp)) } }
        }
    }
}

@Composable
private fun ProfilePicture() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()){
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray.copy(alpha=0.8f), modifier = Modifier.size(50.dp))
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, Color.White, CircleShape)
                    .align(Alignment.BottomEnd)
                    .clickable { /* TODO: Implement image picker */ }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar foto", tint = Color.White, modifier = Modifier.align(Alignment.Center))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Foto de Perfil", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { if(enabled) onExpandedChange(it) }, modifier = modifier) {
        OutlinedTextField(
            value = selectedOption ?: "Seleccionar",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}
