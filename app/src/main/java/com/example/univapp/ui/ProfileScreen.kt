@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.ui.profile.ProfileViewModel
import com.example.univapp.ui.util.qrBitmap

/* ---- paleta ---- */
private val ScreenBg = Color(0xFFF6F8FA)
private val CardBg = Color.White
private val HeaderText = Color(0xFF1F2937)
private val MutedText = Color(0xFF6B7280)
private val Accent = Color(0xFF10B981)
private val DividerColor = Color(0xFFE5E7EB)

@Composable
fun ProfileScreen(
    onBack: (() -> Unit)? = null
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val vm: ProfileViewModel = viewModel()
    val perfil by vm.perfil.collectAsState()
    val err by vm.err.collectAsState()
    var showQr by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = {
                        onBack?.invoke() ?: backDispatcher?.onBackPressed()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(color = ScreenBg) {
                Button(
                    onClick = { showQr = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Mostrar QR", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    ) { pv ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(pv)
        ) {
            when {
                err != null -> Text(
                    text = err ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                )

                perfil == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                else -> {
                    val p = perfil!!

                    val safe = { s: String? -> if (s.isNullOrBlank()) "No aplica" else s.trim() }

                    // valores fijos
                    val semestreVal = "9°"
                    val grupoVal = "IDGSA"
                    val direccionVal = "Saltillo" // siempre Saltillo

                    val qrPayload = "univapp://alumno?id=${p.matricula}&v=1"

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp, top = 12.dp,
                            bottom = 96.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            HeaderBlock(
                                name = safe(p.nombre),
                                program = safe(p.carrera)
                            )
                        }

                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                SquareStatCard(
                                    title = "Semestre",
                                    value = semestreVal,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(96.dp)
                                )
                                SquareStatCard(
                                    title = "Grupo",
                                    value = grupoVal,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(96.dp)
                                )
                            }
                        }

                        item {
                            InfoList(
                                listOf(
                                    "Matrícula" to safe(p.matricula),
                                    "Correo institucional" to safe(p.correo),
                                    "Teléfono" to safe(p.telefono),
                                    "Dirección" to direccionVal, // siempre Saltillo
                                    "Fecha de nacimiento" to safe(p.fechaNacimiento)
                                )
                            )
                        }
                    }

                    if (showQr) {
                        AlertDialog(
                            onDismissRequest = { showQr = false },
                            title = { Text("Tu código QR") },
                            text = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        bitmap = qrBitmap(qrPayload, size = 720),
                                        contentDescription = "QR Alumno",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(260.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = "Matrícula: ${safe(p.matricula)}",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showQr = false }) { Text("Cerrar") }
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/* ---------- UI blocks ---------- */

@Composable
private fun HeaderBlock(
    name: String,
    program: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFFE6FFFA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(52.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = name,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = HeaderText,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(text = program, color = MutedText, fontSize = 14.sp)
    }
}

@Composable
private fun SquareStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = MutedText, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = HeaderText
            )
        }
    }
}

@Composable
private fun InfoList(items: List<Pair<String, String>>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            items.forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        color = MutedText,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        color = HeaderText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End
                    )
                }
                if (index < items.lastIndex) Divider(color = DividerColor, thickness = 1.dp)
            }
        }
    }
}
