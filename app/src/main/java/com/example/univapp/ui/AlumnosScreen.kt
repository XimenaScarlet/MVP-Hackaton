package com.example.univapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.ui.util.AppScaffold
import com.example.univapp.ui.util.ValidatedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnosScreen(vm: AlumnosVM = viewModel()) {
    val lista by vm.alumnos.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    AppScaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Alumnos") }) }
    ) { inner ->
        Column(Modifier.padding(inner).padding(16.dp)) {
            ValidatedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre",
                maxLength = 70
            )
            Spacer(Modifier.height(8.dp))
            ValidatedTextField(
                value = matricula,
                onValueChange = { matricula = it },
                label = "Matrícula",
                maxLength = 15,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(8.dp))
            ValidatedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = "Correo",
                maxLength = 50,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Button(
                onClick = { vm.agregar(nombre, matricula, correo) },
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                enabled = nombre.isNotBlank() && matricula.isNotBlank() && correo.isNotBlank()
            ) { Text("Agregar Alumno") }

            Spacer(Modifier.height(24.dp))
            Text("Lista de Alumnos", style = MaterialTheme.typography.titleMedium)
            Divider(Modifier.padding(vertical = 12.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
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
