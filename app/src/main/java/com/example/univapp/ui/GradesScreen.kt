@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/* ---------- Paleta/estilos ---------- */
private val ScreenBg = Color(0xFFF6F8FA)
private val CardBg = Color.White
private val HeaderText = Color(0xFF22272B)
private val MutedText = Color(0xFF5F6B6F)
private val RowDivider = Color(0xFFE6ECEF)
private val Accent = Color(0xFF20C16C)
private val Warn = Color(0xFFEDC531)
private val Danger = Color(0xFFE05A4E)

/* ---------- Modelo de datos (listo para DB luego) ---------- */
data class UnitGrade(
    val name: String,
    val score: Double // 0.0 - 10.0
)

data class SubjectWithUnits(
    val name: String,
    val units: List<UnitGrade>
) {
    val average: Double
        get() = if (units.isEmpty()) 0.0 else units.map { it.score }.average()
}

data class Cuatrimestre(
    val number: Int, // 1..10
    val subjects: List<SubjectWithUnits>
) {
    val average: Double
        get() = if (subjects.isEmpty()) 0.0 else subjects.map { it.average }.average()
}

/* ---------- Muestra (luego reemplazas por DB) ---------- */
private fun fakeData(): List<Cuatrimestre> = listOf(
    Cuatrimestre(
        number = 1,
        subjects = listOf(
            SubjectWithUnits(
                "Fundamentos de Programación",
                listOf(
                    UnitGrade("U1: Lógica y algoritmos", 9.0),
                    UnitGrade("U2: Estructuras secuenciales", 9.5),
                    UnitGrade("U3: Condicionales y ciclos", 8.7)
                )
            ),
            SubjectWithUnits(
                "Matemáticas I",
                listOf(
                    UnitGrade("U1: Aritmética", 8.5),
                    UnitGrade("U2: Álgebra básica", 8.8),
                    UnitGrade("U3: Funciones", 9.2)
                )
            ),
            SubjectWithUnits(
                "Habilidades de Comunicación",
                listOf(
                    UnitGrade("U1: Lectura crítica", 9.8),
                    UnitGrade("U2: Redacción", 9.2)
                )
            )
        )
    ),
    Cuatrimestre(
        number = 2,
        subjects = listOf(
            SubjectWithUnits(
                "POO",
                listOf(
                    UnitGrade("U1: Clases/Objetos", 8.9),
                    UnitGrade("U2: Herencia", 9.3),
                    UnitGrade("U3: Polimorfismo", 9.0)
                )
            ),
            SubjectWithUnits(
                "Estructuras de Datos",
                listOf(
                    UnitGrade("U1: Listas/Pilas/Colas", 8.3),
                    UnitGrade("U2: Árboles", 8.5)
                )
            )
        )
    )
)

/* ---------- Pantalla ---------- */
@Composable
fun GradesScreen(
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // Estado
    val allTerms = remember { fakeData() }              // <--- reemplaza por DB
    var selectedTerm by remember { mutableStateOf(allTerms.firstOrNull()?.number ?: 1) }
    var showTermPicker by remember { mutableStateOf(false) }

    val term: Cuatrimestre = remember(selectedTerm, allTerms) {
        allTerms.firstOrNull { it.number == selectedTerm } ?: Cuatrimestre(selectedTerm, emptyList())
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Calificaciones") },
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() ?: backDispatcher?.onBackPressed() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(pv)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cabecera centrada con anillo (sin número grande)
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Cuatrimestre $selectedTerm", color = MutedText, fontSize = 14.sp)
                    Spacer(Modifier.height(10.dp))
                    val avg = term.average
                    CircularAverage(
                        value = ((avg / 10.0).toFloat()),
                        size = 120.dp,
                        stroke = 12.dp,
                        centerText = "${(avg * 10).roundToInt()}%"
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("Promedio del cuatrimestre", color = MutedText, fontSize = 13.sp)
                }
            }

            // Acciones: filtrar cuatri / exportar
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                FilledTonalButton(
                    onClick = { showTermPicker = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Outlined.FilterList, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Filtrar por cuatri")
                }
                FilledTonalButton(
                    onClick = { exportGradesPdf(context, term) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Outlined.PictureAsPdf, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Exportar PDF")
                }
            }

            Text(
                "Tus materias",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = HeaderText,
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (term.subjects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay materias capturadas para este cuatrimestre.",
                        color = MutedText,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(term.subjects) { subject ->
                        SubjectRowProgress(subject)
                    }
                }
            }
        }
    }

    if (showTermPicker) {
        CuatrimestrePickerSheet(
            current = selectedTerm,
            onSelect = {
                selectedTerm = it
                showTermPicker = false
            },
            onDismiss = { showTermPicker = false }
        )
    }
}

/* ---------- UI Components ---------- */

@Composable
private fun CircularAverage(
    value: Float, // 0f..1f
    size: Dp,
    stroke: Dp,
    centerText: String
) {
    val bg = RowDivider
    val color = when {
        value >= 0.8f -> Accent
        value >= 0.6f -> Warn
        else -> Danger
    }
    Box(
        modifier = Modifier
            .size(size)
            .drawBehind {
                drawCircle(color = bg, style = Stroke(width = stroke.toPx()))
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * value,
                    useCenter = false,
                    style = Stroke(width = stroke.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(centerText, color = HeaderText, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SubjectRowProgress(subject: SubjectWithUnits) {
    var expanded by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    val avg = subject.average
    val percent = (avg * 10).roundToInt() // 0..100
    val indicatorColor = barColor(percent)

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { expanded = !expanded }
                ) {
                    Text(subject.name, fontWeight = FontWeight.SemiBold, color = HeaderText)
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { ((avg / 10.0).toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = indicatorColor,
                        trackColor = RowDivider
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Progreso: ${percent}%",
                        color = MutedText,
                        fontSize = 12.sp
                    )
                }

                // Flechita con menú de unidades
                Box {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(
                            imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                            contentDescription = "Ver unidades",
                            tint = Color(0xFF60707A)
                        )
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        subject.units.forEach { u ->
                            DropdownMenuItem(
                                text = { Text("${u.name}: ${formatScore(u.score)}") },
                                onClick = { menuOpen = false }
                            )
                        }
                    }
                }
            }

            if (expanded) {
                Divider(color = RowDivider)
                subject.units.forEachIndexed { idx, u ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(u.name, modifier = Modifier.weight(1f), color = HeaderText)
                        Text(
                            formatScore(u.score),
                            color = HeaderText,
                            textAlign = TextAlign.End,
                            modifier = Modifier.width(56.dp)
                        )
                    }
                    if (idx < subject.units.lastIndex) Divider(color = RowDivider)
                }
            }
        }
    }
}

private fun barColor(percent: Int): Color = when {
    percent >= 80 -> Accent
    percent >= 60 -> Warn
    else -> Danger
}

private fun formatScore(value: Double): String {
    val v = (value * 10).roundToInt() / 10.0
    return "%.1f".format(v)
}

/* ---------- BottomSheet para elegir cuatrimestre ---------- */
@Composable
private fun CuatrimestrePickerSheet(
    current: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "Selecciona cuatrimestre",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = HeaderText,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            repeat(10) { idx ->
                val number = idx + 1
                val selected = number == current
                val bg = if (selected) Accent.copy(alpha = .08f) else Color.Transparent
                val tint = if (selected) Accent else HeaderText
                Surface(
                    color = bg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(number) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Cuatrimestre $number",
                            color = tint,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

/* ---------- Exportar PDF (simple y funcional) ---------- */
private fun exportGradesPdf(context: Context, term: Cuatrimestre) {
    try {
        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 px @72dpi
        val page = pdf.startPage(pageInfo)
        val c: Canvas = page.canvas

        val titlePaint = Paint().apply {
            isAntiAlias = true
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }
        val normal = Paint().apply { textSize = 12f; isAntiAlias = true }
        val barPaint = Paint().apply { isAntiAlias = true }
        val divider = Paint().apply { color = 0xFFDDDDDD.toInt(); strokeWidth = 1f }

        var y = 40f
        c.drawText("Calificaciones — Cuatrimestre ${term.number}", 40f, y, titlePaint)
        y += 18f
        c.drawText("Promedio del cuatrimestre: ${"%.1f".format(term.average)} (${(term.average * 10).roundToInt()}%)", 40f, y, normal)
        y += 20f
        c.drawLine(40f, y, 555f, y, divider)
        y += 16f

        term.subjects.forEach { s ->
            val avg = s.average
            val pct = (avg * 10).roundToInt()
            c.drawText(s.name, 40f, y, normal)
            c.drawText("${pct}%", 515f, y, normal)
            y += 6f
            // barra
            val startX = 40f; val endX = 520f; val h = 8f
            barPaint.color = 0xFFE6ECEF.toInt();
            c.drawRect(startX, y, endX, y + h, barPaint)
            val w = (endX - startX) * (pct / 100f)
            barPaint.color = when {
                pct >= 80 -> 0xFF20C16C.toInt()
                pct >= 60 -> 0xFFEDC531.toInt()
                else -> 0xFFE05A4E.toInt()
            }
            c.drawRect(startX, y, startX + w, y + h, barPaint)
            y += 20f
            if (y > 800f) {
                pdf.finishPage(page)
                val pInfo = PdfDocument.PageInfo.Builder(595, 842, pdf.pages.size + 1).create()
                val p = pdf.startPage(pInfo)
                y = 40f
            }
        }

        pdf.finishPage(page)

        val filename = "Calificaciones_Q${term.number}.pdf"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, filename)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { out -> pdf.writeTo(out) }
                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(it, contentValues, null, null)
            }
            showToast(context, "PDF guardado en Descargas: $filename")
        } else {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(dir, filename)
            file.outputStream().use { out -> pdf.writeTo(out) }
            showToast(context, "PDF guardado en archivos de la app: ${file.absolutePath}")
        }
        pdf.close()
    } catch (e: Exception) {
        showToast(context, "Error al exportar PDF: ${e.message}")
    }
}

private fun showToast(context: Context, msg: String) {
    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
}