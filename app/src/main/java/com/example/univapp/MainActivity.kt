package com.example.univapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.univapp.ui.nav.AppNavHost
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Persistencia offline de Firestore (una vez)
        Firebase.firestore.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }

        setContent {
            val nav = rememberNavController()
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(nav = nav)
                }
            }
        }
    }
}
