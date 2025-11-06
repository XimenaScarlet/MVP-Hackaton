package com.example.univapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Portal { ALUMNO, ADMIN, TRANSPORTISTA }

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // null = aún consultando claims; true/false = determinado
    private val _isAdmin = MutableStateFlow<Boolean?>(null)
    val isAdmin: StateFlow<Boolean?> = _isAdmin.asStateFlow()

    // 🔔 Señal de navegación por rol (se consume tras navegar)
    private val _portal = MutableStateFlow<Portal?>(null)
    val portal: StateFlow<Portal?> = _portal.asStateFlow()
    fun consumePortal() { _portal.value = null }

    private val authListener = FirebaseAuth.AuthStateListener { fa ->
        _user.value = fa.currentUser
        refreshAdminFlag()
    }

    init {
        auth.addAuthStateListener(authListener)
        refreshAdminFlag()
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }

    /** matrícula -> correo institucional si no trae '@' */
    private fun normalizeIdentifier(idOrEmail: String): String {
        val id = idOrEmail.trim()
        return if (id.contains("@")) id else "$id@alumno.utc.edu.mx"
    }

    /** Lee custom claims para saber si es admin */
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
            }
            .addOnFailureListener { _isAdmin.value = false }
    }

    /** Traduce excepciones de Firebase a mensajes claros en español */
    private fun mapError(th: Throwable?): String { /* ← igual que tu versión */
        when (th) {
            is FirebaseNetworkException -> return "Sin conexión a internet. Verifica tu red e inténtalo de nuevo."
            is FirebaseAuthInvalidCredentialsException -> {
                val code = (th as? FirebaseAuthException)?.errorCode ?: ""
                val msg = th.message?.lowercase().orEmpty() + " " + th.toString().lowercase()
                return when {
                    code.equals("ERROR_WRONG_PASSWORD", true) ||
                            "wrong password" in msg || "password is invalid" in msg ||
                            "contraseña" in msg -> "La contraseña es incorrecta."
                    code.equals("ERROR_INVALID_EMAIL", true) ||
                            "invalid email" in msg -> "El correo institucional es inválido."
                    else -> "Las credenciales de autenticación son inválidas o han expirado."
                }
            }
            is FirebaseAuthInvalidUserException -> {
                val code = th.errorCode ?: ""
                return if (code.equals("ERROR_USER_DISABLED", true))
                    "La cuenta está deshabilitada. Contacta al administrador."
                else "Matrícula o correo no registrados."
            }
            is FirebaseAuthException -> {
                val code = th.errorCode ?: ""
                return when {
                    code.equals("ERROR_USER_NOT_FOUND", true) -> "Matrícula o correo no registrados."
                    code.equals("ERROR_WRONG_PASSWORD", true) -> "La contraseña es incorrecta."
                    code.equals("ERROR_INVALID_EMAIL", true) -> "El correo institucional es inválido."
                    code.equals("ERROR_TOO_MANY_REQUESTS", true) -> "Demasiados intentos. Intenta más tarde."
                    code.equals("ERROR_NETWORK_REQUEST_FAILED", true) -> "Sin conexión a internet. Verifica tu red."
                    else -> "Error de autenticación: $code"
                }
            }
        }
        val raw = th?.message.orEmpty().lowercase()
        return when {
            "network" in raw || "internet" in raw -> "Sin conexión a internet. Verifica tu red."
            "wrong password" in raw || "password is invalid" in raw || "contraseña" in raw -> "La contraseña es incorrecta."
            "user not found" in raw || "no user record" in raw -> "Matrícula o correo no registrados."
            "invalid email" in raw -> "El correo institucional es inválido."
            else -> th?.message ?: "Error desconocido. Inténtalo de nuevo."
        }
    }

    /** Login: bypass transportista o Firebase */
    fun login(identifier: String, password: String, onError: (String?) -> Unit = {}) {
        val idTrim = identifier.trim()

        // ✅ BYPASS TRANSPORTISTA (no está en BD)
        if ((idTrim.equals("transporte", true) || idTrim.equals("transportista", true))
            && password == "transporte"
        ) {
            // No tocamos Firebase; navegamos directo.
            _portal.value = Portal.TRANSPORTISTA
            return
        }

        // Firebase (alumno/admin)
        val email = normalizeIdentifier(idTrim)
        _loading.value = true
        _error.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loading.value = false
                _user.value = auth.currentUser
                refreshAdminFlag()
                // elegimos portal usando flag admin (si falla, default alumno)
                val goAdmin = isAdmin.value == true
                _portal.value = if (goAdmin) Portal.ADMIN else Portal.ALUMNO
            }
            .addOnFailureListener { e ->
                _loading.value = false
                val msg = mapError(e)
                _error.value = msg
                onError(msg)
            }
    }

    fun sendReset(identifier: String, callback: (ok: Boolean, err: String?) -> Unit) {
        val email = normalizeIdentifier(identifier)
        _loading.value = true
        _error.value = null
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { _loading.value = false; callback(true, null) }
            .addOnFailureListener { e ->
                _loading.value = false
                val msg = mapError(e)
                _error.value = msg
                callback(false, msg)
            }
    }

    fun clearError() { _error.value = null }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
            _user.value = null
            _isAdmin.value = null
        }
    }
}
