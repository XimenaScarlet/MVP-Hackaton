package com.example.univapp.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminGruposScreen(
    vm: AdminViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val grupos by vm.grupos.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    var query by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<Grupo?>(null) }

    LaunchedEffect(Unit) { vm.refreshAll() }

    val list = remember(grupos, query) {
        if (query.isBlank()) grupos
        else grupos.filter { it.nombre.contains(query, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Grupos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { /* futuro: filtros avanzados */ }) {
                        Icon(Icons.Outlined.FilterList, contentDescription = "Filtrar")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Agregar Grupo") },
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                onClick = { showAdd = true }
            )
        }
    ) { pv ->
        Column(
            Modifier
                .padding(pv)
                .fillMaxSize()
        ) {
            // Buscador
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = { Icon(Icons.Rounded.Group, contentDescription = null) },
                placeholder = { Text("Buscar grupos por nombre") },
                singleLine = true
            )

            if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())

            if (!loading && list.isEmpty()) {
                Text("No hay grupos para mostrar.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(list, key = { it.id }) { g ->
                        GrupoItem(
                            grupo = g,
                            onEdit = { editTarget = g }
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }

            error?.takeIf { it.isNotBlank() }?.let { msg ->
                Text(
                    "Error: $msg",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    if (showAdd) {
        GrupoDialog(
            title = "Agregar grupo",
            initial = Grupo(id = "", nombre = "", tutor = "", alumnos = emptyList()),
            onDismiss = { showAdd = false },
            onConfirm = { nuevo ->
                vm.saveGrupo(nuevo)
                showAdd = false
            }
        )
    }

    editTarget?.let { target ->
        GrupoDialog(
            title = "Editar grupo",
            initial = target,
            onDismiss = { editTarget = null },
            onConfirm = { edit ->
                vm.saveGrupo(edit.copy(id = target.id))
                editTarget = null
            }
        )
    }
}

@Composable
private fun GrupoItem(
    grupo: Grupo,
    onEdit: () -> Unit
) {
    Surface(
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            leadingContent = {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Group, contentDescription = null)
                    }
                }
            },
            headlineContent = { Text(grupo.nombre.ifBlank { "Grupo sin nombre" }) },
            supportingContent = {
                val count = (grupo.alumnos ?: emptyList()).size
                val label = if (count == 1) "1 Alumno" else "$count Alumnos"
                Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            trailingContent = {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Editar")
                }
            },
            modifier = Modifier.clickable { onEdit() }
        )
    }
}

@Composable
private fun GrupoDialog(
    title: String,
    initial: Grupo,
    onDismiss: () -> Unit,
    onConfirm: (Grupo) -> Unit
) {
    var nombre by remember { mutableStateOf(initial.nombre) }
    var tutor by remember { mutableStateOf(initial.tutor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        confirmButton = {
            TextButton(
                enabled = nombre.isNotBlank(),
                onClick = { onConfirm(initial.copy(nombre = nombre.trim(), tutor = tutor.trim())) }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del grupo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tutor,
                    onValueChange = { tutor = it },
                    label = { Text("Tutor (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
