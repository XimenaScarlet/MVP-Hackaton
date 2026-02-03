package com.example.univapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase

class UnivAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Mover la configuración de persistencia aquí
        Firebase.firestore.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
    }
}
