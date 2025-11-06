@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui

import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBack: (() -> Unit)? = null,
    onLogout: () -> Unit,               // requerido
    darkInitial: Boolean = false,
    onToggleDark: (Boolean) -> Unit = {}
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val ctx = LocalContext.current

    var dark by remember { mutableStateOf(darkInitial) }
    var mostrarCorreo by remember { mutableStateOf(true) }
    var confirmLogout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() ?: backDispatcher?.onBackPressed() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Apariencia
            Text(
                "Apariencia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            SettingSwitch(
                icon = { Icon(Icons.Outlined.SettingsBrightness, null) },
                title = "Tema oscuro",
                subtitle = "Usar colores oscuros en la app",
                checked = dark,
                onCheckedChange = { dark = it; onToggleDark(it) }
            )

            // Preferencias
            Text(
                "Preferencias",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            SettingSwitch(
                icon = { Icon(Icons.Outlined.Security, null) },
                title = "Mostrar correo en perfil",
                subtitle = if (mostrarCorreo) "Visible" else "Oculto",
                checked = mostrarCorreo,
                onCheckedChange = { mostrarCorreo = it }
            )

            // Almacenamiento
            Text(
                "Almacenamiento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            SettingClickable(
                icon = { Icon(Icons.Outlined.CleaningServices, null) },
                title = "Limpiar caché",
                subtitle = "Elimina datos temporales"
            ) {
                Toast.makeText(ctx, "Caché limpia", Toast.LENGTH_SHORT).show()
            }

            // Acerca de
            Text(
                "Acerca de",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            SettingClickable(
                icon = { Icon(Icons.Outlined.Info, null) },
                title = "Versión",
                subtitle = "UnivApp 1.0.0"
            )

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { confirmLogout = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = { confirmLogout = false; onLogout() }) { Text("Sí, salir") }
            },
            dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text("Cancelar") } }
        )
    }
}

/* Helpers */
@Composable
private fun SettingSwitch(
    icon: @Composable (() -> Unit)? = null,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        leadingContent = icon,
        headlineContent = { Text(title) },
        supportingContent = { if (subtitle != null) Text(subtitle) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}

@Composable
private fun SettingClickable(
    icon: @Composable (() -> Unit)? = null,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    ListItem(
        leadingContent = icon,
        headlineContent = { Text(title) },
        supportingContent = { if (subtitle != null) Text(subtitle) },
        trailingContent = trailing,
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    )
}
