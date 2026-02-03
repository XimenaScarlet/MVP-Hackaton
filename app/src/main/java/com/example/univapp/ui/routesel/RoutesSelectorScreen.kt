@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui.routesel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoutesSelectorScreen(
    onBack: () -> Unit = {},
    onOpenSaltillo: () -> Unit,
    onOpenRamos: () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Selecciona tu Ruta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RouteOutlinedButton(
                text = "Ruta Saltillo",
                onClick = onOpenSaltillo
            )
            Spacer(Modifier.height(20.dp))
            RouteOutlinedButton(
                text = "Ruta Ramos",
                onClick = onOpenRamos
            )
        }
    }
}

@Composable
private fun RouteOutlinedButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, Color(0xFF0EA5E9)), // celeste
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF374151) // gris oscuro
        ),
        modifier = Modifier
            .fillMaxWidth(0.65f) // más angosto
            .height(110.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.DirectionsBus,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
