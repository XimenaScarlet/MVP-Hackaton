package com.example.univapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.univapp.data.Carrera

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarreraSelector(
    carreras: List<Carrera>,
    selectedCarrera: Carrera?,
    onCarreraSelected: (Carrera) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFF007BFF).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF007AFF))
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Carrera seleccionada", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(selectedCarrera?.nombre ?: "", fontWeight = FontWeight.SemiBold)
                }
                Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF007AFF))
            }
        }
        if (expanded) {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                carreras.forEach { carrera ->
                    DropdownMenuItem(text = { Text(carrera.nombre ?: "") }, onClick = {
                        onCarreraSelected(carrera)
                        expanded = false
                    })
                }
            }
        }
    }
}