package com.example.univapp.ui.maps

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Guarda/carga un GeoJSON con las rutas “on-roads”.
 * Archivo: filesDir/routes_onroads.geojson
 */
object RoutesOnRoadsCache {

    private const val FILE_NAME = "routes_onroads.geojson"

    fun loadGeoJson(context: Context): Map<String, List<LatLng>>? {
        val f = File(context.filesDir, FILE_NAME)
        if (!f.exists()) return null
        val text = f.readText()
        val json = JSONObject(text)
        if (json.optString("type") != "FeatureCollection") return null
        val out = LinkedHashMap<String, List<LatLng>>()
        val features = json.getJSONArray("features")
        for (i in 0 until features.length()) {
            val feat = features.getJSONObject(i)
            val id = feat.optString("id")
            val geom = feat.getJSONObject("geometry")
            if (geom.optString("type") != "LineString") continue
            val coords = geom.getJSONArray("coordinates")
            val list = ArrayList<LatLng>(coords.length())
            for (j in 0 until coords.length()) {
                val arr = coords.getJSONArray(j)
                val lon = arr.getDouble(0)
                val lat = arr.getDouble(1)
                list.add(LatLng(lat, lon))
            }
            out[id] = list
        }
        return out
    }

    fun saveGeoJson(context: Context, paths: Map<String, List<LatLng>>) {
        val features = JSONArray()
        paths.forEach { (id, pts) ->
            val coords = JSONArray()
            pts.forEach { p -> coords.put(JSONArray().put(p.longitude).put(p.latitude)) }
            val geom = JSONObject().put("type", "LineString").put("coordinates", coords)
            val feat = JSONObject()
                .put("type", "Feature")
                .put("id", id)
                .put("properties", JSONObject().put("routeId", id))
                .put("geometry", geom)
            features.put(feat)
        }
        val fc = JSONObject().put("type", "FeatureCollection").put("features", features)
        File(context.filesDir, FILE_NAME).writeText(fc.toString())
    }
}
