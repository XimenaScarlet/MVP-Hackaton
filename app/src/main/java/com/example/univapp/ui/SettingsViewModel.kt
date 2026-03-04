package com.example.univapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.univapp.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    private val _showEmail = MutableStateFlow(sessionManager.showEmail)
    val showEmail: StateFlow<Boolean> = _showEmail.asStateFlow()

    private val _pushNotifications = MutableStateFlow(sessionManager.pushNotifications)
    val pushNotifications: StateFlow<Boolean> = _pushNotifications.asStateFlow()

    private val _darkMode = MutableStateFlow(sessionManager.darkMode)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    fun toggleShowEmail(value: Boolean) {
        sessionManager.setShowEmail(value)
        _showEmail.value = value
    }

    fun togglePushNotifications(value: Boolean) {
        sessionManager.setPushNotifications(value)
        _pushNotifications.value = value
    }

    fun toggleDarkMode(value: Boolean) {
        sessionManager.setDarkMode(value)
        _darkMode.value = value
    }

    fun clearCache(): Long {
        val context = getApplication<Application>()
        var deletedBytes = 0L
        try {
            deletedBytes = getFolderSize(context.cacheDir)
            deleteDir(context.cacheDir)
            
            context.externalCacheDir?.let {
                deletedBytes += getFolderSize(it)
                deleteDir(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return deletedBytes
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (i in children.indices) {
                    val success = deleteDir(File(dir, children[i]))
                    if (!success) return false
                }
            }
            return dir.delete()
        } else if (dir != null && dir.isFile) {
            return dir.delete()
        }
        return false
    }

    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            val files = file.listFiles()
            if (files != null) {
                for (f in files) {
                    if (f.isDirectory) {
                        size += getFolderSize(f)
                    } else {
                        size += f.length()
                    }
                }
            }
        }
        return size
    }
}
