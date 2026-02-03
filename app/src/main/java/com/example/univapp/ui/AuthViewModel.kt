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

enum class Portal { ALUMNO, ADMIN, TRANSPORTISTA }

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

    private val _portal = MutableStateFlow<Portal?>(null)
    val portal: StateFlow<Portal?> = _portal.asStateFlow()
    fun consumePortal() { _portal.value = null }

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
                    _offlineSession.value = null // Clear offline session when online
                }
            }
            .addOnFailureListener { _isAdmin.value = false }
    }

    private fun mapError(th: Throwable?): String {
        return when (th) {
            is FirebaseNetworkException -> "Sin conexión a internet. Verifica tu red e inténtalo de nuevo."
            is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta."
            is FirebaseAuthInvalidUserException -> "La matrícula no existe. Contacta a la administración."
            is FirebaseAuthException -> when (th.errorCode) {
                "ERROR_USER_NOT_FOUND" -> "La matrícula no existe. Contacta a la administración."
                "ERROR_WRONG_PASSWORD" -> "Contraseña incorrecta."
                "ERROR_INVALID_EMAIL" -> "El correo institucional es inválido."
                "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Intenta más tarde."
                "ERROR_NETWORK_REQUEST_FAILED" -> "Sin conexión a internet. Verifica tu red."
                else -> "Error de autenticación: ${th.errorCode}"
            }
            else -> th?.message ?: "Error desconocido. Inténtalo de nuevo."
        }
    }

    fun login(identifier: String, password: String, onError: (String?) -> Unit = {}) {
        val idTrim = identifier.trim()

        if ((idTrim.equals("transporte", true) || idTrim.equals("transportista", true))
            && password == "transporte"
        ) {
            _portal.value = Portal.TRANSPORTISTA
            return
        }

        val email = normalizeIdentifier(idTrim)
        _loading.value = true
        _error.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loading.value = false
                // AuthStateListener will handle the rest
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

        db.collection("alumnos").whereEqualTo("matricula", matricula).limit(1).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    _loading.value = false
                    val err = "La matrícula no existe. Contacta a la administración."
                    _error.value = err
                    callback(false, err)
                    return@addOnSuccessListener
                }

                val userDoc = documents.first()
                val email = userDoc.getString("correo")

                if (email.isNullOrBlank()) {
                    _loading.value = false
                    val err = "El usuario no tiene un correo asociado."
                    _error.value = err
                    callback(false, err)
                    return@addOnSuccessListener
                }

                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        _loading.value = false
                        if (task.isSuccessful) {
                            callback(true, "Te enviamos un correo para restablecer tu contraseña.")
                        } else {
                            val err = "No fue posible enviar el correo. Intenta más tarde."
                            _error.value = err
                            callback(false, err)
                        }
                    }
            }
            .addOnFailureListener { e ->
                _loading.value = false
                val err = "Error al consultar la base de datos: ${e.message}"
                _error.value = err
                callback(false, err)
            }
    }

    fun clearError() { _error.value = null }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _offlineSession.value = null
            auth.signOut()
        }
    }
}
