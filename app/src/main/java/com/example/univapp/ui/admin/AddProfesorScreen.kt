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
import com.example.univapp.ui.util.AppScaffold
import com.example.univapp.ui.util.ValidatedTextField

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

    AppScaffold(
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
                        turno = "",
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
            SectionTitle(icon = Icons.Default.Person, title = "INFORMACIÓN PERSONAL")
            
            ValidatedTextField(
                value = nombres,
                onValueChange = { nombres = it },
                label = "Nombre(s)",
                maxLength = 50
            )
            Spacer(Modifier.height(16.dp))
            ValidatedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = "Apellidos",
                maxLength = 50
            )
            Spacer(Modifier.height(16.dp))
            ValidatedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = "Teléfono (Opcional)",
                maxLength = 10,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
            )
            Spacer(Modifier.height(24.dp))

            SectionTitle(icon = Icons.Default.Business, title = "DATOS INSTITUCIONALES")
            ValidatedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = "Correo Institucional",
                maxLength = 60,
                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color.Gray) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email)
            )
            Text(
                "Se validará disponibilidad",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
            Spacer(Modifier.height(16.dp))
            ValidatedTextField(
                value = numeroEmpleado,
                onValueChange = { numeroEmpleado = it },
                label = "Número de Empleado / ID",
                maxLength = 20
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = fechaAlta,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de Alta") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = Color(0xFFF0F0F0)
                )
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
