@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.transporter

import android.Manifest
import android.os.Build
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
import kotlinx.coroutines.*
import java.util.concurrent.Executors

// Firestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAX_CAPACITY = 30

@Composable
fun TransporterScanScreen(
    routeId: String,
    busName: String,
    notifyPhoneNumber: String,
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    var permissionGranted by remember { mutableStateOf(false) }
    val reqCam = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionGranted = it
    }
    LaunchedEffect(Unit) { reqCam.launch(Manifest.permission.CAMERA) }

    val db = remember { Firebase.firestore }
    val tripId = remember(routeId) {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        "${today}_${routeId}"
    }

    // Estado desde Firestore en vivo
    val scanned = remember { mutableStateListOf<String>() }             // IDs escaneados
    val names = remember { mutableStateMapOf<String, String>() }        // nombre si existe
    var lastText by remember { mutableStateOf<String?>(null) }
    var showOk by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }

    // Suscripción en vivo
    LaunchedEffect(tripId) {
        db.collection("transportes").document(tripId).collection("scans")
            .addSnapshotListener { qs, _ ->
                scanned.clear()
                if (qs != null) {
                    for (d in qs.documents) {
                        val id = d.id
                        scanned.add(id)
                        if (!names.containsKey(id)) {
                            db.collection("alumnos").document(id).get()
                                .addOnSuccessListener { snap ->
                                    snap.getString("nombre")?.let { names[id] = it }
                                }
                        }
                    }
                }
            }
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
                        // Borra escaneos del viaje (seguro para pruebas)
                        val col = db.collection("transportes").document(tripId).collection("scans")
                        col.get().addOnSuccessListener { qs ->
                            val batch = db.batch()
                            qs.documents.forEach { batch.delete(it.reference) }
                            batch.commit()
                        }
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
                        // Guarda/actualiza resumen del viaje
                        val summary = mapOf(
                            "routeId" to routeId,
                            "bus" to busName,
                            "notifyPhone" to notifyPhoneNumber,
                            "count" to scanned.size,
                            "notifiedAt" to FieldValue.serverTimestamp()
                        )
                        db.collection("transportes").document(tripId)
                            .set(summary, SetOptions.merge())
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
                            CameraPreviewWithAnalyzer(
                                onBarcode = { text ->
                                    // Todas las mutaciones de estado en MAIN
                                    scope.launch(Dispatchers.Main) {
                                        val clean = text.trim()
                                        if (clean.isEmpty()) return@launch
                                        if (scanned.size >= MAX_CAPACITY) return@launch
                                        if (scanned.contains(clean)) return@launch

                                        lastText = clean
                                        showOk = true

                                        val data = mapOf(
                                            "studentId" to clean,
                                            "routeId" to routeId,
                                            "bus" to busName,
                                            "status" to "boarded",
                                            "scannedAt" to FieldValue.serverTimestamp(),
                                            "device" to Build.MODEL
                                        )
                                        // Guardar (seguro aunque se repita: misma key)
                                        Firebase.firestore.collection("transportes")
                                            .document(tripId)
                                            .collection("scans")
                                            .document(clean)
                                            .set(data, SetOptions.merge())
                                    }
                                }
                            )
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Se requiere permiso de cámara.")
                            }
                        }
                    }

                    if (showOk) {
                        LaunchedEffect(lastText) { delay(900); showOk = false }
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
                items(scanned, key = { it }) { code ->
                    val displayName = names[code]
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
                                val initials = (displayName ?: code)
                                    .split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
                                    .joinToString("")
                                Text(initials, color = Color(0xFF334155), fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(displayName ?: "Alumno", fontWeight = FontWeight.SemiBold)
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

/* ====== Cámara + ML Kit (robusto) ====== */
@Composable
private fun CameraPreviewWithAnalyzer(
    throttleMs: Long = 700,
    onBarcode: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }

    // Recursos que hay que cerrar
    val options = remember {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_DATA_MATRIX)
            .build()
    }
    val scanner = remember { BarcodeScanning.getClient(options) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    var isProcessing by remember { mutableStateOf(false) }
    var lastEmit by remember { mutableStateOf(0L) }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize()) { view ->
        runCatching {
            val provider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(view.surfaceProvider) }
            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            val analysis = ImageAnalysis.Builder()
                .setTargetResolution(android.util.Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(executor) { proxy ->
                val img = proxy.image
                if (img == null) {
                    proxy.close(); return@setAnalyzer
                }
                if (isProcessing) { proxy.close(); return@setAnalyzer }
                val now = System.currentTimeMillis()
                if (now - lastEmit < throttleMs) { proxy.close(); return@setAnalyzer }

                isProcessing = true
                val input = InputImage.fromMediaImage(img, proxy.imageInfo.rotationDegrees)
                scanner.process(input)
                    .addOnSuccessListener { list ->
                        if (list.isNotEmpty()) {
                            lastEmit = System.currentTimeMillis()
                            val raw = list.first().rawValue?.trim().orEmpty()
                            if (raw.isNotEmpty()) onBarcode(raw)
                        }
                    }
                    .addOnFailureListener {
                        // no-op: evitamos que un error tumbe la app
                    }
                    .addOnCompleteListener {
                        isProcessing = false
                        proxy.close()
                    }
            }

            provider.unbindAll()
            provider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
        }
    }

    // Limpieza de recursos al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            runCatching { cameraProviderFuture.get().unbindAll() }
            runCatching { scanner.close() }
            runCatching { executor.shutdown() }
        }
    }
}
