package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.univapp.data.Materia
import com.example.univapp.ui.util.AppScaffold
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subjectId: String,
    term: Int,
    onBack: () -> Unit = {},
    settingsVm: SettingsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val db = FirebaseFirestore.getInstance()
    var materia by remember { mutableStateOf<Materia?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    val darkBg = Color(0xFF0B101F)
    val accentColor = Color(0xFF6366F1)

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

    AppScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "DETALLES", 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.Bold, 
                        letterSpacing = 2.sp,
                        color = Color(0xFF94A3B8)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = darkBg,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = darkBg
        ) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentColor)
                }
            } else if (materia == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontró información", color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // Título de la materia
                    Text(
                        text = materia?.nombre ?: "",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 48.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Badges de Docente y Aula
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        DetailRowBadge(
                            icon = Icons.Default.Person,
                            label = "DOCENTE",
                            value = materia?.profesorNombre ?: "Dr. Julián Rodríguez"
                        )
                        
                        DetailRowBadge(
                            icon = Icons.Default.MeetingRoom,
                            label = "AULA",
                            value = materia?.aula ?: "B-204"
                        )
                    }

                    Spacer(modifier = Modifier.height(56.dp))

                    // Header de Unidades
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            "Temario / Unidades", 
                            fontSize = 22.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.White
                        )
                        Text(
                            "3 / 3 Completado", 
                            fontSize = 14.sp, 
                            color = accentColor,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Lista de Unidades
                    val unidades = if (!materia?.descripcion.isNullOrBlank() && materia?.descripcion?.contains(",") == true) {
                        materia?.descripcion?.split(",") ?: emptyList()
                    } else {
                        listOf("Introducción al Desarrollo", "Conceptos Fundamentales", "Implementación y Pruebas")
                    }

                    unidades.forEachIndexed { index, unidad ->
                        NewUnitItem(
                            number = index + 1,
                            title = unidad.trim(),
                            accentColor = accentColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun DetailRowBadge(icon: ImageVector, label: String, value: String) {
    Surface(
        color = Color(0xFF131A2C).copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color(0xFF818CF8), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Text(value, fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun NewUnitItem(number: Int, title: String, accentColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF131A2C),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            // Línea de acento lateral
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
            
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono Check circular
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, null, tint = accentColor, modifier = Modifier.size(18.dp))
                    }
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UNIDAD ${number.toString().padStart(2, '0')}",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    null,
                    tint = Color(0xFF334155),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
