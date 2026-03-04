package com.example.univapp.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

data class Session(
    val userId: String,
    val email: String,
    val isAdmin: Boolean
)

class SessionManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "session_prefs"
        private const val USER_ID_KEY = "user_id"
        private const val EMAIL_KEY = "email"
        private const val IS_ADMIN_KEY = "is_admin"
        private const val IS_LOGGED_IN_KEY = "is_logged_in"

        // Settings keys
        private const val SHOW_EMAIL_KEY = "show_email"
        private const val PUSH_NOTIFICATIONS_KEY = "push_notifications"
        private const val DARK_MODE_KEY = "dark_mode"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSession(userId: String, email: String, isAdmin: Boolean) {
        with(sharedPreferences.edit()) {
            putString(USER_ID_KEY, userId)
            putString(EMAIL_KEY, email)
            putBoolean(IS_ADMIN_KEY, isAdmin)
            putBoolean(IS_LOGGED_IN_KEY, true)
            apply()
        }
    }

    fun getSession(): Session? {
        val isLoggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false)
        if (!isLoggedIn) return null

        val userId = sharedPreferences.getString(USER_ID_KEY, null)
        val email = sharedPreferences.getString(EMAIL_KEY, null)
        val isAdmin = sharedPreferences.getBoolean(IS_ADMIN_KEY, false)

        return if (userId != null && email != null) {
            Session(userId, email, isAdmin)
        } else null
    }

    fun clearSession() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    // Settings logic
    val showEmail: Boolean
        get() = sharedPreferences.getBoolean(SHOW_EMAIL_KEY, true)

    fun setShowEmail(show: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(SHOW_EMAIL_KEY, show)
            apply()
        }
    }

    val pushNotifications: Boolean
        get() = sharedPreferences.getBoolean(PUSH_NOTIFICATIONS_KEY, true)

    fun setPushNotifications(enabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(PUSH_NOTIFICATIONS_KEY, enabled)
            apply()
        }
    }

    val darkMode: Boolean
        get() = sharedPreferences.getBoolean(DARK_MODE_KEY, false)

    fun setDarkMode(enabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(DARK_MODE_KEY, enabled)
            apply()
        }
    }
}

fun hasInternet(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
