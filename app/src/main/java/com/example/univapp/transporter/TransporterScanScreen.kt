@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.transporter

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

private const val MAX_CAPACITY = 30

@Composable
fun TransporterScanScreen(
    routeId: String,
    busName: String,
    notifyPhoneNumber: String,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    val reqCam = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionGranted = it
    }
    LaunchedEffect(Unit) { reqCam.launch(Manifest.permission.CAMERA) }

    val sampleNames = remember {
        mapOf(
            "22040020" to "Ana López",
            "22040021" to "Juan Torres",
            "22040022" to "María García",
            "22040023" to "Luis Hernández",
            "22040024" to "Paola Martínez"
        )
    }

    val scanned = remember { mutableStateListOf<String>() }
    var lastText by remember { mutableStateOf<String?>(null) }
    var showOk by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) } // popup de confirmación

    // precargar 5 usuarios
    LaunchedEffect(Unit) {
        if (scanned.isEmpty()) scanned.addAll(sampleNames.keys.take(5))
    }

    val headerGrad = Brush.horizontalGradient(
        colors = listOf(Color(0xFF6C63FF), Color(0xFF8A85FF))
    )

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.QrCodeScanner, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Scan QR Code", fontWeight = FontWeight.SemiBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    AssistChip(onClick = {}, label = { Text("Ruta $routeId") })
                    Spacer(Modifier.width(8.dp))
                    AssistChip(onClick = {}, label = { Text("${scanned.size}/$MAX_CAPACITY") })
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = {
                        scanned.clear(); lastText = null
                    }) {
                        Icon(Icons.Outlined.RestartAlt, contentDescription = "Reiniciar conteo")
                    }
                }
            )
        }
    ) { pv ->
        Column(Modifier.fillMaxSize().padding(pv)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = (scanned.size / MAX_CAPACITY.toFloat()).coerceIn(0f, 1f),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = {
                        // popup de "enviado"
                        showPopup = true
                    },
                    enabled = scanned.isNotEmpty()
                ) { Text("Avisar") }
            }

            Surface(
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Box(Modifier.fillMaxSize()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .background(headerGrad)
                            .align(Alignment.TopCenter)
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F7FB))
                    ) {
                        if (permissionGranted) {
                            CameraPreviewWithAnalyzer { text ->
                                val clean = text.trim()
                                if (clean.isNotEmpty()
                                    && !scanned.contains(clean)
                                    && scanned.size < MAX_CAPACITY
                                ) {
                                    scanned.add(clean)
                                    lastText = clean
                                    showOk = true
                                }
                            }
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Se requiere permiso de cámara.")
                            }
                        }
                    }

                    if (showOk) {
                        LaunchedEffect(lastText) { delay(1000); showOk = false }
                        Surface(
                            color = Color.White,
                            shadowElevation = 8.dp,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A))
                                Spacer(Modifier.width(8.dp))
                                Text("Leído: ${lastText ?: ""}")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                "Escaneados (${scanned.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scanned) { code ->
                    val name = sampleNames[code] ?: "Alumno"
                    ElevatedCard(shape = RoundedCornerShape(14.dp)) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE9ECF5)),
                                contentAlignment = Alignment.Center
                            ) {
                                val initials = name.split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
                                    .joinToString("")
                                Text(initials, color = Color(0xFF334155), fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(name, fontWeight = FontWeight.SemiBold)
                                Text(code, color = Color(0xFF64748B), style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            confirmButton = {
                TextButton(onClick = { showPopup = false }) { Text("OK") }
            },
            title = { Text("Mensaje enviado") },
            text = { Text("Se envió el mensaje de WhatsApp al encargado de la ruta.") },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/* ====== Cámara + ML Kit ====== */
@Composable
private fun CameraPreviewWithAnalyzer(onBarcode: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }

    val options = remember {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_DATA_MATRIX
            )
            .build()
    }
    val scanner = remember { BarcodeScanning.getClient(options) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize()) { view ->
        val provider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(view.surfaceProvider) }
        val selector = CameraSelector.DEFAULT_BACK_CAMERA
        val analysis = ImageAnalysis.Builder()
            .setTargetResolution(android.util.Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        var lastEmit = 0L
        analysis.setAnalyzer(executor) { proxy ->
            val img = proxy.image
            if (img != null) {
                val input = InputImage.fromMediaImage(img, proxy.imageInfo.rotationDegrees)
                scanner.process(input)
                    .addOnSuccessListener { list ->
                        val now = System.currentTimeMillis()
                        if (list.isNotEmpty() && now - lastEmit > 700) {
                            lastEmit = now
                            val raw = list.first().rawValue?.trim().orEmpty()
                            if (raw.isNotEmpty()) onBarcode(raw)
                        }
                    }
                    .addOnCompleteListener { proxy.close() }
            } else proxy.close()
        }

        provider.unbindAll()
        provider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
    }
}
