package com.example.univapp.ui.admin

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QuestionMark

enum class ActivityType(
    val title: String,
    val color: Color,
    val icon: ImageVector
) {
    ADD("Agregado", Color(0xFF2E7D32), Icons.Default.Add),
    UPDATE("Actualizado", Color(0xFF1565C0), Icons.Default.Edit),
    DELETE("Eliminado", Color(0xFFC62828), Icons.Default.Delete),
    CREATE("Creado", Color(0xFF6A1B9A), Icons.Default.Add),
    UNKNOWN("Desconocido", Color(0xFF616161), Icons.Default.QuestionMark);

    companion object {
        fun from(value: String?): ActivityType =
            try { value?.let { valueOf(it) } ?: UNKNOWN } catch (_: Exception) { UNKNOWN }
    }
}
