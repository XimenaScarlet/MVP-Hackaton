package com.example.univapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.univapp.ui.SettingsViewModel
import com.example.univapp.ui.nav.AppNavHost
import com.example.univapp.ui.theme.UnivAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Iniciar Splash Screen antes de super.onCreate
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Habilitar diseño Edge-to-Edge para un look moderno
        enableEdgeToEdge()

        setContent {
            // Obtener el estado del modo oscuro de forma global
            val settingsVm: SettingsViewModel = hiltViewModel()
            val isDarkMode by settingsVm.darkMode.collectAsState()

            UnivAppTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
