package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.data.Alumno
import com.example.univapp.data.Carrera
import com.example.univapp.data.Grupo
import com.example.univapp.data.Profesor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminGroupDetailScreen(
    groupId: String,
    onBack: () -> Unit,
    vm: AdminGroupDetailViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(key1 = groupId) {
        vm.load(groupId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalles del Grupo", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF673AB7))
            } else if (uiState.error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(uiState.error!!, color = Color.Gray, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    Button(onClick = { vm.retry(groupId) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))) { 
                        Text("Reintentar")
                    }
                }
            } else {
                GroupDetailContent(uiState)
            }
        }
    }
}

@Composable
private fun GroupDetailContent(uiState: GroupDetailUiState) {
    val groupName = uiState.group?.nombre ?: ""
    val cuatriNum = groupName.takeWhile { it.isDigit() }
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
        else -> "No especificado"
    }

    val programType = uiState.group?.programType?.uppercase() ?: "TSU"
    val autoTurno = if (programType.contains("ING")) "VESPERTINO" else "MATUTINO"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge(programType)
                    Badge(autoTurno)
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = groupName.ifEmpty { "Grupo" },
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Text(
                    text = uiState.carrera?.nombre ?: "Carrera no especificada",
                    fontSize = 15.sp,
                    color = Color(0xFF667085),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            Spacer(Modifier.height(32.dp))
        }

        item {
            Text(
                "INFORMACIÓN ACADÉMICA",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF98A2B3),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AcademicInfoCard(
                    title = "Cuatrimestre",
                    value = cuatriText,
                    icon = Icons.Default.Today,
                    iconColor = Color(0xFFF79009),
                    iconBg = Color(0xFFFFF4ED),
                    modifier = Modifier.weight(1f)
                )
                AcademicInfoCard(
                    title = "Duración",
                    value = "4 Meses",
                    icon = Icons.Default.Schedule,
                    iconColor = Color(0xFF2E90FA),
                    iconBg = Color(0xFFEFF8FF),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(32.dp))
        }

        item {
            Text(
                "TUTOR ASIGNADO",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF98A2B3),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(16.dp))
            TutorCardStyled(uiState.tutor)
            Spacer(Modifier.height(32.dp))
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "ALUMNOS ASIGNADOS",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF98A2B3),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { /* TODO */ }, contentPadding = PaddingValues(0.dp)) {
                    Text("Ver todos", color = Color(0xFF5E49B3), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (uiState.alumnos.isEmpty()) {
            item {
                EmptyState(message = "No hay alumnos asignados a este grupo.")
            }
        } else {
            items(uiState.alumnos) { student ->
                StudentListItemStyled(student)
            }
        }
        
        item {
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Badge(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6BBFB)),
        color = Color(0xFFF9F5FF)
    ) {
        Text(
            text = text.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6941C6)
        )
    }
}

@Composable
private fun AcademicInfoCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF2F4F7)),
        modifier = modifier
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(title, fontSize = 12.sp, color = Color(0xFF667085), fontWeight = FontWeight.Medium)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF101828))
        }
    }
}

@Composable
private fun TutorCardStyled(tutor: Profesor?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF2F4F7)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFFF2F4F7)),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = tutor?.nombre?.split(" ")?.filter { it.isNotBlank() }?.take(2)?.mapNotNull { it.firstOrNull()?.uppercase() }?.joinToString("") ?: "?"
                    Text(initials, fontWeight = FontWeight.Bold, color = Color(0xFF667085), fontSize = 14.sp)
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFF12B76A)))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(tutor?.nombre ?: "Tutor no asignado", fontWeight = FontWeight.Bold, color = Color(0xFF101828), fontSize = 15.sp)
                Text("ID: ${tutor?.numeroEmpleado ?: "DOC-0000"}", fontSize = 13.sp, color = Color(0xFF667085))
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEEF4FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Email, null, tint = Color(0xFF6172F3), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun StudentListItemStyled(student: Alumno) {
    val initials = student.nombre?.split(" ")?.filter { it.isNotBlank() }?.take(2)?.mapNotNull { it.firstOrNull()?.uppercase() }?.joinToString("") ?: "?"
    
    val avatarBg = when (initials.firstOrNull()) {
        in 'A'..'E' -> Color(0xFFECFDF3)
        in 'F'..'J' -> Color(0xFFEFF8FF)
        in 'K'..'O' -> Color(0xFFFEFBE8)
        else -> Color(0xFFFFF1F3)
    }
    val avatarTint = when (initials.firstOrNull()) {
        in 'A'..'E' -> Color(0xFF039855)
        in 'F'..'J' -> Color(0xFF175CD3)
        in 'K'..'O' -> Color(0xFFCA8504)
        else -> Color(0xFFE31B54)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(avatarBg),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, fontWeight = FontWeight.Bold, color = avatarTint, fontSize = 14.sp)
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(student.nombre ?: "", fontWeight = FontWeight.Bold, color = Color(0xFF101828), fontSize = 15.sp)
            Text("ID: ${student.matricula ?: ""}", fontSize = 12.sp, color = Color(0xFF667085))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color(0xFFD0D5DD), modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun EmptyState(message: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(Icons.Default.BrokenImage, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
        Text(message, color = Color.Gray, textAlign = TextAlign.Center, fontSize = 14.sp)
    }
}
