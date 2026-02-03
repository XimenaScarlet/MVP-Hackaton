package com.example.univapp.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminGruposScreen(
    onBack: () -> Unit,
    onAddManually: () -> Unit,
    onImportExcel: () -> Unit,
    uiState: AdminGruposUiState,
    onCarreraSelected: (Carrera?) -> Unit,
    onGroupClick: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("TSU") }

    val isCarreraSelected = uiState.selectedCarrera != null

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Grupos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isCarreraSelected) {
                            onCarreraSelected(null)
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (isCarreraSelected) {
                Box {
                    FloatingActionButton(
                        onClick = { showMenu = true },
                        containerColor = Color(0xFF007BFF),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Grupo")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Agregar Manualmente") }, onClick = {
                            onAddManually()
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text("Importar Excel") }, onClick = {
                            onImportExcel()
                            showMenu = false
                        })
                    }
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (!isCarreraSelected) {
                // Selector de Tipo (TSU / ING)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color(0xFFF1F3F4), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    val types = listOf("TSU", "ING")
                    types.forEach { type ->
                        val isSelected = selectedType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(
                                    if (isSelected) Color.White else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedType = type },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "SELECCIONA UNA CARRERA",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.isLoading && uiState.carreras.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF007BFF))
                    }
                } else {
                    val filteredCarreras = uiState.carreras.filter { it.tipo.contains(selectedType, ignoreCase = true) }
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredCarreras) { carrera ->
                            CarreraGridItemGrupos(carrera = carrera, onClick = { onCarreraSelected(carrera) })
                        }
                    }
                }
            } else {
                // Vista de Grupos de la Carrera Seleccionada (AGRUPADOS POR CUATRIMESTRE)
                val groupedGrupos = uiState.grupos.groupBy { grupo ->
                    val name = grupo.nombre ?: ""
                    val cuatriNum = name.takeWhile { it.isDigit() }
                    if (cuatriNum.isEmpty()) "OTROS" else cuatriNum
                }.toSortedMap(compareByDescending { it.toIntOrNull() ?: 0 })

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "CARRERA SELECCIONADA",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                uiState.selectedCarrera?.nombre ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF007BFF),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    if (uiState.isLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF007BFF))
                            }
                        }
                    } else if (uiState.grupos.isEmpty()) {
                        item { EmptyStateGrupos(message = "No hay grupos en esta carrera.") }
                    } else {
                        groupedGrupos.forEach { (cuatri, grupos) ->
                            item {
                                val cuatriText = when (cuatri) {
                                    "1" -> "PRIMER"
                                    "2" -> "SEGUNDO"
                                    "3" -> "TERCER"
                                    "4" -> "CUARTO"
                                    "5" -> "QUINTO"
                                    "6" -> "SEXTO"
                                    "7" -> "SÉPTIMO"
                                    "8" -> "OCTAVO"
                                    "9" -> "NOVENO"
                                    "10" -> "DÉCIMO"
                                    "11" -> "ONCEAVO"
                                    else -> "OTROS"
                                }
                                Text(
                                    "$cuatriText CUATRIMESTRE".uppercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            val rows = grupos.chunked(2)
                            items(rows) { rowGrupos ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    rowGrupos.forEach { grupo ->
                                        GrupoButtonItemGrupos(
                                            grupo = grupo,
                                            onClick = { onGroupClick(grupo.id) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (rowGrupos.size == 1) {
                                        Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarreraGridItemGrupos(carrera: Carrera, onClick: () -> Unit) {
    val iconInfo = getIconForCarreraGrupos(carrera.nombre)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F3F4))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconInfo.second.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconInfo.first, contentDescription = null, tint = iconInfo.second)
            }
            
            Text(
                text = carrera.nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun GrupoButtonItemGrupos(grupo: Grupo, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F3F4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                grupo.nombre ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "ACTIVO",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF007BFF)
            )
        }
    }
}

private fun getIconForCarreraGrupos(nombre: String): Pair<ImageVector, Color> {
    return when {
        nombre.contains("Información", true) || nombre.contains("Software", true) -> Icons.Default.Code to Color(0xFF007AFF)
        nombre.contains("Mantenimiento", true) -> Icons.Default.PrecisionManufacturing to Color(0xFFFF9500)
        nombre.contains("Administración", true) || nombre.contains("Negocios", true) -> Icons.Default.AccountBalance to Color(0xFF34C759)
        nombre.contains("Mecatrónica", true) || nombre.contains("Robótica", true) -> Icons.Default.ElectricBolt to Color(0xFFAF52DE)
        nombre.contains("Diseño", true) -> Icons.Default.Palette to Color(0xFFFF2D55)
        nombre.contains("Procesos", true) || nombre.contains("Alimentarios", true) -> Icons.Default.Science to Color(0xFF5856D6)
        else -> Icons.Default.School to Color.Gray
    }
}

@Composable
private fun EmptyStateGrupos(message: String? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.Groups, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(80.dp))
            Text(message ?: "No hay datos disponibles", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}
