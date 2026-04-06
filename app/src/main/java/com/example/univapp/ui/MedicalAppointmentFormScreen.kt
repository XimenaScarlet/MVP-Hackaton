package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.univapp.ui.util.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalAppointmentFormScreen(
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    vm: MedicalAppointmentViewModel,
    settingsVm: SettingsViewModel = hiltViewModel()
) {
    val motivoMed by vm.reason.collectAsState()
    val priority by vm.priority.collectAsState()
    
    val darkBg = Color(0xFF0B101F)
    val cardBg = Color(0xFF131A2C)
    val accentColor = Color(0xFF818CF8)
    val subtitleColor = Color(0xFF94A3B8)

    var tieneAlergia by remember { mutableStateOf<Boolean?>(null) }
    var tomaMed by remember { mutableStateOf<Boolean?>(null) }

    // Validación para el botón continuar
    val isFormValid = motivoMed.trim().length >= 5 && priority.isNotEmpty() && tieneAlergia != null && tomaMed != null

    AppScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalles de Consulta", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = darkBg)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(darkBg).padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // MOTIVO DE LA CONSULTA
                FormLabel("MOTIVO DE LA CONSULTA")
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    color = cardBg.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp, 
                        color = if (motivoMed.isNotEmpty()) accentColor.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f)
                    )
                ) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        if (motivoMed.isEmpty()) {
                            Text("Escribe aquí el motivo...", color = Color(0xFF475569), fontSize = 15.sp)
                        }
                        val dynamicFontSize = if (motivoMed.isNotEmpty()) 28.sp else 15.sp
                        
                        BasicTextField(
                            value = motivoMed,
                            onValueChange = { vm.reason.value = it },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White, 
                                fontSize = dynamicFontSize,
                                fontWeight = if (motivoMed.isNotEmpty()) FontWeight.Black else FontWeight.Normal,
                                textAlign = if (motivoMed.isNotEmpty()) TextAlign.Center else TextAlign.Start
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // NIVEL DE URGENCIA
                FormLabel("NIVEL DE URGENCIA")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    UrgencyOption(
                        modifier = Modifier.weight(1f),
                        title = "Normal",
                        icon = Icons.Default.CheckCircle,
                        selected = priority == "Normal",
                        onClick = { vm.priority.value = "Normal" }
                    )
                    UrgencyOption(
                        modifier = Modifier.weight(1f),
                        title = "Urgente",
                        icon = Icons.Default.PriorityHigh,
                        selected = priority == "Urgente",
                        onClick = { vm.priority.value = "Urgente" }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // PREGUNTAS SI/NO
                QuestionSwitch(
                    icon = Icons.Default.MedicalServices,
                    question = "¿TIENES ALGUNA ALERGIA?",
                    selected = tieneAlergia,
                    onSelectedChange = { tieneAlergia = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                QuestionSwitch(
                    icon = Icons.Default.Medication,
                    question = "¿TOMAS MEDICAMENTO?",
                    selected = tomaMed,
                    onSelectedChange = { tomaMed = it }
                )

                Spacer(modifier = Modifier.height(120.dp))
            }

            // Bottom Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, darkBg.copy(alpha = 0.9f))))
                    .padding(24.dp)
            ) {
                Button(
                    onClick = onContinue,
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        disabledContainerColor = accentColor.copy(alpha = 0.3f)
                    )
                ) {
                    Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if(isFormValid) Color.White else Color.White.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight, 
                        null, 
                        modifier = Modifier.size(20.dp),
                        tint = if(isFormValid) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        letterSpacing = 1.sp,
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun UrgencyOption(
    modifier: Modifier,
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accentColor = Color(0xFF818CF8)
    Surface(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        color = Color(0xFF131A2C),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) accentColor else Color.White.copy(alpha = 0.05f)
        )
    ) {
        Box {
            if (selected) {
                Icon(
                    Icons.Default.Stars, 
                    null, 
                    tint = accentColor, 
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(16.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = if (selected) accentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = if (selected) accentColor else Color(0xFF475569), modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(title, color = if (selected) Color.White else Color(0xFF94A3B8), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun QuestionSwitch(
    icon: ImageVector,
    question: String,
    selected: Boolean?,
    onSelectedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF131A2C).copy(alpha = 0.5f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF818CF8), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(question, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 0.5.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                color = Color(0xFF0B101F),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selected == false) Color(0xFF818CF8) else Color.Transparent)
                            .clickable { onSelectedChange(false) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No", color = if (selected == false) Color.White else Color(0xFF475569), fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected == true) Color(0xFF818CF8) else Color.Transparent)
                            .clickable { onSelectedChange(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sí", color = if (selected == true) Color.White else Color(0xFF475569), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BasicTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, textStyle: androidx.compose.ui.text.TextStyle) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.White)
    )
}
