package com.example.univapp.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.ui.util.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportProfesoresScreen(
    onBack: () -> Unit,
    viewModel: AdminImportProfesoresViewModel = viewModel()
) {
    val importState by viewModel.importState.collectAsState()
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> selectedFileUri = uri }
    )

    AppScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Importar Profesores", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F6F8))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GuiaImportProfesores()
            Spacer(modifier = Modifier.height(24.dp))
            SubirArchivoProfesores(selectedFileUri) {
                launcher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            }
            Spacer(modifier = Modifier.height(16.dp))
            AvisoImportanciaProfesores()
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    selectedFileUri?.let {
                        val inputStream = context.contentResolver.openInputStream(it)
                        viewModel.importProfesoresFromUri(it, inputStream)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                enabled = selectedFileUri != null && importState != ImportState.Loading
            ) {
                if (importState == ImportState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Importar Datos", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
            TextButton(onClick = onBack) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    }

    when (val state = importState) {
        is ImportState.Success -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                title = { Text("Importación Exitosa") },
                text = { Text("${state.result.totalProcessed} profesores han sido importados.") },
                confirmButton = {
                    Button(onClick = { viewModel.resetState(); onBack() }) { Text("Aceptar") }
                }
            )
        }
        is ImportState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                title = { Text("Error en la Importación") },
                text = { Text(state.message) },
                confirmButton = {
                    Button(onClick = { viewModel.resetState() }) { Text("Aceptar") }
                }
            )
        }
        else -> {}
    }
}

@Composable
private fun GuiaImportProfesores() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF007AFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guía de Importación", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            listOf(
                "Formato requerido: .xlsx",
                "Columnas: id, nombre, correo, carreraId",
                "Evite celdas vacías en nombre y correo."
            ).forEachIndexed { index, text ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(modifier = Modifier.size(20.dp).background(Color.White.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                        Text((index + 1).toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text, color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun SubirArchivoProfesores(selectedFileUri: Uri?, onClick: () -> Unit) {
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    Box(
        modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) { drawRoundRect(color = Color(0xFFE0E0E0), style = stroke) }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Upload, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
            Text(selectedFileUri?.lastPathSegment ?: "Seleccionar archivo", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AvisoImportanciaProfesores() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.Info, null, tint = Color(0xFFFFA726))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Los registros existentes se actualizarán si el ID coincide.", color = Color(0xFFE65100), fontSize = 14.sp)
        }
    }
}
