package com.example.univapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.R
import com.example.univapp.data.hasInternet

@Composable
fun LoginScreen(
    vm: AuthViewModel = viewModel(),
    errorText: String?,
    onLogin: (identifier: String, password: String, remember: Boolean) -> Unit,
    onDismissError: () -> Unit
) {
    val loading by vm.loading.collectAsState()
    val context = LocalContext.current
    // Colores
    val teal = Color(0xFF0F6C6D)
    val tealDark = Color(0xFF0C5758)
    val mintA = Color(0xFFD0EFE9)
    val mintB = Color(0xFFC4E7E1)
    val cardBg = Color(0xFFF2F5F4)

    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val currentError = localError ?: errorText

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(mintA, mintB)))
    ) {
        // Franja superior con logo centrado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(teal)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo_3_este_si),
                    contentDescription = "Logo UTC",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 12.dp)
                )
                Text(
                    text = "Mi UTC",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Tarjeta centrada
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = cardBg,
            tonalElevation = 0.dp,
            shadowElevation = 10.dp,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 320.dp)
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Iniciar sesión", color = tealDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    leadingIcon = { Icon(Icons.Outlined.Email, null, tint = tealDark) },
                    placeholder = {
                        Box(Modifier.fillMaxWidth()) {
                            Text("Matrícula", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = tealDark) },
                    placeholder = {
                        Box(Modifier.fillMaxWidth()) {
                            Text("Password", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { showPass = !showPass },
                        ) {
                            Icon(
                                imageVector = if (showPass) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = teal
                            )
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth()
                )

                // Lógica para mostrar el botón de olivido de contraseña
                if (currentError != null && currentError.contains("Contraseña incorrecta")) {
                    TextButton(onClick = { showForgotPasswordDialog = true }) {
                        Text("¿Olvidaste tu contraseña?")
                    }
                } else {
                    Spacer(Modifier.height(24.dp)) // Mantiene el espacio consistente
                }

                Button(
                    onClick = {
                        localError = null
                        if (hasInternet(context)) {
                            onLogin(identifier.trim(), password, false)
                        } else {
                            localError = "Sin conexión a internet. Conéctate para iniciar sesión."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = teal, contentColor = Color.White),
                    enabled = identifier.isNotBlank() && password.isNotBlank() && !loading
                ) { Text("Entrar", fontSize = 16.sp) }
            }
        }

        // Banner de error general
        AnimatedVisibility(
            visible = currentError != null && !showForgotPasswordDialog, // No mostrar si el diálogo está abierto
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
                    Text(currentError ?: "", color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        localError = null
                        onDismissError()
                    }) { Text("OK") }
                }
            }
        }

        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                vm = vm,
                onDismiss = { showForgotPasswordDialog = false }
            )
        }
    }
}


@Composable
fun ForgotPasswordDialog(
    vm: AuthViewModel,
    onDismiss: () -> Unit,
) {
    var matricula by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val loading by vm.loading.collectAsState()

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text("Recuperar contraseña") },
        text = {
            Column {
                if (successMessage != null) {
                    Text(successMessage!!)
                } else {
                    Text("Te enviaremos un enlace de recuperación al correo asociado a tu matrícula.")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = matricula,
                        onValueChange = { matricula = it },
                        label = { Text("Matrícula") },
                        singleLine = true,
                        isError = error != null,
                        enabled = !loading
                    )
                    if (loading) {
                        Spacer(Modifier.height(16.dp))
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            if (successMessage == null) {
                Button(
                    enabled = !loading,
                    onClick = {
                        error = null
                        if (matricula.isBlank()) {
                            error = "La matrícula no puede estar vacía."
                        } else {
                            vm.sendPasswordResetLink(matricula) { ok, msg ->
                                if (ok) {
                                    successMessage = msg
                                } else {
                                    error = msg
                                }
                            }
                        }
                    }
                ) {
                    Text("Enviar enlace")
                }
            }
        },
        dismissButton = {
            if (!loading) {
                TextButton(onClick = onDismiss) {
                    Text(if (successMessage != null) "Cerrar" else "Cancelar")
                }
            }
        }
    )
}
