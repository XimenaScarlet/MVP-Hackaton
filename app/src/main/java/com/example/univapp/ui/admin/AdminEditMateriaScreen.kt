package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.Profesor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditMateriaScreen(
    materiaId: String,
    onBack: () -> Unit,
    vm: AdminEditMateriaViewModel = viewModel(factory = AdminEditMateriaViewModelFactory(materiaId))
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var nombre by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var selectedProfesor by remember { mutableStateOf<Profesor?>(null) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.materia, uiState.profesores) {
        if (!isInitialized && uiState.materia != null) {
            uiState.materia?.let { m ->
                nombre = m.nombre
                clave = m.clave
                selectedProfesor = uiState.profesores.find { p -> p.id == m.profesorId }
                isInitialized = true
            }
        }
    }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            onBack()
            vm.onDoneNavigating()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Editar Materia", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1D2939)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color(0xFF1D2939))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        uiState.materia?.let { m ->
                            val updated = m.copy(
                                nombre = nombre,
                                clave = clave,
                                profesorId = selectedProfesor?.id
                            )
                            vm.updateMateria(updated)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0FB7A8)),
                    enabled = !uiState.isSaving && nombre.isNotBlank()
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar Cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            
            // Icono con Badge de Cámara (Estilo idéntico a la imagen)
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFFD1FADF)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.School, null, tint = Color(0xFF0FB7A8), modifier = Modifier.size(52.dp))
                    }
                }
                Surface(
                    modifier = Modifier.size(36.dp).offset(x = 4.dp, y = 4.dp),
                    shape = CircleShape,
                    color = Color(0xFF0FB7A8),
                    border = androidx.compose.foundation.BorderStroke(3.dp, Color.White),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.CameraAlt, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Tarjeta de Formulario (White Card)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    
                    EditMateriaField(label = "NOMBRE DE LA MATERIA", value = nombre, onValueChange = { nombre = it })
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.weight(0.4f)) {
                            EditMateriaField(label = "CLAVE", value = clave, readOnly = true)
                        }
                        Box(Modifier.weight(0.6f)) {
                            EditMateriaField(
                                label = "CARRERA",
                                value = uiState.currentCarrera?.nombre ?: "Sin carrera",
                                readOnly = true
                            )
                        }
                    }

                    // Grupo como campo de solo lectura
                    EditMateriaField(
                        label = "GRUPO",
                        value = if (uiState.currentGrupo != null) {
                            val cuatri = uiState.currentGrupo?.nombre?.takeWhile { it.isDigit() } ?: ""
                            val cuatriText = when (cuatri) {
                                "1" -> "Primer"
                                "2" -> "Segundo"
                                "3" -> "Tercer"
                                "4" -> "Cuarto"
                                "5" -> "Quinto"
                                "6" -> "Sexto"
                                "7" -> "Séptimo"
                                "8" -> "Octavo"
                                "9" -> "Noveno"
                                "10" -> "Décimo"
                                "11" -> "Onceavo"
                                else -> "Cuatrimestre"
                            }
                            "${uiState.currentGrupo?.nombre} — $cuatriText Cuatrimestre"
                        } else "Sin grupo",
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null, tint = Color(0xFF98A2B3)) }
                    )

                    // Profesor como Dropdown funcional
                    EditMateriaDropdown(
                        label = "PROFESOR ASIGNADO",
                        value = selectedProfesor?.nombre ?: "Pendiente por definir",
                        items = uiState.profesores,
                        itemLabel = { it.nombre },
                        onSelect = { selectedProfesor = it },
                        leadingIcon = Icons.Default.Search
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Unidades de Aprendizaje (Solo lectura y sin botón Añadir)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Unidades de Aprendizaje",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1D2939)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Lista de Unidades Estilizada como en la imagen
            val demoUnits = listOf(
                "Fundamentos de Arquitectura" to "Unidad 1 • 4 Temas",
                "Patrones de Diseño" to "Unidad 2 • 6 Temas",
                "Implementación y Calidad" to "Unidad 3 • 3 Temas"
            )
            
            demoUnits.forEach { (title, subtitle) ->
                UnitEditItemStyled(title, subtitle)
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(120.dp))
        }
    }
}

@Composable
private fun EditMateriaField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF98A2B3),
            letterSpacing = 0.5.sp
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = leadingIcon?.let { icon -> { Icon(icon, null, tint = Color(0xFF98A2B3), modifier = Modifier.size(20.dp)) } },
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF9FAFB),
                focusedContainerColor = Color(0xFFF9FAFB),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = if (readOnly) Color.Transparent else Color(0xFF0FB7A8),
                disabledTextColor = Color(0xFF1D2939),
                disabledBorderColor = Color.Transparent,
                disabledContainerColor = Color(0xFFF9FAFB)
            ),
            readOnly = readOnly,
            enabled = !readOnly,
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> EditMateriaDropdown(
    label: String,
    value: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onSelect: (T) -> Unit,
    leadingIcon: ImageVector? = null
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF98A2B3),
            letterSpacing = 0.5.sp
        )
        Spacer(Modifier.height(8.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = leadingIcon?.let { icon -> { Icon(icon, null, tint = Color(0xFF98A2B3), modifier = Modifier.size(20.dp)) } },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF9FAFB),
                    focusedContainerColor = Color(0xFFF9FAFB),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF0FB7A8)
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(itemLabel(item)) },
                        onClick = {
                            onSelect(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitEditItemStyled(title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF2F4F7))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DragIndicator, null, tint = Color(0xFFD0D5DD), modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1D2939)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF98A2B3)
                )
            }
        }
    }
}
