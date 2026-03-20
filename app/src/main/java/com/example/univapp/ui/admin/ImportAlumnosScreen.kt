package com.example.univapp.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.ui.util.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportAlumnosScreen(
    onBack: () -> Unit,
    vm: AdminImportAlumnosViewModel = viewModel()
) {
    val state by vm.importState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            vm.importAlumnosFromUri(it, inputStream)
        }
    }

    AppScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Importar Alumnos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val s = state) {
                is ImportState.Idle -> {
                    Icon(Icons.Default.Description, null, modifier = Modifier.size(80.dp), tint = Color.Gray)
                    Spacer(Modifier.height(16.dp))
                    Text("Selecciona un archivo Excel (.xlsx)", textAlign = TextAlign.Center)
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = { launcher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Subir Excel")
                    }
                }
                is ImportState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Procesando datos y creando cuentas...")
                }
                is ImportState.Success -> {
                    val res = s.result
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    Text("¡Importación Exitosa!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    
                    Card(
                        modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Resumen:", fontWeight = FontWeight.Bold)
                            Text("• Total procesados: ${res.totalProcessed}")
                            Text("• Cuentas creadas: ${res.createdInAuth}")
                            Text("• Usuarios existentes: ${res.existingInAuth}")
                        }
                    }

                    if (res.rowErrors.isNotEmpty()) {
                        Text("Avisos/Errores (${res.rowErrors.size}):", color = Color.Red, modifier = Modifier.fillMaxWidth())
                        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            items(res.rowErrors) { err ->
                                Text("- $err", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                    
                    Button(onClick = { vm.resetState() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Importar otro archivo")
                    }
                }
                is ImportState.Error -> {
                    Text("Error: ${s.message}", color = Color.Red, textAlign = TextAlign.Center)
                    Button(onClick = { vm.resetState() }, Modifier.padding(top = 16.dp)) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}
