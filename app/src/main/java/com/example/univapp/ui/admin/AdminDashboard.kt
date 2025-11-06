package com.example.univapp.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    vm: AdminViewModel = viewModel(),
    onLogout: () -> Unit,
    onOpenAlumnos: () -> Unit,
    onOpenMaterias: () -> Unit,
    onOpenGrupos: () -> Unit,
    onOpenHorarios: () -> Unit,
    onOpenProfesores: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPerfil: () -> Unit
) {
    val alumnos by vm.alumnos.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.refreshAll() }

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hola, Admin",
                        modifier = Modifier.clickable { expanded = true }
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Outlined.Person, contentDescription = "Perfil")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    expanded = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            if (loading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
            }

            // Fila 1
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    title = "Alumnos",
                    icon = { Icon(Icons.Outlined.Group, contentDescription = null) },
                    onClick = onOpenAlumnos,
                    modifier = Modifier.weight(1f)
                )
                DashboardCard(
                    title = "Materias",
                    icon = { Icon(Icons.Outlined.MenuBook, contentDescription = null) },
                    onClick = onOpenMaterias,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Fila 2
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    title = "Grupos",
                    icon = { Icon(Icons.Outlined.Group, contentDescription = null) },
                    onClick = onOpenGrupos,
                    modifier = Modifier.weight(1f)
                )
                DashboardCard(
                    title = "Horarios",
                    icon = { Icon(Icons.Outlined.Schedule, contentDescription = null) },
                    onClick = onOpenHorarios,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Fila 3
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    title = "Profesores",
                    icon = { Icon(Icons.Outlined.School, contentDescription = null) },
                    onClick = onOpenProfesores,
                    modifier = Modifier.weight(1f)
                )
                DashboardCard(
                    title = "Configuración",
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Fila 4
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    title = "Perfil",
                    icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    onClick = onOpenPerfil,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.weight(1f))
            }

            error?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(12.dp))
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icon?.invoke()
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
