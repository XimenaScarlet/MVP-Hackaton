package com.example.univapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.univapp.ui.StudentProceduresViewModel.DocumentRecord
import com.example.univapp.ui.StudentProceduresViewModel.RequestRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

// Renombrado para evitar conflicto con SessionManager
private val Context.localDataStore by preferencesDataStore(name = "univapp_local_store")

class LocalStore(private val context: Context) {

    companion object {
        private val REQUESTS_KEY = stringPreferencesKey("requests_json")
        private val DOCUMENTS_KEY = stringPreferencesKey("documents_json")
    }

    suspend fun saveRequests(requests: List<RequestRecord>) {
        val array = JSONArray()
        requests.forEach { req ->
            val obj = JSONObject().apply {
                put("id", req.id)
                put("title", req.title)
                put("folio", req.folio)
                put("date", req.date)
                put("status", req.status)
                put("tipo", req.tipo)
                put("entrega", req.entrega)
                put("createdAtMillis", req.createdAtMillis)
            }
            array.put(obj)
        }
        context.localDataStore.edit { prefs ->
            prefs[REQUESTS_KEY] = array.toString()
        }
    }

    val requestsFlow: Flow<List<RequestRecord>> = context.localDataStore.data.map { prefs ->
        val json = prefs[REQUESTS_KEY] ?: "[]"
        val array = JSONArray(json)
        val list = mutableListOf<RequestRecord>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                RequestRecord(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    folio = obj.getString("folio"),
                    date = obj.getString("date"),
                    status = obj.getString("status"),
                    tipo = obj.getString("tipo"),
                    entrega = obj.getString("entrega"),
                    createdAtMillis = obj.optLong("createdAtMillis", 0L)
                )
            )
        }
        list
    }

    suspend fun saveDocuments(documents: List<DocumentRecord>) {
        val array = JSONArray()
        documents.forEach { doc ->
            val obj = JSONObject().apply {
                put("id", doc.id)
                put("title", doc.title)
                put("date", doc.date)
                put("folio", doc.folio)
                put("tipo", doc.tipo)
                put("fileName", doc.fileName)
                put("createdAtMillis", doc.createdAtMillis)
            }
            array.put(obj)
        }
        context.localDataStore.edit { prefs ->
            prefs[DOCUMENTS_KEY] = array.toString()
        }
    }

    val documentsFlow: Flow<List<DocumentRecord>> = context.localDataStore.data.map { prefs ->
        val json = prefs[DOCUMENTS_KEY] ?: "[]"
        val array = JSONArray(json)
        val list = mutableListOf<DocumentRecord>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                DocumentRecord(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    date = obj.getString("date"),
                    folio = obj.getString("folio"),
                    tipo = obj.getString("tipo"),
                    fileName = obj.getString("fileName"),
                    createdAtMillis = obj.optLong("createdAtMillis", 0L)
                )
            )
        }
        list
    }
}
