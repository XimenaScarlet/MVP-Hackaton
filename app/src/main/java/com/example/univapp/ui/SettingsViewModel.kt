package com.example.univapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    val showEmail: StateFlow<Boolean> = sessionManager.showEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val pushNotifications: StateFlow<Boolean> = sessionManager.pushNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val darkMode: StateFlow<Boolean> = sessionManager.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleShowEmail(value: Boolean) {
        viewModelScope.launch { sessionManager.setShowEmail(value) }
    }

    fun togglePushNotifications(value: Boolean) {
        viewModelScope.launch { sessionManager.setPushNotifications(value) }
    }

    fun toggleDarkMode(value: Boolean) {
        viewModelScope.launch { sessionManager.setDarkMode(value) }
    }

    fun clearCache(): Long {
        val context = getApplication<Application>()
        var deletedBytes = 0L
        try {
            deletedBytes = getFolderSize(context.cacheDir)
            deleteDir(context.cacheDir)
            
            // Also external cache
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
