@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    onBack: (() -> Unit)? = null,
    onLogout: () -> Unit,
    vm: SettingsViewModel = viewModel()
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val ctx = LocalContext.current
    val scrollState = rememberScrollState()

    val dark by vm.darkMode.collectAsState()
    val mostrarCorreo by vm.showEmail.collectAsState()
    val notificaciones by vm.pushNotifications.collectAsState()
    var confirmLogout by remember { mutableStateOf(false) }

    // Colores dinámicos
    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val sectionHeaderColor = if (dark) Color(0xFF94A3B8) else Color(0xFF9CA3AF)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF74777F)
    val iconBoxColor = if (dark) Color(0xFF334155) else Color(0xFFEEF2FF)
    val iconTintColor = if (dark) Color(0xFF60A5FA) else Color(0xFF1D4ED8)
    val dividerColor = if (dark) Color(0xFF334155) else Color(0xFFF1F3F4)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 12.dp, 8.dp, 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBack?.invoke() ?: backDispatcher?.onBackPressed() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Atrás",
                        modifier = Modifier.size(30.dp),
                        tint = titleColor
                    )
                }
                Text(
                    text = "Configuración",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // APARIENCIA
                SettingsSectionTitle("APARIENCIA", sectionHeaderColor)
                SettingsCard(cardBg) {
                    SettingsSwitchItem(
                        icon = Icons.Outlined.DarkMode,
                        title = "Tema oscuro",
                        subtitle = "Usar colores oscuros en la app",
                        checked = dark,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        iconBoxColor = iconBoxColor,
                        iconTintColor = iconTintColor,
                        onCheckedChange = { vm.toggleDarkMode(it) }
                    )
                }

                // PREFERENCIAS
                SettingsSectionTitle("PREFERENCIAS", sectionHeaderColor)
                SettingsCard(cardBg) {
                    Column {
                        SettingsSwitchItem(
                            icon = Icons.Outlined.Shield,
                            title = "Mostrar correo en perfil",
                            subtitle = if (mostrarCorreo) "Visible para otros usuarios" else "Oculto para otros usuarios",
                            checked = mostrarCorreo,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor,
                            iconBoxColor = iconBoxColor,
                            iconTintColor = iconTintColor,
                            onCheckedChange = { vm.toggleShowEmail(it) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = dividerColor)
                        SettingsSwitchItem(
                            icon = Icons.Outlined.Notifications,
                            title = "Notificaciones push",
                            subtitle = "Alertas de notas y tareas",
                            checked = notificaciones,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor,
                            iconBoxColor = iconBoxColor,
                            iconTintColor = iconTintColor,
                            onCheckedChange = { vm.togglePushNotifications(it) }
                        )
                    }
                }

                // ALMACENAMIENTO
                SettingsSectionTitle("ALMACENAMIENTO", sectionHeaderColor)
                SettingsCard(cardBg) {
                    SettingsClickableItem(
                        icon = Icons.Outlined.CleaningServices,
                        title = "Limpiar caché",
                        subtitle = "Elimina datos temporales",
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        iconBoxColor = iconBoxColor,
                        iconTintColor = iconTintColor,
                        onClick = { 
                            val bytes = vm.clearCache()
                            val mb = String.format("%.2f", bytes / (1024.0 * 1024.0))
                            Toast.makeText(ctx, "Se liberaron $mb MB", Toast.LENGTH_SHORT).show() 
                        }
                    )
                }

                // ACERCA DE
                SettingsSectionTitle("ACERCA DE", sectionHeaderColor)
                SettingsCard(cardBg) {
                    Column {
                        SettingsInfoItem(
                            icon = Icons.Outlined.Info,
                            title = "Versión",
                            subtitle = "UnivApp 1.2.4 Build 2024",
                            titleColor = titleColor,
                            subtitleColor = subtitleColor,
                            iconBoxColor = iconBoxColor,
                            iconTintColor = iconTintColor
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = dividerColor)
                        SettingsClickableItem(
                            icon = Icons.Outlined.Description,
                            title = "Términos y Condiciones",
                            trailingIcon = Icons.Default.OpenInNew,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor,
                            iconBoxColor = iconBoxColor,
                            iconTintColor = iconTintColor,
                            onClick = { /* Abrir términos */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Logout Button
                TextButton(
                    onClick = { confirmLogout = true },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        text = "Cerrar sesión",
                        color = if (dark) Color(0xFFFB7185) else Color(0xFFDC2626),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            containerColor = cardBg,
            titleContentColor = titleColor,
            textContentColor = subtitleColor,
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que quieres cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = { confirmLogout = false; onLogout() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { confirmLogout = false }) { 
                    Text("Cancelar", color = iconTintColor) 
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun SettingsSectionTitle(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
    )
}

@Composable
private fun SettingsCard(bgColor: Color, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = bgColor,
        shadowElevation = 0.5.dp
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    titleColor: Color,
    subtitleColor: Color,
    iconBoxColor: Color,
    iconTintColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIconBox(icon, iconBoxColor, iconTintColor)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = titleColor)
            Text(text = subtitle, fontSize = 12.sp, color = subtitleColor)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF3B82F6),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = if (titleColor == Color.White) Color(0xFF475569) else Color(0xFFE5E7EB),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailingIcon: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
    titleColor: Color,
    subtitleColor: Color,
    iconBoxColor: Color,
    iconTintColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIconBox(icon, iconBoxColor, iconTintColor)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = titleColor)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 12.sp, color = subtitleColor)
            }
        }
        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = subtitleColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    titleColor: Color,
    subtitleColor: Color,
    iconBoxColor: Color,
    iconTintColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIconBox(icon, iconBoxColor, iconTintColor)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = titleColor)
            Text(text = subtitle, fontSize = 12.sp, color = subtitleColor)
        }
    }
}

@Composable
private fun SettingIconBox(icon: ImageVector, bgColor: Color, tintColor: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(20.dp)
        )
    }
}
