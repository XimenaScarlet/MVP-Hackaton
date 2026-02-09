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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportMateriasScreen(
    onBack: () -> Unit,
    viewModel: AdminImportMateriasViewModel = viewModel()
) {
    val importState by viewModel.importState.collectAsState()
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedFileUri = uri
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Importar Materias", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F6F8),
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GuiaDeImportacionMaterias()
            Spacer(modifier = Modifier.height(24.dp))
            SubirArchivoMaterias(selectedFileUri) {
                launcher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            }
            Spacer(modifier = Modifier.height(16.dp))
            ImportanteWarningMaterias()
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    selectedFileUri?.let {
                        val inputStream = context.contentResolver.openInputStream(it)
                        viewModel.importMateriasFromUri(it, inputStream)
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
                text = { Text("${state.count} materias han sido importadas correctamente.") },
                confirmButton = {
                    Button(onClick = { viewModel.resetState(); onBack() }) {
                        Text("Aceptar")
                    }
                }
            )
        }
        is ImportState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                title = { Text("Error en la Importación") },
                text = { Text(state.message) },
                confirmButton = {
                    Button(onClick = { viewModel.resetState() }) {
                        Text("Aceptar")
                    }
                }
            )
        }
        else -> {}
    }
}

@Composable
private fun GuiaDeImportacionMaterias() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF007AFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Guía de Importación", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Sigue estos pasos cuidadosamente", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            listOf(
                "El archivo debe estar obligatoriamente en formato .xlsx",
                "La primera fila debe contener los encabezados: Clave, Nombre, Créditos, etc.",
                "Asegúrese de que las claves de materia sean únicas."
            ).forEachIndexed { index, text ->
                Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier.size(20.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
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
private fun SubirArchivoMaterias(selectedFileUri: Uri?, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Subir Archivo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("REQUERIDO", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)).clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.fillMaxSize()){ drawRoundRect(color = Color(0xFFE0E0E0), style = stroke) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Upload, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                Text(selectedFileUri?.lastPathSegment ?: "Seleccionar archivo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Máximo 10MB (.xlsx)", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ImportanteWarningMaterias() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), border = BorderStroke(1.dp, Color(0xFFFFE0B2)), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFFA726))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("IMPORTANTE", color = Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Se actualizarán las materias que ya existan con la misma clave.", color = Color(0xFFE65100), fontSize = 14.sp)
            }
        }
    }
}
