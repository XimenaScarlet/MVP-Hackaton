// ui/admin/AdminMateriasScreen.kt
package com.example.univapp.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMateriasScreen(
    vm: AdminViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val materias by vm.materias.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    // carrera seleccionada; null = vista de carreras (home)
    var carreraSeleccionada by remember { mutableStateOf<String?>(null) }
    var showAdd by remember { mutableStateOf(false) }

    // Agrupación por carrera para la vista inicial
    val carreras = remember(materias) {
        materias
            .groupBy { it.carrera.trim().ifBlank { "Sin carrera" } }
            .map { (nombre, lista) -> nombre to lista.size }
            .sortedBy { it.first.lowercase() }
    }

    // Lista filtrada cuando hay carrera seleccionada
    val listaFiltrada = remember(materias, carreraSeleccionada) {
        if (carreraSeleccionada.isNullOrBlank()) emptyList()
        else materias.filter { it.carrera.trim().ifBlank { "Sin carrera" } == carreraSeleccionada }
    }

    LaunchedEffect(Unit) { vm.refreshAll() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Materias") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (carreraSeleccionada == null) onBack() else carreraSeleccionada = null
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Agregar materia")
            }
        }
    ) { pv ->
        Column(Modifier.padding(pv)) {

            if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())

            // -------- Vista A: lista de carreras --------
            if (carreraSeleccionada == null) {
                if (!loading && carreras.isEmpty()) {
                    Text("No hay materias para mostrar.", modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        items(carreras, key = { it.first }) { (nombreCarrera, total) ->
                            CarreraRow(
                                nombre = nombreCarrera,
                                totalMaterias = total,
                                onOpen = { carreraSeleccionada = nombreCarrera }
                            )
                            Divider()
                        }
                    }
                }
            }
            // -------- Vista B: materias de una carrera --------
            else {
                if (!loading && listaFiltrada.isEmpty()) {
                    Text(
                        "No hay materias para «$carreraSeleccionada».",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        items(listaFiltrada, key = { it.id }) { m ->
                            MateriaRow(
                                materia = m,
                                onDelete = { vm.deleteMateria(m.id) }
                            )
                            Divider()
                        }
                    }
                }
            }

            error?.takeIf { it.isNotBlank() }?.let { msg ->
                Text(
                    text = "Error: $msg",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    if (showAdd) {
        AddMateriaDialog(
            onDismiss = { showAdd = false },
            onSave = {
                vm.saveMateria(it)
                showAdd = false
            }
        )
    }
}

/* ---------- Row de carrera (card simple con contador y chevron) ---------- */
@Composable
private fun CarreraRow(
    nombre: String,
    totalMaterias: Int,
    onOpen: () -> Unit
) {
    ListItem(
        leadingContent = {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(Modifier.size(56.dp))
            }
        },
        headlineContent = { Text(nombre) },
        supportingContent = {
            val label = if (totalMaterias == 1) "1 materia" else "$totalMaterias materias"
            Text(label)
        },
        trailingContent = {
            Icon(Icons.Outlined.ChevronRight, contentDescription = null)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onOpen() }
    )
}

/* ---------- Row de materia ---------- */
@Composable
private fun MateriaRow(
    materia: Materia,
    onDelete: () -> Unit
) {
    var menu by remember { mutableStateOf(false) }

    ListItem(
        leadingContent = {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.extraLarge
            ) { Box(Modifier.size(48.dp)) }
        },
        headlineContent = { Text(materia.nombre.ifBlank { "Materia sin nombre" }) },
        supportingContent = {
            val sub = buildString {
                append(materia.clave.ifBlank { "S/C" })
                if (materia.carrera.isNotBlank()) append(" | ${materia.carrera}")
            }
            Text(sub)
        },
        trailingContent = {
            // Menú simple: solo eliminar (puedes ampliarlo)
            Box {
                // Podrías poner un IconButton con more-vert si quieres menú contextual
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    )
}

/* ---------- Diálogo para agregar materia ---------- */
@Composable
private fun AddMateriaDialog(
    onDismiss: () -> Unit,
    onSave: (Materia) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val carrerasUadec = remember {
        listOf(
            "Ingeniería de Software",
            "Derecho",
            "Medicina",
            "Arquitectura",
            "Psicología",
            "Contaduría",
            "Administración",
            "Ingeniería Mecánica",
            "Ingeniería Eléctrica",
            "Ingeniería Química"
        )
    }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar materia") },
        confirmButton = {
            val valido = nombre.isNotBlank() && clave.isNotBlank() && carrera.isNotBlank()
            TextButton(
                enabled = valido,
                onClick = {
                    onSave(
                        Materia(
                            id = "",
                            nombre = nombre.trim(),
                            clave = clave.trim(),
                            carrera = carrera.trim(),
                            descripcion = descripcion.trim()
                        )
                    )
                }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la materia") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = clave,
                    onValueChange = { clave = it.uppercase() },
                    label = { Text("Clave (p.ej. MAT-101)") },
                    singleLine = true
                )
                Box {
                    OutlinedTextField(
                        value = carrera,
                        onValueChange = { /* solo por menú */ },
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
                        carrerasUadec.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = { carrera = c; expanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    singleLine = false,
                    minLines = 2
                )
            }
        }
    )
}
