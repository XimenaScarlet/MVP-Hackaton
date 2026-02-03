package com.example.univapp.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.example.univapp.data.Horario
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHorariosScreen(
    onBack: () -> Unit,
    onAddManually: () -> Unit,
    onImportExcel: () -> Unit,
    vm: AdminHorariosViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val isDetailView = uiState.selectedGrupo != null
    val isCarreraSelected = uiState.selectedCarrera != null
    var showMenu by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf("TSU") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Horarios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isDetailView) {
                            vm.onBack()
                        } else if (isCarreraSelected) {
                            vm.onCarreraSelected(null)
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
            if (isDetailView) {
                Box {
                    FloatingActionButton(
                        onClick = { showMenu = true },
                        containerColor = Color(0xFF007BFF),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Horario")
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

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val filteredCarreras = uiState.carreras.filter { it.tipo.contains(selectedType, ignoreCase = true) }
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredCarreras) { carrera ->
                            CarreraGridItem(carrera = carrera, onClick = { vm.onCarreraSelected(carrera) })
                        }
                    }
                }
            } else if (!isDetailView) {
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
                                CircularProgressIndicator()
                            }
                        }
                    } else if (uiState.grupos.isEmpty()) {
                        item { EmptyState(message = "No hay grupos en esta carrera.") }
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
                                        GrupoButtonItem(
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
                // Vista de Horario
                val programType = uiState.selectedCarrera?.tipo ?: "TSU"
                val isIngenieria = programType.contains("ING", ignoreCase = true)
                
                Column(Modifier.padding(horizontal = 16.dp)) {
                    GroupHeader(uiState.selectedGrupo, uiState.selectedCarrera)
                }
                WeeklyCalendarLikeCapture(horarios = uiState.horarios, isIngenieria = isIngenieria)
            }
        }
    }
}

@Composable
private fun CarreraGridItem(carrera: Carrera, onClick: () -> Unit) {
    val iconInfo = getIconForCarrera(carrera.nombre)
    
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
private fun GrupoButtonItem(grupo: Grupo, onClick: () -> Unit, modifier: Modifier = Modifier) {
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

private fun getIconForCarrera(nombre: String): Pair<ImageVector, Color> {
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
private fun GroupHeader(grupo: Grupo?, carrera: Carrera?) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Grupo: ${grupo?.nombre ?: ""}", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            AssistChip(
                onClick = { },
                label = { Text("Active", color = Color(0xFF4CAF50)) },
                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)),
                border = null
            )
        }
        Text("${carrera?.nombre ?: ""} • Jan - Jun 2024", color = Color.Gray)
    }
}

@Composable
private fun EmptyState(message: String? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.EventBusy, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(80.dp))
            Text(message ?: "No hay datos disponibles", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun WeeklyCalendarLikeCapture(
    horarios: List<Horario>,
    isIngenieria: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBg = Color.White
    val border = Color(0xFFE0E0E0)
    val softText = Color(0xFF6B7280)

    val locale = Locale("es", "MX")
    val monday = LocalDate.now().with(DayOfWeek.MONDAY)
    val days = (0..4).map { monday.plusDays(it.toLong()) }

    // RANGO AJUSTADO: 17:30 (5:30pm) - 22:00 (10:00pm) para ING, 7am - 8pm para TSU
    val startH = if (isIngenieria) 17 else 7
    val startM = if (isIngenieria) 30 else 0
    val endH = if (isIngenieria) 22 else 20
    val endM = 0
    
    val timeSlots = generateHalfHourSlots(startHour = startH, startMinute = startM, endHour = endH, endMinute = endM)

    val timeColWidth = 60.dp
    val slotHeight = 44.dp
    val headerHeight = 56.dp

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val dayColWidth = (maxWidth - timeColWidth) / 5f

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, border),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F6F8))
            ) {
                Box(
                    modifier = Modifier
                        .width(timeColWidth)
                        .height(headerHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text("HORA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = softText)
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    days.forEach { date ->
                        val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
                            .replace(".", "")
                            .uppercase(locale)

                        Box(
                            modifier = Modifier
                                .width(dayColWidth)
                                .height(headerHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(dayName, fontSize = 11.sp, color = softText, fontWeight = FontWeight.Bold)
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            val vScroll = rememberScrollState()

            Row(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .width(timeColWidth)
                        .fillMaxHeight()
                        .background(Color(0xFFF9FAFB))
                        .verticalScroll(vScroll),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    timeSlots.forEach { t ->
                        Box(
                            modifier = Modifier
                                .height(slotHeight)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(t, color = softText, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(vScroll)
                        .background(cardBg)
                ) {
                    GridLines(
                        rowsCount = timeSlots.size,
                        colsCount = days.size,
                        rowHeight = slotHeight,
                        colWidth = dayColWidth,
                        lineColor = border.copy(alpha = 0.5f)
                    )

                    if (horarios.isEmpty()) {
                        Box(
                            modifier = Modifier.matchParentSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay clases", color = softText, fontSize = 12.sp)
                        }
                    } else {
                        // Bloques de horario
                    }
                }
            }
        }
    }
}

private fun generateHalfHourSlots(
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int
): List<String> {
    val result = mutableListOf<String>()
    var h = startHour
    var m = startMinute

    fun fmt(hh: Int, mm: Int) = String.format("%02d:%02d", hh, mm)

    while (h < endHour || (h == endHour && m <= endMinute)) {
        result.add(fmt(h, m))
        m += 30
        if (m >= 60) {
            m -= 60
            h += 1
        }
    }
    return result
}

@Composable
private fun GridLines(
    rowsCount: Int,
    colsCount: Int,
    rowHeight: Dp,
    colWidth: Dp,
    lineColor: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight * rowsCount)
    ) {
        val w = size.width
        val h = size.height
        val colPx = colWidth.toPx()
        for (i in 1 until colsCount) {
            val x = colPx * i
            drawLine(lineColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
        }
        val rowPx = rowHeight.toPx()
        for (i in 1..rowsCount) {
            val y = rowPx * i
            drawLine(lineColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
        }
    }
}
