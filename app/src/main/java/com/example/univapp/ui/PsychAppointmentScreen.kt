@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PsychAppointmentScreen(
    onBack: () -> Unit = {},
    onConfirmed: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // --- Estado del calendario ---
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // --- Estado de hora y textos ---
    val times = listOf("9:00", "10:00", "11:00", "14:00", "15:00", "16:00")
    val disabledTimes = setOf("11:00")
    var selectedTime by remember { mutableStateOf("14:00") }

    var reason by remember { mutableStateOf(TextFieldValue()) }
    var notes by remember { mutableStateOf(TextFieldValue()) }

    // --- Diálogos ---
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Agendar Cita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },

        // Botón fijo abajo
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Button(
                    onClick = {
                        if (reason.text.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Escribe el motivo de la cita.") }
                        } else {
                            showConfirmDialog = true
                        }
                    },
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1677FF))
                ) {
                    Text("Agendar Cita", fontSize = 18.sp)
                }
            }
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F8FA))
                .padding(pv)
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 84.dp), // deja espacio para la bottomBar
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // -------- Calendario --------
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                            Icon(Icons.Outlined.ChevronLeft, contentDescription = "Mes anterior")
                        }
                        Text(
                            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es"))} ${currentMonth.year}",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                            Icon(Icons.Outlined.ChevronRight, contentDescription = "Mes siguiente")
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Encabezado de días (L M X J V S D)
                    val days = listOf("L","M","X","J","V","S","D")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        days.forEach { d ->
                            Text(d, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center, color = Color(0xFF9AA3AF))
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    MonthGrid(
                        month = currentMonth,
                        selectedDate = selectedDate,
                        onSelect = { selectedDate = it }
                    )
                }
            }

            // -------- Horas disponibles --------
            Text("Horas Disponibles", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            FlowRowMainAxisWrap {
                times.forEach { t ->
                    val disabled = t in disabledTimes
                    val selected = t == selectedTime
                    FilterChip(
                        selected = selected,
                        onClick = { if (!disabled) selectedTime = t },
                        label = { Text(t, fontWeight = FontWeight.SemiBold) },
                        enabled = !disabled,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1677FF),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF2F4F7),
                            labelColor = Color(0xFF111827),
                            disabledContainerColor = Color(0xFFE5E7EB),
                            disabledLabelColor = Color(0xFF9CA3AF)
                        ),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .padding(6.dp)
                            .height(56.dp)
                    )
                }
            }

            // -------- Campos motivo y notas --------
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Motivo de la cita") },
                placeholder = { Text("Ej: Estrés académico, ansiedad...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas adicionales (opcional)") },
                placeholder = { Text("Cualquier información que consideres relevante...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }

    /* ------------ Diálogo de confirmación ------------ */
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar cita") },
            text = {
                Text(
                    "¿Confirmas tu cita el " +
                            "${selectedDate.dayOfMonth}/${selectedDate.monthValue}/${selectedDate.year} " +
                            "a las $selectedTime?"
                )
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancelar") }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        showSuccessDialog = true
                        onConfirmed()
                    }
                ) { Text("Confirmar") }
            }
        )
    }

    /* ------------ Diálogo de éxito ------------ */
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Cita confirmada") },
            text = {
                Text(
                    "Tu cita fue confirmada para el " +
                            "${selectedDate.dayOfMonth}/${selectedDate.monthValue}/${selectedDate.year} " +
                            "a las $selectedTime. ¡Te esperamos!"
                )
            },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false }) { Text("Aceptar") }
            }
        )
    }
}

/* ---------- Helpers UI ---------- */

@Composable
private fun MonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    onSelect: (LocalDate) -> Unit
) {
    val firstOfMonth = month.atDay(1)
    val firstDayOfWeek = DayOfWeek.MONDAY
    val offset = ((firstOfMonth.dayOfWeek.value - firstDayOfWeek.value) + 7) % 7
    val daysInMonth = month.lengthOfMonth()

    val totalCells = offset + daysInMonth
    val items = (0 until totalCells).map { index ->
        if (index < offset) null else month.atDay(index - offset + 1)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        content = {
            itemsIndexed(items) { _, day ->
                DayCell(
                    date = day,
                    selected = day == selectedDate,
                    onSelect = { day?.let(onSelect) }
                )
            }
        }
    )
}

@Composable
private fun DayCell(
    date: LocalDate?,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val label = date?.dayOfMonth?.toString() ?: ""
    val selectable = date != null
    Surface(
        onClick = onSelect,
        enabled = selectable,
        shape = RoundedCornerShape(12.dp),
        color = when {
            !selectable -> Color.Transparent
            selected    -> Color(0xFFE6F0FF)
            else        -> Color.Transparent
        }
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                color = when {
                    !selectable -> Color.Transparent
                    selected    -> Color(0xFF1C6CE1)
                    else        -> Color(0xFF111827)
                },
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun FlowRowMainAxisWrap(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
