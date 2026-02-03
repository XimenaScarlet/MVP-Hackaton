package com.example.univapp.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.Alumno

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignStudentsScreen(
    onBack: () -> Unit,
    onSaveGroup: (List<String>) -> Unit,
    vm: AdminGruposViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedStudents by remember { mutableStateOf(setOf<String>()) }

    val filteredStudents = uiState.alumnos.filter { a ->
        (a.nombre ?: "").contains(searchQuery, ignoreCase = true) ||
        (a.matricula ?: "").contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar Alumnos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar estudiantes...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                         IconButton(onClick = { /* TODO: Implement filter */ }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lista de Estudiantes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    if (selectedStudents.isNotEmpty()) {
                        val count = selectedStudents.size
                        Text("$count Seleccionados", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filteredStudents) { student ->
                        StudentSelectItem(student = student, isSelected = student.id in selectedStudents, onSelect = {
                            selectedStudents = if (it) {
                                selectedStudents + student.id
                            } else {
                                selectedStudents - student.id
                            }
                        })
                    }
                }
            }
        }
         Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Button(
                onClick = { onSaveGroup(selectedStudents.toList()) },
                enabled = selectedStudents.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar Grupo")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Check, contentDescription = null)
            }
        }
    }
}

@Composable
private fun StudentSelectItem(student: Alumno, isSelected: Boolean, onSelect: (Boolean) -> Unit) {
    Card(
        onClick = { onSelect(!isSelected) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFE0E0E0),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text((student.nombre ?: "").take(2).uppercase(), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(student.nombre ?: "", fontWeight = FontWeight.SemiBold)
                Text("ID: ${student.matricula ?: ""}", fontSize = 12.sp, color = Color.Gray)
            }
            Checkbox(checked = isSelected, onCheckedChange = { onSelect(it) })
        }
    }
}
