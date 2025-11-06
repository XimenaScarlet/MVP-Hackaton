package com.example.univapp.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfesoresScreen(
    onBack: () -> Unit,
    vm: AdminViewModel = viewModel()
) {
    // StateFlows del VM
    val profesores by vm.profesores.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    // Diálogos
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Profesor?>(null) }

    // Primer fetch
    LaunchedEffect(Unit) { vm.refreshAll() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profesores") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        }
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {

            // Lista / vacío
            when {
                loading && profesores.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Cargando…")
                    }
                }

                profesores.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Aún no hay profesores",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Toca el botón + para registrar el primero.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        items(profesores, key = { it.id }) { p ->
                            ProfesorCard(
                                p = p,
                                onEdit = { editing = p; showDialog = true },
                                onDelete = { vm.deleteProfesor(p.id) }
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }

            // Loading fino mientras hay lista
            if (loading && profesores.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }

            // Error simple
            error?.takeIf { it.isNotBlank() }?.let { msg ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                ) { Text(msg, modifier = Modifier.padding(12.dp)) }
            }
        }
    }

    if (showDialog) {
        ProfesorDialog(
            initial = editing,
            onDismiss = { showDialog = false },
            onConfirm = { prof ->
                vm.saveProfesor(prof)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ProfesorCard(
    p: Profesor,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(p.nombre.ifBlank { "Profesor sin nombre" }, style = MaterialTheme.typography.titleMedium)
            if (p.correo.isNotBlank()) Text(p.correo, style = MaterialTheme.typography.bodyMedium)
            if (p.telefono.isNotBlank()) Text("Tel: ${p.telefono}")
            if (p.departamento.isNotBlank()) Text("Depto: ${p.departamento}")
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete) { Text("Eliminar") }
            }
        }
    }
}

@Composable
private fun ProfesorDialog(
    initial: Profesor?,
    onDismiss: () -> Unit,
    onConfirm: (Profesor) -> Unit
) {
    var nombre by remember { mutableStateOf(initial?.nombre ?: "") }
    var correo by remember { mutableStateOf(initial?.correo ?: "") }
    var telefono by remember { mutableStateOf(initial?.telefono ?: "") }
    var depto by remember { mutableStateOf(initial?.departamento ?: "") }

    val valido = nombre.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(enabled = valido, onClick = {
                onConfirm(
                    Profesor(
                        id = initial?.id ?: "", // vacío → add(); con id → update()
                        nombre = nombre.trim(),
                        correo = correo.trim(),
                        telefono = telefono.trim(),
                        departamento = depto.trim()
                    )
                )
            }) { Text(if (initial == null) "Crear" else "Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text(if (initial == null) "Nuevo profesor" else "Editar profesor") },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre*") })
                OutlinedTextField(
                    value = correo, onValueChange = { correo = it },
                    label = { Text("Correo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                OutlinedTextField(
                    value = telefono, onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                OutlinedTextField(value = depto, onValueChange = { depto = it }, label = { Text("Departamento") })
            }
        }
    )
}
