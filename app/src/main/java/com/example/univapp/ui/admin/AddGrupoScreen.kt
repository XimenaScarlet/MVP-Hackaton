package com.example.univapp.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.Profesor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGrupoScreen(
    carrera: String,
    onBack: () -> Unit,
    onGrupoAdded: () -> Unit,
    vm: AddGrupoViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var tipoPrograma by remember { mutableStateOf("Ingeniería") }
    var selectedTutor by remember { mutableStateOf<Profesor?>(null) }

    Scaffold(
        containerColor = Color(0xFFF5F6F8),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agregar Grupo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Button(
                onClick = { 
                    vm.saveGrupo(nombre, tipoPrograma, selectedTutor, carrera, onGrupoAdded)
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) {
                Text("Siguiente", fontSize = 16.sp)
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoCard(text = "Complete los detalles a continuación para registrar un nuevo grupo en el sistema universitario.")

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Grupo") },
                placeholder = { Text("Ej: 1°A") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text("Tipo de Programa", modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            SegmentedButton(listOf("Ingeniería", "TSU"), tipoPrograma) { tipoPrograma = it }

            Spacer(Modifier.height(16.dp))

            TutorSelector(uiState.profesores, selectedTutor) { selectedTutor = it }
        }
    }
}

@Composable
private fun InfoCard(text: String) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE6E0EC))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(40.dp).padding(end = 16.dp))
            Column {
                Text("Información Académica", fontWeight = FontWeight.Bold)
                Text(text, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun SegmentedButton(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                options.lastIndex -> RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                else -> RoundedCornerShape(0.dp)
            }
            Button(
                onClick = { onSelected(option) },
                modifier = Modifier.weight(1f),
                shape = shape,
                colors = ButtonDefaults.buttonColors(containerColor = if (selected == option) Color(0xFF673AB7) else Color.White, contentColor = if (selected == option) Color.White else Color.Black)
            ) {
                Text(option)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TutorSelector(tutores: List<Profesor>, selected: Profesor?, onSelected: (Profesor) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected?.nombre ?: "Seleccionar Tutor",
            onValueChange = {}, // No-op
            readOnly = true,
            label = { Text("Tutor") },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            tutores.forEach { tutor ->
                DropdownMenuItem(text = { Text(tutor.nombre ?: "") }, onClick = {
                    onSelected(tutor)
                    expanded = false
                })
            }
        }
    }
}
