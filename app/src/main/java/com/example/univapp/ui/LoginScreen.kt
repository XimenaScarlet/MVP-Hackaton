package com.example.univapp.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private const val PREFS_NAME = "login_prefs"
private const val KEY_REMEMBER = "remember_me"
private const val KEY_LAST_ID = "last_identifier"

@Composable
fun LoginScreen(
    vm: AuthViewModel = viewModel(),
    errorText: String? = null,
    onLogin: (identifier: String, password: String, remember: Boolean) -> Unit,
    onForgot: (emailOrMat: String, afterSent: () -> Unit) -> Unit,
    onDismissError: () -> Unit
) {
    val teal = Color(0xFF0F6C6D)
    val tealDark = Color(0xFF0C5758)
    val mintA = Color(0xFFD0EFE9)
    val mintB = Color(0xFFC4E7E1)
    val cardBg = Color(0xFFF2F5F4)
    val hintColor = Color(0xFF6B7F7C)

    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    // Cargar preferencias
    LaunchedEffect(Unit) {
        val sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        rememberMe = sp.getBoolean(KEY_REMEMBER, false)
        if (rememberMe) identifier = sp.getString(KEY_LAST_ID, "") ?: ""
    }
    fun persistPrefs() {
        val sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit()
            .putBoolean(KEY_REMEMBER, rememberMe)
            .putString(KEY_LAST_ID, if (rememberMe) identifier else "")
            .apply()
    }

    var showForgot by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(mintA, mintB)))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(teal)
        ) {
            Text(
                "Bienvenido!\nUTC Panel",
                color = Color.White,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 48.dp, start = 24.dp)
            )
        }

        // Card
        Surface(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
            color = cardBg,
            tonalElevation = 0.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.TopCenter)
                .offset(y = 160.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Text("Login", color = tealDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = tealDark) },
                    placeholder = { Text("Matrícula o correo") },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = tealDark) },
                    placeholder = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            if (showPass) "Hide" else "Show",
                            color = teal,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showPass = !showPass }
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 12.sp
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = {
                                rememberMe = it
                                persistPrefs()
                            },
                            colors = CheckboxDefaults.colors(checkedColor = teal)
                        )
                        Text("Remember me", color = hintColor, fontSize = 12.sp)
                    }
                    Text(
                        "Forgot Password",
                        color = teal,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showForgot = true }
                            .padding(4.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        persistPrefs()
                        // ⚠️ Pasar el valor CRUDO. El VM/AppNav normaliza o aplica bypass transporte.
                        onLogin(identifier.trim(), password, rememberMe)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = teal,
                        contentColor = Color.White
                    ),
                    enabled = identifier.isNotBlank() && password.isNotBlank()
                ) { Text("Login", fontSize = 16.sp) }
            }
        }

        // Banner error
        AnimatedVisibility(
            visible = errorText != null,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        errorText ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onDismissError) { Text("OK") }
                }
            }
        }
    }

    // Diálogo Forgot
    if (showForgot) {
        var who by remember { mutableStateOf(identifier) }
        AlertDialog(
            onDismissRequest = { showForgot = false },
            title = { Text("Recover password") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = who,
                        onValueChange = { who = it },
                        singleLine = true,
                        placeholder = { Text("Matrícula o correo") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Te enviaremos instrucciones al correo.", color = Color(0xFF6B7F7C), fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Aquí también pásalo crudo; tu VM normaliza.
                    onForgot(who.trim()) { showForgot = false }
                }) { Text("Enviar") }
            },
            dismissButton = { TextButton(onClick = { showForgot = false }) { Text("Cancelar") } },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
