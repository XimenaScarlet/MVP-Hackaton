package com.example.univapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.univapp.data.Session
import com.example.univapp.data.SessionManager
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val sessionManager = SessionManager(application.applicationContext)

    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _offlineSession = MutableStateFlow<Session?>(null)
    val offlineSession: StateFlow<Session?> = _offlineSession.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isAdmin = MutableStateFlow<Boolean?>(null)
    val isAdmin: StateFlow<Boolean?> = _isAdmin.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { fa ->
        _user.value = fa.currentUser
        if (fa.currentUser != null) {
            refreshAdminFlag()
        } else {
             viewModelScope.launch {
                sessionManager.clearSession()
                _offlineSession.value = null
                 _isAdmin.value = null
            }
        }
    }

    init {
        auth.addAuthStateListener(authListener)
        viewModelScope.launch {
            val session = sessionManager.getSession()
            if (session != null && auth.currentUser == null) {
                _offlineSession.value = session
                _isAdmin.value = session.isAdmin
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }

    private fun normalizeIdentifier(idOrEmail: String): String {
        val id = idOrEmail.trim()
        return if (id.contains("@")) id else "$id@alumno.utc.edu.mx"
    }

    private fun refreshAdminFlag() {
        val u = auth.currentUser ?: run {
            _isAdmin.value = null
            return
        }
        _isAdmin.value = null
        u.getIdToken(true)
            .addOnSuccessListener { res ->
                val claims = res.claims
                val admin =
                    (claims["admin"] as? Boolean == true) ||
                            ((claims["role"] as? String)?.equals("admin", true) == true)

                _isAdmin.value = admin
                viewModelScope.launch {
                    sessionManager.saveSession(u.uid, u.email ?: "", admin)
                    _offlineSession.value = null
                }
            }
            .addOnFailureListener { _isAdmin.value = false }
    }

    private fun mapError(th: Throwable?): String {
        return when (th) {
            is FirebaseNetworkException -> "Sin conexión a internet."
            is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta."
            is FirebaseAuthInvalidUserException -> "La matrícula no existe."
            else -> "Error de autenticación."
        }
    }

    fun login(identifier: String, password: String, onError: (String?) -> Unit = {}) {
        val email = normalizeIdentifier(identifier)
        _loading.value = true
        _error.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loading.value = false
            }
            .addOnFailureListener { e ->
                _loading.value = false
                val msg = mapError(e)
                _error.value = msg
                onError(msg)
            }
    }

    fun sendPasswordResetLink(matricula: String, callback: (ok: Boolean, msg: String?) -> Unit) {
        _loading.value = true
        _error.value = null

        db.collection("alumnos").document(matricula).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    _loading.value = false
                    callback(false, "La matrícula no existe.")
                    return@addOnSuccessListener
                }

                val email = doc.getString("correo") ?: normalizeIdentifier(matricula)
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        _loading.value = false
                        if (task.isSuccessful) {
                            callback(true, "Te enviamos un correo para restablecer tu contraseña.")
                        } else {
                            callback(false, "No se pudo enviar el correo.")
                        }
                    }
            }
            .addOnFailureListener { e ->
                _loading.value = false
                callback(false, "Error: ${e.message}")
            }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _offlineSession.value = null
            auth.signOut()
        }
    }

    fun clearError() { _error.value = null }
}
