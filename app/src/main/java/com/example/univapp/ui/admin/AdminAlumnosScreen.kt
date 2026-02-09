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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.Alumno
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAlumnosScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onAddManually: () -> Unit,
    onImportExcel: () -> Unit,
    vm: AdminAlumnosViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("TSU") }

    val isCarreraSelected = uiState.selectedCarrera != null
    val isGrupoSelected = uiState.selectedGrupo != null

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    if (isGrupoSelected) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("ALUMNOS", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF98A2B3), letterSpacing = 2.sp)
                        }
                    } else {
                        Text("Alumnos", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            isGrupoSelected -> vm.onGrupoSelected(null)
                            isCarreraSelected -> vm.onCarreraSelected(null)
                            else -> onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (isGrupoSelected) {
                Box {
                    FloatingActionButton(
                        onClick = { showMenu = true },
                        containerColor = Color(0xFF007BFF),
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, "Añadir Alumno")
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
        // Box con fillMaxSize asegura que el área de contenido ocupe todo el espacio
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (!isCarreraSelected) {
                Column {
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
                        color = Color(0xFF98A2B3),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.isLoadingCarreras) {
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
                                CarreraGridItemAlumnos(carrera = carrera, onClick = { vm.onCarreraSelected(carrera) })
                            }
                        }
                    }
                }
            } else if (!isGrupoSelected) {
                // Vista de Grupos - Asegurando que LazyColumn llene el espacio para permitir scroll
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
                                color = Color(0xFF98A2B3),
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

                    if (uiState.isLoadingGrupos) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF007BFF))
                            }
                        }
                    } else if (uiState.grupos.isEmpty()) {
                        item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Text("No hay grupos en esta carrera.") } }
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
                                    color = Color(0xFF98A2B3),
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
                                        GrupoButtonItemAlumnos(
                                            grupo = grupo,
                                            onClick = { vm.onGrupoSelected(grupo) },
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
            } else {
                // Vista de Lista de Alumnos - Scroll activo por LazyColumn
                Column(Modifier.fillMaxSize()) {
                    Text(
                        text = uiState.selectedGrupo?.nombre ?: "",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1D2939),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    )
                    
                    if (uiState.isLoadingAlumnos) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF007BFF))
                        }
                    } else if (uiState.alumnos.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay alumnos en este grupo.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                            modifier = Modifier.fillMaxSize() // Asegurar que ocupa todo el espacio
                        ) {
                            items(uiState.alumnos, key = { it.id }) { alumno ->
                                AlumnoListItemStyled(alumno = alumno, onClick = { onEdit(alumno.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarreraGridItemAlumnos(carrera: Carrera, onClick: () -> Unit) {
    val iconInfo = getIconForCarreraAlumnos(carrera.nombre)
    
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
private fun GrupoButtonItemAlumnos(grupo: Grupo, onClick: () -> Unit, modifier: Modifier = Modifier) {
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

private fun getIconForCarreraAlumnos(nombre: String): Pair<ImageVector, Color> {
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
private fun AlumnoListItemStyled(alumno: Alumno, onClick: () -> Unit) {
    val initials = alumno.nombre?.split(" ")?.filter { it.isNotBlank() }?.take(2)?.mapNotNull { it.firstOrNull()?.uppercase() }?.joinToString("") ?: "?"
    
    val avatarBg = when (initials.firstOrNull()) {
        in 'A'..'E' -> Color(0xFFEFF8FF)
        in 'F'..'J' -> Color(0xFFECFDF3)
        in 'K'..'O' -> Color(0xFFFEFBE8)
        else -> Color(0xFFFFF1F3)
    }
    val avatarTint = when (initials.firstOrNull()) {
        in 'A'..'E' -> Color(0xFF175CD3)
        in 'F'..'J' -> Color(0xFF039855)
        in 'K'..'O' -> Color(0xFFCA8504)
        else -> Color(0xFFE31B54)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF2F4F7))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, fontWeight = FontWeight.Bold, color = avatarTint, fontSize = 14.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(alumno.nombre ?: "", fontWeight = FontWeight.Bold, color = Color(0xFF1D2939), fontSize = 16.sp)
                Text("ID: ${alumno.matricula ?: ""}", color = Color(0xFF667085), fontSize = 13.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFFD0D5DD), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.MoreVert, contentDescription = "Más", tint = Color(0xFFD0D5DD), modifier = Modifier.size(20.dp))
            }
        }
    }
}
