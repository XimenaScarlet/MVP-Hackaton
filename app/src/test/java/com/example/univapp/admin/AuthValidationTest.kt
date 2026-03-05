package com.example.univapp.admin

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AuthValidationTest {

    @Test
    fun `correo valido devuelve true`() {
        val email = "test@example.com"
        assertThat(isValidEmail(email)).isTrue()
    }

    @Test
    fun `correo sin @ devuelve false`() {
        val email = "testexample.com"
        assertThat(isValidEmail(email)).isFalse()
    }

    @Test
    fun `longitud de contraseña valida devuelve true`() {
        val password = "password123"
        assertThat(isValidPassword(password)).isTrue()
    }

    @Test
    fun `longitud de contraseña invalida devuelve false`() {
        val password = "12345"
        assertThat(isValidPassword(password)).isFalse()
    }
}

// Funciones de utilidad (simuladas para el test)
private fun isValidEmail(email: String): Boolean {
    return "@" in email && email.length > 5
}

private fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}
