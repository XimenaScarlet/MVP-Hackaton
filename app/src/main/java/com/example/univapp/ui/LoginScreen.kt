package com.example.univapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univapp.R
import com.example.univapp.ui.util.AppScaffold
import com.example.univapp.ui.util.ValidatedTextField
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    vm: AuthViewModel = viewModel(),
    errorText: String?,
    onLogin: (identifier: String, password: String, remember: Boolean) -> Unit,
    onDismissError: () -> Unit
) {
    val loading by vm.loading.collectAsState()
    
    var showHeader by remember { mutableStateOf(false) }
    var showCard by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showHeader = true
        delay(200)
        showCard = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathAlpha"
    )
    val softScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "softScale"
    )

    val teal = Color(0xFF0F6C6D)
    val mintA = Color(0xFFD0EFE9)
    val mintB = Color(0xFFC4E7E1)
    val cardBg = Color(0xFFF2F5F4)

    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var showForgotPasswordSheet by remember { mutableStateOf(false) }

    AppScaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(mintA, mintB)))
        ) {
            // Franja superior
            AnimatedVisibility(
                visible = showHeader,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(animationSpec = tween(600)),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .background(teal, RoundedCornerShape(bottomStart = 70.dp, bottomEnd = 70.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(breathAlpha * 0.4f)
                            .blur(80.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color.White.copy(alpha = 0.6f), Color.Transparent),
                                    radius = 1500f
                                )
                            )
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_3_este_si),
                            contentDescription = "Logo UTC",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(140.dp)
                                .padding(bottom = 12.dp)
                                .graphicsLayer(scaleX = softScale, scaleY = softScale)
                        )
                        
                        Text(
                            text = "MI UTC",
                            style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color.White.copy(alpha = breathAlpha),
                                        Color.White
                                    )
                                ),
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 4.sp
                            ),
                            modifier = Modifier.graphicsLayer(scaleX = softScale, scaleY = softScale)
                        )
                    }
                }
            }

            // Tarjeta central
            AnimatedVisibility(
                visible = showCard,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(animationSpec = tween(800)),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = cardBg,
                    shadowElevation = 15.dp,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 120.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(min = 320.dp)
                            .padding(horizontal = 24.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("¡Hola de nuevo!", color = teal, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Inicia sesión para continuar", color = Color.Gray, fontSize = 14.sp)
                        Spacer(Modifier.height(28.dp))

                        ValidatedTextField(
                            value = identifier,
                            onValueChange = { identifier = it },
                            label = "Matrícula o Correo",
                            leadingIcon = { Icon(Icons.Outlined.Email, null, tint = teal) },
                            maxLength = 50,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        Spacer(Modifier.height(18.dp))

                        ValidatedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Contraseña",
                            leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = teal) },
                            maxLength = 30,
                            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPass = !showPass }) {
                                    Icon(
                                        imageVector = if (showPass) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = null,
                                        tint = teal
                                    )
                                }
                            },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { showForgotPasswordSheet = true }, modifier = Modifier.align(Alignment.End)) {
                            Text("¿Olvidaste tu contraseña?", fontSize = 12.sp, color = teal)
                        }
                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = { onLogin(identifier.trim(), password, false) },
                            modifier = Modifier.fillMaxWidth().height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = teal),
                            enabled = identifier.isNotBlank() && password.length >= 6 && !loading
                        ) {
                            if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            else Text("Iniciar Sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Error Banner
            AnimatedVisibility(
                visible = errorText != null && !showForgotPasswordSheet,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp)
            ) {
                Surface(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFFF1F1),
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFDADA))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.ErrorOutline, null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            errorText ?: "",
                            color = Color(0xFF410002),
                            modifier = Modifier.weight(1f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        TextButton(
                            onClick = { onDismissError() },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFBA1A1A))
                        ) {
                            Text("Cerrar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (showForgotPasswordSheet) {
                ForgotPasswordSheet(
                    vm = vm,
                    onDismiss = { showForgotPasswordSheet = false }
                )
            }
        }
    }
}

@Composable
fun ForgotPasswordSheet(vm: AuthViewModel, onDismiss: () -> Unit) {
    var matricula by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val loading by vm.loading.collectAsState()
    val teal = Color(0xFF0F6C6D)

    Dialog(
        onDismissRequest = { if (!loading) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (successMessage == null) "Recuperar acceso" else "¡Éxito!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = teal
                        )
                        IconButton(onClick = onDismiss, enabled = !loading) {
                            Icon(Icons.Outlined.Close, contentDescription = "Cerrar")
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    if (successMessage != null) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Outlined.Mail, null, tint = teal, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(16.dp))
                            Text(successMessage ?: "", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = teal)
                            ) {
                                Text("Volver al login")
                            }
                        }
                    } else {
                        Text(
                            "Ingresa tu matrícula o correo institucional para recibir un enlace de recuperación.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(24.dp))
                        
                        ValidatedTextField(
                            value = matricula,
                            onValueChange = { matricula = it },
                            label = "Matrícula o Correo",
                            maxLength = 50,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
                            enabled = !loading
                        )
                        
                        Spacer(Modifier.height(24.dp))
                        
                        Button(
                            onClick = { 
                                if (matricula.isNotBlank()) {
                                    vm.sendPasswordResetLink(matricula) { ok, m -> if (ok) successMessage = m }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = teal),
                            enabled = matricula.isNotBlank() && !loading
                        ) {
                            if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            else Text("Enviar enlace", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
