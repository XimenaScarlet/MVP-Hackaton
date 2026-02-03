package com.example.univapp.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

data class Session(
    val userId: String,
    val email: String,
    val isAdmin: Boolean
)

class SessionManager(private val context: Context) {

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val IS_ADMIN_KEY = booleanPreferencesKey("is_admin")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        
        // Settings keys
        private val SHOW_EMAIL_KEY = booleanPreferencesKey("show_email")
        private val PUSH_NOTIFICATIONS_KEY = booleanPreferencesKey("push_notifications")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    suspend fun saveSession(userId: String, email: String, isAdmin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[EMAIL_KEY] = email
            preferences[IS_ADMIN_KEY] = isAdmin
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    suspend fun getSession(): Session? {
        val preferences = try { context.dataStore.data.first() } catch (e: Exception) { null }
        if (preferences == null) return null
        
        val isLoggedIn = preferences[IS_LOGGED_IN_KEY] ?: false
        if (!isLoggedIn) return null

        val userId = preferences[USER_ID_KEY]
        val email = preferences[EMAIL_KEY]
        val isAdmin = preferences[IS_ADMIN_KEY]

        return if (userId != null && email != null && isAdmin != null) {
            Session(userId, email, isAdmin)
        } else null
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    // Settings logic
    val showEmail: Flow<Boolean> = context.dataStore.data.map { it[SHOW_EMAIL_KEY] ?: true }
    suspend fun setShowEmail(show: Boolean) {
        context.dataStore.edit { it[SHOW_EMAIL_KEY] = show }
    }

    val pushNotifications: Flow<Boolean> = context.dataStore.data.map { it[PUSH_NOTIFICATIONS_KEY] ?: true }
    suspend fun setPushNotifications(enabled: Boolean) {
        context.dataStore.edit { it[PUSH_NOTIFICATIONS_KEY] = enabled }
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE_KEY] ?: false }
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }
}

fun hasInternet(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
