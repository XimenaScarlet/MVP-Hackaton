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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.R

@Composable
fun LoginScreen(
    vm: AuthViewModel = viewModel(),
    errorText: String? = null,
    onLogin: (identifier: String, password: String, remember: Boolean) -> Unit,
    onForgot: (emailOrMat: String, afterSent: () -> Unit) -> Unit,
    onDismissError: () -> Unit
) {
    // Colores
    val teal = Color(0xFF0F6C6D)
    val tealDark = Color(0xFF0C5758)
    val mintA = Color(0xFFD0EFE9)
    val mintB = Color(0xFFC4E7E1)
    val cardBg = Color(0xFFF2F5F4)

    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

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

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { onLogin(identifier.trim(), password, false) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = teal, contentColor = Color.White),
                    enabled = identifier.isNotBlank() && password.isNotBlank()
                ) { Text("Entrar", fontSize = 16.sp) }
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
                    Text(errorText ?: "", color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismissError) { Text("OK") }
                }
            }
        }
    }
}
