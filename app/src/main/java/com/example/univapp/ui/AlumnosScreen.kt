package com.example.univapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnosScreen(vm: AlumnosVM = viewModel()) {
    val lista by vm.alumnos.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Alumnos") }) }
    ) { inner ->
        Column(Modifier.padding(inner).padding(16.dp)) {
            OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(matricula, { matricula = it }, label = { Text("Matrícula") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(correo, { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = { vm.agregar(nombre, matricula, correo) },
                modifier = Modifier.padding(top = 8.dp)
            ) { Text("Agregar") }

            Divider(Modifier.padding(vertical = 12.dp))
            LazyColumn {
                items(lista) { a ->
                    ListItem(
                        headlineContent = { Text(a.nombre ?: "") },
                        supportingContent = { Text("${a.matricula ?: ""} • ${a.correo ?: ""}") },
                        trailingContent = {
                            IconButton(onClick = { vm.borrar(a.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }
}
