package com.example.univapp.ui.admin

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditAlumnoScreen(
    alumnoId: String,
    onBack: () -> Unit,
    vm: AdminEditAlumnoViewModel = viewModel()
) {
    val alumno by vm.alumno.collectAsState()
    val carreras by vm.carreras.collectAsState()
    val grupos by vm.grupos.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val isSaving by vm.isSaving.collectAsState()
    val saveSuccess by vm.saveSuccess.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()

    LaunchedEffect(alumnoId) {
        if (alumnoId.isNotBlank()) {
            vm.loadAlumno(alumnoId)
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Alumno", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (alumno != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileImage(alumno?.id ?: "")
                    Spacer(modifier = Modifier.height(24.dp))

                    CustomOutlinedTextField(label = "Nombre", value = alumno?.nombre ?: "", onValueChange = { vm.onFieldChange("nombre", it) })
                    CustomOutlinedTextField(label = "Teléfono", value = alumno?.telefono ?: "", onValueChange = { vm.onFieldChange("telefono", it) }, keyboardType = KeyboardType.Phone)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column(Modifier.weight(1f)) {
                                    DropdownSelector(label = "Género", selectedValue = alumno?.genero ?: "", options = listOf("Femenino", "Masculino"), onValueSelected = { vm.onFieldChange("genero", it) })
                                }
                                Column(Modifier.weight(1f)) {
                                    CustomOutlinedTextField(label = "Edad", value = alumno?.edad?.toString() ?: "", onValueChange = { vm.onFieldChange("edad", it) }, keyboardType = KeyboardType.Number)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            DatePickerField(label = "Fecha de Nacimiento", value = alumno?.fechaNacimiento ?: "", onValueChange = { vm.onFieldChange("fechaNacimiento", it) })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    DropdownSelector(
                        label = "Carrera",
                        selectedValue = carreras.find { it.id == (alumno?.carreraId ?: "") }?.nombre ?: "",
                        options = carreras.map { it.nombre ?: "" },
                        onValueSelected = { selectedName ->
                            val selectedId = carreras.find { it.nombre == selectedName }?.id ?: ""
                            vm.onFieldChange("carreraId", selectedId)
                        }
                    )

                    DropdownSelector(
                        label = "Grupo",
                        selectedValue = grupos.find { it.id == alumno?.grupoId }?.nombre ?: "",
                        options = grupos.map { it.nombre ?: "" },
                        onValueSelected = { selectedName ->
                            val selectedId = grupos.find { (it.nombre ?: "") == selectedName }?.id ?: ""
                            vm.onFieldChange("grupoId", selectedId)
                        }
                    )

                    DropdownSelector(label = "Estatus Académico", selectedValue = alumno?.estatusAcademico ?: "", options = listOf("Regular", "Irregular"), onValueSelected = { vm.onFieldChange("estatusAcademico", it) })
                    DatePickerField(label = "Fecha de Ingreso", value = alumno?.fechaIngreso ?: "", onValueChange = { vm.onFieldChange("fechaIngreso", it) })

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("CONTACTO DE EMERGENCIA", modifier = Modifier.fillMaxWidth(), color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    CustomOutlinedTextField(label = "Nombre de Contacto", value = alumno?.nombreContacto ?: "", onValueChange = { vm.onFieldChange("nombreContacto", it) })
                    CustomOutlinedTextField(label = "Teléfono de Emergencia", value = alumno?.telefonoEmergencia ?: "", onValueChange = { vm.onFieldChange("telefonoEmergencia", it) }, keyboardType = KeyboardType.Phone)

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { vm.saveChanges() },
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A5AE0))
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileImage(id: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8E5FB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF6A5AE0), modifier = Modifier.size(60.dp))
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6A5AE0))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("ID: $id", color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun CustomOutlinedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        modifier = modifier.fillMaxWidth().padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color(0xFF6A5AE0),
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
fun DatePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            onValueChange("$dayOfMonth/${month + 1}/$year")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(label, color = Color.Gray) },
        readOnly = true,
        modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }.padding(bottom = 16.dp),
        trailingIcon = {
            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date", tint = Color.Gray)
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color(0xFF6A5AE0),
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}
