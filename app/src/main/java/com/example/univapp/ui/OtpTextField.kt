package com.example.univapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            // Filtra para aceptar solo dígitos y trunca a 6 caracteres
            val filteredValue = newValue.filter { it.isDigit() }.take(6)
            onValueChange(filteredValue)
        },
        label = { Text("Código de 6 dígitos") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            letterSpacing = 8.sp // Espacio para que los números se vean separados
        ),
        visualTransformation = { text ->
            TransformedText(
                AnnotatedString(text.text.padEnd(6, ' ')),
                OffsetMapping.Identity
            )
        }
    )
}
