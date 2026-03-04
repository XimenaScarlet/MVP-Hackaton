package com.example.univapp.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.univapp.ui.StudentProceduresViewModel.DocumentRecord
import com.example.univapp.ui.StudentProceduresViewModel.RequestRecord
import org.json.JSONArray
import org.json.JSONObject

class LocalStore(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "univapp_local_store_encrypted"
        private const val REQUESTS_KEY = "requests_json"
        private const val DOCUMENTS_KEY = "documents_json"
    }

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        PREFS_NAME,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveRequests(requests: List<RequestRecord>) {
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
        with(sharedPreferences.edit()) {
            putString(REQUESTS_KEY, array.toString())
            apply()
        }
    }

    fun getRequests(): List<RequestRecord> {
        val json = sharedPreferences.getString(REQUESTS_KEY, "[]") ?: "[]"
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
        return list
    }

    fun saveDocuments(documents: List<DocumentRecord>) {
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
        with(sharedPreferences.edit()) {
            putString(DOCUMENTS_KEY, array.toString())
            apply()
        }
    }

    fun getDocuments(): List<DocumentRecord> {
        val json = sharedPreferences.getString(DOCUMENTS_KEY, "[]") ?: "[]"
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
        return list
    }
}
