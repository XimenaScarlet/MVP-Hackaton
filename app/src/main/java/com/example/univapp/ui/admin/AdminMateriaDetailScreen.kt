package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMateriaDetailScreen(
    materiaId: String,
    onBack: () -> Unit,
    onEditMateria: (String) -> Unit,
    vm: AdminMateriaDetailViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = materiaId) {
        vm.load(materiaId)
    }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            onBack()
            vm.onDoneNavigating()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("DETALLES", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF98A2B3), letterSpacing = 2.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
                    }
                },
                actions = {
                    var menuExpanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Outlined.MoreHoriz, contentDescription = "Opciones", tint = Color(0xFF98A2B3))
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Eliminar Materia", color = Color.Red) },
                                onClick = {
                                    menuExpanded = false
                                    showDeleteDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Button(
                    onClick = { onEditMateria(materiaId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D2939))
                ) {
                    Icon(Icons.Default.Notes, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Editar Materia", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar Materia") },
                text = { Text("¿Estás seguro de que deseas eliminar esta materia?") },
                confirmButton = {
                    TextButton(onClick = { vm.deleteMateria(materiaId); showDeleteDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                }
            )
        }

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF1D2939))
            } else {
                MateriaDetailContentStyled(uiState)
            }
        }
    }
}

@Composable
private fun MateriaDetailContentStyled(uiState: MateriaDetailUiState) {
    val m = uiState.materia
    val cuatriNum = uiState.grupo?.nombre?.takeWhile { it.isDigit() } ?: ""
    val cuatriText = when (cuatriNum) {
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
        else -> "N/A"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Central Icon (Drafting Compass style)
        item {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .border(1.dp, Color(0xFFF2F4F7), RoundedCornerShape(24.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CompassCalibration, null, tint = Color(0xFF1D2939), modifier = Modifier.size(36.dp))
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        // Título Estilo Italic Serif
        item {
            Text(
                text = m?.nombre ?: "Arquitectura de Software",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                textAlign = TextAlign.Center,
                color = Color(0xFF1D2939),
                lineHeight = 40.sp
            )
            Spacer(Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF9FAFB)
            ) {
                Text(
                    text = m?.clave ?: "ARQ-2024-TI",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF98A2B3),
                    letterSpacing = 1.sp
                )
            }
            Spacer(Modifier.height(48.dp))
        }

        // Bloque de Información
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFF2F4F7), RoundedCornerShape(24.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                InfoItemRow(
                    icon = Icons.Default.AccountBalance,
                    label = "PROGRAMA ACADÉMICO",
                    value = uiState.carrera?.nombre ?: "Tecnologías de la Información"
                )
                InfoItemRow(
                    icon = Icons.Default.Share,
                    label = "COHORTE Y GRUPO",
                    value = "${uiState.grupo?.nombre ?: "9IDGSA"} — $cuatriText Cuatrimestre"
                )
                InfoItemRow(
                    icon = Icons.Default.LocationOn,
                    label = "CATEDRÁTICO ASIGNADO",
                    value = uiState.profesor?.nombre ?: "Pendiente por definir",
                    isPending = uiState.profesor == null
                )
            }
            Spacer(Modifier.height(40.dp))
        }

        // Unidades Temáticas Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "CURRÍCULO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5E49B3),
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        "Unidades Temáticas",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color(0xFF1D2939)
                    )
                    Text("3 Módulos", fontSize = 12.sp, color = Color(0xFF98A2B3), modifier = Modifier.padding(bottom = 4.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // Lista de Unidades
        val units = listOf(
            "Fundamentos de Arquitectura",
            "Patrones de Diseño",
            "Implementación y Calidad"
        )
        itemsIndexed(units) { index, title ->
            val formattedIndex = String.format("%02d.", index + 1)
            UnitCardItem(formattedIndex, title)
            Spacer(Modifier.height(16.dp))
        }
        
        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
private fun InfoItemRow(icon: ImageVector, label: String, value: String, isPending: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color(0xFFF9FAFB)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color(0xFF475467), modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF98A2B3), letterSpacing = 0.5.sp)
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPending) Color(0xFFD0D5DD) else Color(0xFF1D2939),
                fontStyle = if (isPending) FontStyle.Italic else FontStyle.Normal
            )
        }
    }
}

@Composable
private fun UnitCardItem(index: String, title: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF2F4F7))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = index,
                fontSize = 26.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                color = Color(0xFFF2F4F7), // Número desvanecido
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1D2939))
                Text("4 TEMAS • 12 HORAS", fontSize = 11.sp, color = Color(0xFF98A2B3), fontWeight = FontWeight.Bold)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color(0xFFD0D5DD))
        }
    }
}
