package com.example.univapp.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAlumnosScreen(
    vm: AdminViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val alumnos by vm.alumnos.collectAsState()
    val carreras by vm.carreras.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    var showAdd by remember { mutableStateOf(false) }
    var selected: Alumno? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) { vm.refreshAll() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alumnos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Agregar alumno")
            }
        }
    ) { pv ->
        Column(Modifier.padding(pv)) {

            if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())

            if (!loading && alumnos.isEmpty()) {
                Text("No hay alumnos para mostrar.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    items(alumnos, key = { it.id }) { a ->
                        ListItem(
                            headlineContent = { Text(a.nombre.ifBlank { "Sin nombre" }) },
                            supportingContent = {
                                Text("${a.matricula} • ${a.carrera.ifBlank { "Carrera no asignada" }}")
                            },
                            trailingContent = {
                                TextButton(onClick = { vm.deleteAlumno(a.id) }) {
                                    Text("Eliminar")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable { selected = a }
                        )
                        Divider()
                    }
                }
            }

            error?.let { msg ->
                if (msg.isNotBlank()) {
                    Text(
                        "Error: $msg",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    selected?.let { alumno ->
        AlumnoDetailDialog(alumno = alumno, onDismiss = { selected = null })
    }

    if (showAdd) {
        AddAlumnoDialog(
            carrerasOpciones = carreras.map { it.nombre },
            onDismiss = { showAdd = false },
            onSave = { data ->
                vm.addAlumnoCompleto(
                    nombre = data.nombre,
                    matricula = data.matricula,
                    carrera = data.carrera,
                    direccion = data.direccion,
                    edad = 0, // se deduce si quieres luego
                    fechaNacimiento = data.fechaNacimiento,
                    telefono = data.telefono
                )
                showAdd = false
            }
        )
    }
}

/* ---------- Detalle del alumno ---------- */

@Composable
private fun AlumnoDetailDialog(
    alumno: Alumno,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
        title = { Text(alumno.nombre.ifBlank { "Alumno" }) },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Matrícula: ${alumno.matricula}")
                Text("Correo: ${alumno.correo}")
                Text("Carrera: ${alumno.carrera}")
            }
        }
    )
}

/* ---------- Alta con validaciones y formato manual de fecha ---------- */

data class AlumnoFormData(
    val nombre: String,
    val matricula: String,
    val carrera: String,
    val direccion: String,
    val fechaNacimiento: String,
    val telefono: String
)

@Composable
private fun AddAlumnoDialog(
    carrerasOpciones: List<String>,
    onDismiss: () -> Unit,
    onSave: (AlumnoFormData) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var fechaNac by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    val valido = nombre.isNotBlank() &&
            matricula.isNotBlank() &&
            carrera.isNotBlank() &&
            fechaNac.isNotBlank() &&
            telefono.length in 8..12

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar alumno") },
        confirmButton = {
            TextButton(
                enabled = valido,
                onClick = {
                    onSave(
                        AlumnoFormData(
                            nombre,
                            matricula,
                            carrera,
                            direccion,
                            fechaNac,
                            telefono
                        )
                    )
                }
            ) { Text("Guardar alumno") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") }
                )

                OutlinedTextField(
                    value = matricula,
                    onValueChange = { new ->
                        if (new.all { it.isLetterOrDigit() }) matricula = new.uppercase()
                    },
                    label = { Text("Matrícula") }
                )

                // Dropdown de carrera
                Box {
                    OutlinedTextField(
                        value = carrera,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Carrera") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.Outlined.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        carrerasOpciones.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    carrera = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") }
                )

                // Campo de texto manual para fecha
                OutlinedTextField(
                    value = fechaNac,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() || it == '/' } && input.length <= 10)
                            fechaNac = input
                    },
                    label = { Text("Fecha de nacimiento (dd/MM/yyyy)") },
                    placeholder = { Text("14/10/2004") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = {
                        if (it.all(Char::isDigit) && it.length <= 12) telefono = it
                    },
                    label = { Text("Teléfono (máx. 12 dígitos)") },
                    singleLine = true
                )

                Text(
                    "El correo y contraseña se generarán automáticamente con la matrícula.",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    )
}
