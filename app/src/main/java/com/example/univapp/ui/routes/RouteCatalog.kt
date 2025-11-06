package com.example.univapp.ui.routes

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng

/* =================== Modelos =================== */
data class StopSpec(
    val name: String,
    val pos: LatLng?,                  // deja null si alguna no tiene coord exacta aún
    val notes: String? = null,
    val tags: List<String> = emptyList()
)

enum class Shift { MANANA, TARDE }

data class RouteSpec(
    val id: String,            // ej. "R5"
    val title: String,         // nombre visible
    val color: Color,          // color del trazo en el mapa
    val pathHint: List<LatLng>,// polyline guía (si no hay Directions)
    val stops: List<StopSpec>, // paradas en orden
    val departuresMorning: List<String> = listOf("06:30","07:00","07:30","09:20"),
    val departuresEvening: List<String> = listOf("13:30","15:00","17:30","19:20"),
    val supportsMorning: Boolean = true,
    val supportsEvening: Boolean = true
) {
    fun departures(shift: Shift): List<String> =
        if (shift == Shift.MANANA) departuresMorning else departuresEvening
}

/* === Coordenada oficial de la UTC === */
private val UTC = LatLng(25.558634, -100.938278)

/* Utilidad: genera una polyline aproximada desde las paradas con coordenadas */
private fun hintFromStops(stops: List<StopSpec>) =
    stops.mapNotNull { it.pos }.ifEmpty { listOf(UTC) }

/* =================== Catálogo de rutas (R5..R11) =================== */
@Suppress("FunctionName")
fun RoutesCatalog(): List<RouteSpec> {

    /* ---------- R5 – Santa Elena ---------- */
    val r5Stops = listOf(
        StopSpec("UTC / Zona Industrial (frente a DM Control)", UTC),
        StopSpec("Blvd. Nazario Ortiz Garza esquina Periférico L.E.A.", LatLng(25.4444, -100.9728)),
        StopSpec("Periférico L.E.A. esquina Carretera Saltillo–Zacatecas", LatLng(25.4205, -100.9581)),
        StopSpec("Entrada a colonia Las Teresitas", LatLng(25.3812, -100.9449)),
        StopSpec("Entrada a colonia Huachichiles", LatLng(25.3791, -100.9403)),
        StopSpec("Calle Teodoro Sánchez (zona de talleres y OXXO)", LatLng(25.3708, -100.9361)),
        StopSpec("Calle Santa Elena (plaza o iglesia principal)", LatLng(25.3659, -100.9348))
    )

    /* ---------- R6 – Urdiñola – Calzada Narro ---------- */
    val r6Stops = listOf(
        StopSpec("UTC / Zona Industrial", UTC),
        StopSpec("Blvd. Nazario Ortiz Garza esquina Periférico L.E.A.", LatLng(25.4444, -100.9728)),
        StopSpec("Paseo de la Reforma esquina Periférico L.E.A.", LatLng(25.4312, -100.9760)),
        StopSpec("Calle Urdíñola (frente a Parque Las Maravillas)", LatLng(25.4268, -100.9885)),
        StopSpec("Calle Arizpe de la Masa esquina Mimbre", LatLng(25.4201, -100.9852)),
        StopSpec("Calle Ana Paula esquina Calzada Narro", LatLng(25.4166, -100.9820)),
        StopSpec("Calzada Narro (frente a Universidad Carolina o Parque)", LatLng(25.4143, -100.9802))
    )

    /* ---------- R7 – Guerrero – Lomas Verdes ---------- */
    val r7Stops = listOf(
        StopSpec("UTC / Zona Industrial", UTC),
        StopSpec("Blvd. Nazario Ortiz Garza esquina L.E.A.", LatLng(25.4444, -100.9728)),
        StopSpec("Calle Guerrero esquina Benito Juárez", LatLng(25.4251, -100.9930)),
        StopSpec("Benito Juárez esquina Miguel Hidalgo", LatLng(25.4275, -100.9970)),
        StopSpec("Miguel Hidalgo esquina Libertad", LatLng(25.4296, -101.0004)),
        StopSpec("Libertad esquina Sierra de las Casitas", LatLng(25.4395, -101.0030)),
        StopSpec("Paseo de los Osos (entrada principal a Valle Dorado)", LatLng(25.4438, -101.0068)),
        StopSpec("Lomas Verdes (zona alta)", LatLng(25.4468, -101.0106))
    )

    /* ---------- R8 – Fidel Velázquez – Otilio ---------- */
    val r8Stops = listOf(
        StopSpec("UTC / Salida sobre Fidel Velázquez", UTC),
        StopSpec("Fidel Velázquez esquina Néstor Chaires", LatLng(25.4441, -100.9477)),
        StopSpec("Plan de Guadalupe esquina Vicente Guerrero", LatLng(25.4430, -100.9545)),
        StopSpec("Nazario Ortiz esquina Benito Juárez", LatLng(25.4444, -100.9728)),
        StopSpec("Aquiles Serdán esquina Francisco Villa", LatLng(25.4330, -100.9516)),
        StopSpec("Francisco Villa esquina San José (plaza o cancha)", LatLng(25.4304, -100.9496))
    )

    /* ---------- R9 – Arteaga – Bonanza ---------- */
    val r9Stops = listOf(
        StopSpec("UTC / Zona Industrial", UTC),
        StopSpec("Blvd. Valdez Sánchez esquina Vicente Guerrero", LatLng(25.4412, -100.9635)),
        StopSpec("Blvd. Valdez Sánchez esquina Acueducto", LatLng(25.4392, -100.9540)),
        StopSpec("Calle Minero esquina Acueducto", LatLng(25.4379, -100.9485)),
        StopSpec("Calle Minero esquina Getsemaní (“a Torreón”)", LatLng(25.4362, -100.9440)),
        StopSpec("Entrada al Fraccionamiento Bonanza (puerta principal)", LatLng(25.4315, -100.9328))
    )

    /* ---------- R10 – Loma Linda – Mirasierra ---------- */
    val r10Stops = listOf(
        StopSpec("UTC / Zona Industrial", UTC),
        StopSpec("Fundadores esquina Revolución (cerca de OXXO)", LatLng(25.4392, -100.9710)),
        StopSpec("Revolución esquina Ensenada", LatLng(25.4364, -100.9665)),
        StopSpec("Ensenada esquina Cedros", LatLng(25.4331, -100.9541)),
        StopSpec("Cedros esquina Mezquite", LatLng(25.4321, -100.9523)),
        StopSpec("Otilio González esquina Mezquite", LatLng(25.4320, -100.9460)),
        StopSpec("Entrada principal de Mirasierra (Cedros y Mezquite)", LatLng(25.4315, -100.9515))
    )

    /* ---------- R11 – Central – Valle Verde ---------- */
    val r11Stops = listOf(
        StopSpec("UTC / Zona Industrial", UTC),
        StopSpec("Blvd. Colosio esquina Carretera Monterrey–Saltillo", LatLng(25.5401, -100.9512)),
        StopSpec("Emilio Carranza esquina Isidro López Zertuche", LatLng(25.4337, -100.9883)),
        StopSpec("Calle Ramos Arizpe esquina Mariano Salas", LatLng(25.4263, -100.9919)),
        StopSpec("Felipe Berriozábal esquina Australia", LatLng(25.4239, -100.9902)),
        StopSpec("Calzada Narro esquina Valle Verde (frente al parque)", LatLng(25.4184, -100.9870))
    )

    return listOf(
        RouteSpec("R5","Ruta 5 – Santa Elena", Color(0xFF0EA5E9), hintFromStops(r5Stops), r5Stops),
        RouteSpec("R6","Ruta 6 – Urdiñola – Calzada Narro", Color(0xFF22C55E), hintFromStops(r6Stops), r6Stops),
        RouteSpec("R7","Ruta 7 – Guerrero – Lomas Verdes", Color(0xFFF59E0B), hintFromStops(r7Stops), r7Stops),
        RouteSpec("R8","Ruta 8 – Fidel Velázquez – Otilio", Color(0xFFE11D48), hintFromStops(r8Stops), r8Stops),
        RouteSpec("R9","Ruta 9 – Arteaga – Bonanza", Color(0xFF6366F1), hintFromStops(r9Stops), r9Stops),
        RouteSpec("R10","Ruta 10 – Loma Linda – Mirasierra", Color(0xFF06B6D4), hintFromStops(r10Stops), r10Stops),
        RouteSpec("R11","Ruta 11 – Central – Valle Verde", Color(0xFF84CC16), hintFromStops(r11Stops), r11Stops)
    )
}
