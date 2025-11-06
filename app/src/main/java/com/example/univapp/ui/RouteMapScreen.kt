@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.univapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.univapp.location.LocationHelper
import com.example.univapp.ui.maps.EtaResult
import com.example.univapp.ui.maps.fetchEtaOsrm
import com.example.univapp.ui.maps.routeOnRoadsBetweenStops
import com.example.univapp.ui.routes.RouteSpec
import com.example.univapp.ui.routes.RoutesCatalog
import com.example.univapp.ui.routes.StopSpec
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/* ================= Utils ================= */

private val UTC = LatLng(25.558634, -100.938278)

private fun boundsOf(points: List<LatLng>): LatLngBounds? {
    if (points.isEmpty()) return null
    val b = LatLngBounds.Builder()
    points.forEach { b.include(it) }
    return try { b.build() } catch (_: Exception) { null }
}

private fun RouteSpec.allPoints(path: List<LatLng>?): List<LatLng> {
    val poly = path ?: pathHint
    val markers = stops.mapNotNull { it.pos }
    return poly + markers
}

/* ================= Screen ================= */

@Composable
fun RouteMapScreen(
    routeId: String,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val routes = remember { RoutesCatalog() }

    // cámara
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(UTC, 12.5f)
    }

    // ubicación (si ya hay permiso)
    val locHelper = remember { LocationHelper(ctx) }
    var myLoc by remember { mutableStateOf<LatLng?>(null) }
    LaunchedEffect(Unit) { if (locHelper.hasPermission()) myLoc = locHelper.getCurrentLocation() }

    // polylines on-road cache
    val roadCache = remember { mutableStateMapOf<String, List<LatLng>>() }
    LaunchedEffect(routes) {
        routes.forEach { r ->
            val onRoad = withTimeoutOrNull(20_000) {
                routeOnRoadsBetweenStops(
                    apiKey = ctx.packageManager.getApplicationInfo(ctx.packageName, 128)
                        .metaData?.getString("com.google.android.geo.API_KEY") ?: "",
                    stops = r.stops
                )
            }.orEmpty()
            if (onRoad.size >= 2) roadCache[r.id] = onRoad
        }
    }

    // selección
    var selectedIndex by remember { mutableStateOf(-1) }   // -1 = Todas
    var selectedStop by remember { mutableStateOf<StopSpec?>(null) }
    var eta by remember { mutableStateOf<EtaResult?>(null) }
    var etaLoading by remember { mutableStateOf(false) }

    // al cambiar selección, enfoca
    LaunchedEffect(selectedIndex, roadCache) {
        val indices = if (selectedIndex == -1) routes.indices.toList() else listOf(selectedIndex)
        val allPoints = indices.flatMap { i -> routes[i].allPoints(roadCache[routes[i].id]) }
        boundsOf(allPoints)?.let { b ->
            cameraState.animate(CameraUpdateFactory.newLatLngBounds(b, 80))
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        if (selectedIndex == -1) "Rutas • Todas"
                        else routes.getOrNull(selectedIndex)?.title ?: "Ruta"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { pv ->
        Box(Modifier.fillMaxSize().padding(pv)) {

            /* ====== Mapa ====== */
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                properties = MapProperties(isMyLocationEnabled = myLoc != null),
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                val indices = if (selectedIndex == -1) routes.indices.toList() else listOf(selectedIndex)
                indices.forEach { idx ->
                    val r = routes[idx]
                    val path = roadCache[r.id].takeIf { !it.isNullOrEmpty() } ?: r.pathHint

                    // polyline de la ruta
                    Polyline(
                        points = path,
                        color = r.color,
                        width = if (idx == selectedIndex) 14f else 10f,
                        zIndex = if (idx == selectedIndex) 12f else 8f,
                        geodesic = false,
                        startCap = RoundCap(),
                        endCap = RoundCap(),
                        jointType = JointType.ROUND,
                        clickable = true,
                        onClick = { selectedIndex = idx }
                    )

                    // paradas
                    val showAllStops = selectedIndex != -1
                    r.stops.forEachIndexed { i, s ->
                        s.pos?.let { p ->
                            if (showAllStops || i == 0 || i == r.stops.lastIndex) {
                                Marker(
                                    state = MarkerState(p),
                                    title = s.name,
                                    onClick = {
                                        selectedStop = s
                                        myLoc?.let { me ->
                                            etaLoading = true
                                            scope.launch {
                                                eta = withTimeoutOrNull(10_000) { fetchEtaOsrm(me, p) }
                                                etaLoading = false
                                            }
                                        }
                                        true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            /* ====== ETA bubble ====== */
            AnimatedVisibility(
                visible = selectedStop != null && (etaLoading || eta != null),
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    tonalElevation = 4.dp
                ) {
                    Row(
                        Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedStop?.name ?: "")
                        Spacer(Modifier.width(8.dp))
                        when {
                            etaLoading -> Text("Calculando…")
                            eta != null -> {
                                val mins = kotlin.math.max(1, (eta!!.durationSeconds / 60.0).toInt())
                                val km = eta!!.distanceMeters / 1000.0
                                Text("• $mins min • ${"%.1f".format(km)} km", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            /* ====== Barra inferior con carrusel (scroll horizontal) ====== */
            RoutesCarouselBar(
                routes = routes,
                selectedIndex = selectedIndex,
                onSelect = { selectedIndex = it },
                modifier = Modifier.align(Alignment.BottomCenter)   // <-- aquí va el align
            )
        }
    }
}

/* ---------- Bottom carousel ---------- */

@Composable
private fun RoutesCarouselBar(
    routes: List<RouteSpec>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFFDFDFE),
        tonalElevation = 8.dp,
        shadowElevation = 10.dp,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        modifier = modifier.fillMaxWidth()  // <-- usamos el modifier recibido
    ) {
        Column(
            Modifier
                .navigationBarsPadding()
                .padding(vertical = 10.dp)
        ) {
            Text(
                "Rutas",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF475569),
                modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    RouteChipCard(
                        label = "Todas",
                        selected = selectedIndex == -1,
                        color = Color(0xFF0EA5E9),
                        onClick = { onSelect(-1) }
                    )
                }
                itemsIndexed(routes) { i, r ->
                    RouteChipCard(
                        label = r.id,
                        selected = selectedIndex == i,
                        color = r.color,
                        onClick = { onSelect(i) }
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun RouteChipCard(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val bg = if (selected) color.copy(alpha = 0.18f) else Color(0xFFF2F4F7)
    val fg = if (selected) color else Color(0xFF334155)

    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = bg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (selected) 6.dp else 2.dp),
        modifier = Modifier
            .height(64.dp)
            .widthIn(min = 98.dp)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.DirectionsBus, contentDescription = null, tint = fg)
            Spacer(Modifier.width(8.dp))
            Text(label, color = fg, fontWeight = FontWeight.SemiBold)
        }
    }
}
