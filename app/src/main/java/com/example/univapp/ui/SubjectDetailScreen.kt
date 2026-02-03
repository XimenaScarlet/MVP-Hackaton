@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.univapp.data.Materia

@Composable
fun SubjectDetailScreen(
    subjectId: String,
    term: Int,
    onBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val db = FirebaseFirestore.getInstance()
    var materia by remember { mutableStateOf<Materia?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(subjectId) {
        try {
            val doc = db.collection("materias").document(subjectId).get().await()
            materia = doc.toObject(Materia::class.java)?.apply { id = doc.id }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
            }
        } else if (materia == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se encontró información de la materia")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Top Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Atrás",
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFF6366F1)
                        )
                    }
                    Text(
                        text = "Materia en Curso",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                }

                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    // Badges
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "SEMESTRE $term",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6366F1),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            color = Color(0xFFFFF1F2),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                materia?.periodo ?: "GENERAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF43F5E),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = materia?.nombre ?: "",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1A1C1E)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Professor Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF818CF8))
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(60.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Face, null, tint = Color(0xFF818CF8), modifier = Modifier.size(36.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(materia?.profesorId ?: "Profesor UTC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF4ADE80), CircleShape))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("DOCENTE ASIGNADO", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                InfoMetric(Modifier.weight(1f), Icons.Default.Schedule, "TURNO", materia?.turno ?: "N/A")
                                InfoMetric(Modifier.weight(1f), Icons.Default.Map, "AULA", materia?.aula ?: "N/A")
                                InfoMetric(Modifier.weight(1f), Icons.Default.Star, "CRÉDITOS", "${materia?.creditos ?: 0}")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text("Descripción", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = materia?.descripcion ?: "Sin descripción disponible para esta materia.",
                        fontSize = 15.sp,
                        color = Color(0xFF6B7280),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoMetric(modifier: Modifier, icon: ImageVector, label: String, value: String) {
    Surface(
        modifier = modifier.height(80.dp),
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
            Text(value, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
