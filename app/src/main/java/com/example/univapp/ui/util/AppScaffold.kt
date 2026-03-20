package com.example.univapp.ui.util

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Contenedor base para todas las pantallas de la app.
 * Aplica automáticamente paddings para evitar que el contenido choque con la Status Bar y Navigation Bar.
 */
@Composable
fun AppScaffold(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        snackbarHost = snackbarHost,
        // Este parámetro es clave: ignora los insets por defecto para que nosotros los manejemos 
        // o los aplica correctamente si usamos el PaddingValues de content.
        contentWindowInsets = WindowInsets.safeDrawing 
    ) { innerPadding ->
        content(innerPadding)
    }
}
