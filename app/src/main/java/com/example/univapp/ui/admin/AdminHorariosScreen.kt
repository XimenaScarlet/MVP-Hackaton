package com.example.univapp.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHorariosScreen(
    vm: AdminViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    LaunchedEffect(Unit) { vm.refreshAll() }

    val carrerasDb by vm.carreras.collectAsState()
    val grupos     by vm.grupos.collectAsState()
    val materias   by vm.materias.collectAsState()
    val loading    by vm.loading.collectAsState()
    val errorText  by vm.error.collectAsState()

    // Si por algo /carreras llega vacío, hacemos fallback con lo visto en grupos/materias
    val carreraOptions = remember(carrerasDb, grupos, materias) {
        val fromCarr = carrerasDb.map { it.nombre }
        val fromOthers = (grupos.map { it.carrera } + materias.map { it.carrera }).filter { it.isNotBlank() }
        (fromCarr + fromOthers).distinct().sorted()
    }

    var selCarrera by remember { mutableStateOf<String?>(null) }
    val gruposDeCarrera by remember(selCarrera, grupos) {
        mutableStateOf(
            if (selCarrera.isNullOrBlank()) emptyList()
            else grupos.filter { it.carrera.equals(selCarrera, ignoreCase = true) }
                .sortedBy { it.nombre.ifBlank { it.id } }
        )
    }
    var selGrupo by remember { mutableStateOf<Grupo?>(null) }

    var horarios by remember { mutableStateOf<List<Horario>>(emptyList()) }
    LaunchedEffect(selGrupo?.id) {
        horarios = emptyList()
        selGrupo?.id?.let { gid -> vm.horariosByGrupo(gid).collectLatest { horarios = it } }
    }

    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Horario") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            if (selGrupo != null) {
                ExtendedFloatingActionButton(
                    onClick = { showAdd = true },
                    icon = { Icon(Icons.Outlined.Add, null) },
                    text = { Text("Agregar Clases") }
                )
            }
        }
    ) { pv ->
        Column(
            Modifier
                .padding(pv)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Solo para confirmar que sí hay carreras cargadas
            Text(
                if (carreraOptions.isEmpty()) "Carreras (0)" else "Carreras (${carreraOptions.size})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DropdownFieldExposed(
                    label = "Carrera",
                    placeholder = "Selecciona carrera",
                    value = selCarrera ?: "",
                    options = carreraOptions,
                    onSelect = { c -> selCarrera = c; selGrupo = null },
                    enabled = carreraOptions.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                )
                DropdownFieldExposed(
                    label = "Grupo",
                    placeholder = "Selecciona grupo",
                    value = selGrupo?.nombre ?: "",
                    options = gruposDeCarrera.map { it.nombre.ifBlank { it.id } },
                    onSelect = { name ->
                        selGrupo = gruposDeCarrera.find { it.nombre == name || it.id == name }
                    },
                    enabled = !selCarrera.isNullOrBlank(),
                    modifier = Modifier.weight(1f)
                )
            }

            if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())

            Text(
                "Horario Actual",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            TimetableGrid(horarios = horarios, timeSlots = defaultTimeSlots())

            if (selGrupo != null && !loading && horarios.isEmpty()) {
                Text("Aún no hay clases para este grupo.")
            }

            errorText?.takeIf { it.isNotBlank() }?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showAdd && selGrupo != null && !selCarrera.isNullOrBlank()) {
        AddHorarioDialog(
            grupo = selGrupo!!,
            materias = materias.filter { it.carrera.equals(selCarrera, true) },
            onDismiss = { showAdd = false },
            onSaveMany = { nuevos -> nuevos.forEach { vm.saveHorario(it) }; showAdd = false }
        )
    }
}

/* -------------------- Dropdown (Material3 Exposed) -------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownFieldExposed(
    label: String,
    placeholder: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value.ifBlank { "" },
            onValueChange = {},
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            readOnly = true,
            enabled = enabled,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

/* -------------------- Tabla semanal y utilidades -------------------- */
private data class TimeSlot(val label: String, val start: String, val end: String)

private fun defaultTimeSlots() = listOf(
    TimeSlot("9–11 am", "09:00", "11:00"),
    TimeSlot("11–1 pm", "11:00", "13:00"),
    TimeSlot("1–3 pm", "13:00", "15:00"),
    TimeSlot("3–5 pm", "15:00", "17:00"),
)

@Composable
private fun TimetableGrid(
    horarios: List<Horario>,
    timeSlots: List<TimeSlot>,
    days: List<Int> = listOf(1, 2, 3, 4, 5)
) {
    val dayNames = listOf("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo")
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            HeaderCell("Hora", weight = 0.9f)
            days.forEach { d -> HeaderCell(dayNames[d - 1], weight = 1f) }
        }
        timeSlots.forEach { slot ->
            Row(Modifier.fillMaxWidth()) {
                BodyCell(slot.label, weight = 0.9f)
                days.forEach { d ->
                    val itemsThisCell = horarios.filter {
                        it.diaSemana == d && it.horaInicio <= slot.start && it.horaFin >= slot.end
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .padding(6.dp)
                            .clip(MaterialTheme.shapes.large)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), MaterialTheme.shapes.large)
                    ) {
                        if (itemsThisCell.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) { items(itemsThisCell, key = { it.id }) { h -> ClassChip(h) } }
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun RowScope.HeaderCell(text: String, weight: Float) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp,
        modifier = Modifier.weight(weight).height(44.dp).padding(2.dp),
        shape = MaterialTheme.shapes.medium
    ) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text, fontWeight = FontWeight.SemiBold) } }
}

@Composable private fun RowScope.BodyCell(text: String, weight: Float) {
    Surface(
        tonalElevation = 0.dp,
        modifier = Modifier.weight(weight).height(44.dp).padding(2.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text) } }
}

@Composable
private fun ClassChip(h: Horario) {
    val isBlue = (h.materiaNombre.hashCode() and 1) == 0
    val bg = if (isBlue) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val fg = if (isBlue) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    Surface(tonalElevation = 2.dp, color = bg, shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(10.dp)) {
            Text(h.materiaNombre, fontWeight = FontWeight.SemiBold, color = fg)
            val aulaTxt = h.aula.takeIf { it.isNotBlank() }?.let { " • Salón $it" } ?: ""
            Text("${h.horaInicio}–${h.horaFin}$aulaTxt", style = MaterialTheme.typography.labelMedium, color = fg)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHorarioDialog(
    grupo: Grupo,
    materias: List<Materia>,
    onDismiss: () -> Unit,
    onSaveMany: (List<Horario>) -> Unit
) {
    var seleccion by remember { mutableStateOf(setOf<String>()) }
    var dia by remember { mutableStateOf(1) }
    var hIni by remember { mutableStateOf("09:00") }
    var hFin by remember { mutableStateOf("11:00") }
    var aula by remember { mutableStateOf("") }
    var openDia by remember { mutableStateOf(false) }
    val diaLabel = listOf("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar clases a ${grupo.nombre}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Materias", style = MaterialTheme.typography.labelLarge)
                if (materias.isEmpty()) Text("No hay materias registradas para esta carrera.")
                materias.forEach { m ->
                    val checked = m.id in seleccion
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(m.nombre)
                        Checkbox(checked, onCheckedChange = { ok -> seleccion = if (ok) seleccion + m.id else seleccion - m.id })
                    }
                }
                Box {
                    OutlinedTextField(
                        value = diaLabel.getOrElse(dia - 1) { "Lunes" }, onValueChange = {}, readOnly = true,
                        label = { Text("Día") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(false) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OutlinedTextField(hIni, { hIni = it }, singleLine = true, label = { Text("Hora inicio (HH:mm)") })
                OutlinedTextField(hFin, { hFin = it }, singleLine = true, label = { Text("Hora fin (HH:mm)") })
                OutlinedTextField(aula, { aula = it }, singleLine = true, label = { Text("Aula (opcional)") })
            }
        },
        confirmButton = {
            TextButton(
                enabled = seleccion.isNotEmpty(),
                onClick = {
                    val nuevos = seleccion.mapNotNull { mid ->
                        val mat = materias.find { it.id == mid } ?: return@mapNotNull null
                        Horario(grupoId = grupo.id, materiaId = mat.id, materiaNombre = mat.nombre,
                            diaSemana = dia, horaInicio = hIni.trim(), horaFin = hFin.trim(), aula = aula.trim())
                    }
                    onSaveMany(nuevos)
                }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
