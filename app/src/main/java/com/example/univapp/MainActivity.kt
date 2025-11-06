package com.example.univapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.univapp.ui.nav.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val nav = rememberNavController()      // <-- crear NavController

            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(nav = nav)          // <-- pasarlo a tu host
                }
            }
        }
    }
}
