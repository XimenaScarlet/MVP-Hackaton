package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfesorScreen(
    carreraId: String,
    onBack: () -> Unit,
    vm: AddProfesorViewModel = viewModel(factory = AddProfesorViewModelFactory(carreraId))
) {
    val uiState by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var numeroEmpleado by remember { mutableStateOf("") }
    var fechaAlta by remember { mutableStateOf("16/10/2023") }
    var activo by remember { mutableStateOf(true) }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            onBack()
            vm.onDoneNavigating()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agregar Profesor", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    vm.saveProfesor(
                        nombres = nombres,
                        apellidos = apellidos,
                        telefono = telefono,
                        correo = correo,
                        numeroEmpleado = numeroEmpleado,
                        turno = "", // Turno ya no es parte del UI
                        activo = activo
                    )
                },
                enabled = nombres.isNotBlank() && apellidos.isNotBlank() && correo.isNotBlank() && !uiState.isSaving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar Profesor", color = Color.Gray)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(24.dp))
            // Información personal
            SectionTitle(icon = Icons.Default.Person, title = "INFORMACIÓN PERSONAL")
            AppStyledTextField(
                value = nombres,
                onValueChange = { nombres = it },
                label = "Nombre(s)",
                placeholder = "Ej. Juan Pablo"
            )
            Spacer(Modifier.height(16.dp))
            AppStyledTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = "Apellidos",
                placeholder = "Ej. Pérez García"
            )
            Spacer(Modifier.height(16.dp))
            AppStyledTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = "Teléfono",
                placeholder = "Ej. 55 1234 5678",
                isOptional = true
            )
            Spacer(Modifier.height(24.dp))

            // Datos institucionales
            SectionTitle(icon = Icons.Default.Business, title = "DATOS INSTITUCIONALES")
            AppStyledTextField(
                value = correo,
                onValueChange = { correo = it },
                label = "Correo Institucional",
                placeholder = "usuario@universidad.edu.mx",
                leadingIcon = Icons.Default.Email
            )
            Text(
                "Se validará disponibilidad",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
            Spacer(Modifier.height(16.dp))
            AppStyledTextField(
                value = numeroEmpleado,
                onValueChange = { numeroEmpleado = it },
                label = "Número de Empleado / ID",
                placeholder = "Ej. EMP-2023-001"
            )
            Spacer(Modifier.height(16.dp))
            AppStyledTextField(
                value = fechaAlta,
                onValueChange = { fechaAlta = it },
                label = "Fecha de Alta",
                placeholder = "",
                leadingIcon = Icons.Default.CalendarToday,
                readOnly = true // El ejemplo muestra una fecha estática
            )
            Spacer(Modifier.height(24.dp))

            // Estado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (activo) Color(0xFF4CAF50) else Color.LightGray, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Activo")
                }
                Switch(
                    checked = activo, 
                    onCheckedChange = { activo = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF6200EE)
                    )
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF6200EE))
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
    }
    Spacer(Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppStyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isOptional: Boolean = false,
    leadingIcon: ImageVector? = null,
    readOnly: Boolean = false
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            if (isOptional) Text("Opcional", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = leadingIcon?.let { icon ->
                { Icon(icon, contentDescription = null, tint = Color.Gray) }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6200EE),
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFFF0F0F0),
                unfocusedContainerColor = Color(0xFFF0F0F0)
            ),
            readOnly = readOnly
        )
    }
}
