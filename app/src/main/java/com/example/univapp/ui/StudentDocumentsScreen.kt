package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudentDocumentsScreen(
    onBack: () -> Unit = {},
    vm: StudentProceduresViewModel = viewModel(),
    settingsVm: SettingsViewModel = viewModel()
) {
    val dark by settingsVm.darkMode.collectAsState()
    val documents by vm.digitalDocuments.collectAsState()
    val isLoading by vm.loading.collectAsState()

    val bgColor = if (dark) Color(0xFF0F172A) else Color(0xFFF8F9FB)
    val cardBg = if (dark) Color(0xFF1E293B) else Color.White
    val titleColor = if (dark) Color.White else Color(0xFF1A1C1E)
    val textColor = if (dark) Color(0xFFE2E8F0) else Color(0xFF1A1C1E)
    val subtitleColor = if (dark) Color(0xFF94A3B8) else Color(0xFF74777F)

    LaunchedEffect(Unit) {
        vm.loadDigitalDocuments()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = cardBg,
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Atrás",
                            modifier = Modifier.size(32.dp),
                            tint = if (dark) Color.White else Color(0xFF004696)
                        )
                    }
                    Text(
                        text = "Mis Documentos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
            }

            if (isLoading && documents.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2563EB))
                }
            } else if (documents.isEmpty()) {
                EmptyDocumentsState(dark, titleColor, subtitleColor)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(documents) { doc ->
                        DocumentCard(doc, cardBg, titleColor, subtitleColor, dark) {
                            vm.openDocument(doc)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "DOCUMENTOS CON VALIDEZ OFICIAL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = subtitleColor,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyDocumentsState(dark: Boolean, titleColor: Color, subtitleColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FolderOpen,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = if (dark) Color(0xFF334155) else Color(0xFFD1D5DB)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay documentos aún",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )
        Text(
            text = "Aquí aparecerán los archivos que descargues digitalmente.",
            fontSize = 14.sp,
            color = subtitleColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DocumentCard(
    item: StudentProceduresViewModel.DocumentRecord, 
    cardBg: Color,
    titleColor: Color,
    subtitleColor: Color,
    dark: Boolean,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(if (dark) Color(0xFF334155) else Color(0xFFE8F0FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PictureAsPdf, null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(item.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor)
                    Text(item.date, fontSize = 14.sp, color = subtitleColor)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Button(
                onClick = onOpen,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FileOpen, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Abrir PDF", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
