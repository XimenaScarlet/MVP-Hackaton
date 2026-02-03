package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentsScreen(
    onBack: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FB)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
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
                            tint = Color(0xFF2563EB)
                        )
                    }
                    Text(
                        text = "Pagos y Adeudos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Periodo Actual Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "PERIODO ACTUAL", 
                                fontSize = 11.sp, 
                                color = Color(0xFF9CA3AF), 
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Ene–Abr 2026", 
                                fontSize = 20.sp, 
                                color = Color(0xFF1A1C1E), 
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            Icons.Default.UnfoldMore, 
                            null, 
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SummaryItem("TOTAL A PAGAR", "$12,450", Color(0xFF1A1C1E))
                        
                        VerticalDivider(modifier = Modifier.height(30.dp), color = Color(0xFFF3F4F6))
                        
                        SummaryItem("TOTAL PAGADO", "$8,500", Color(0xFF1A1C1E))
                        
                        VerticalDivider(modifier = Modifier.height(30.dp), color = Color(0xFFF3F4F6))
                        
                        SummaryItem("SALDO PENDIENTE", "$3,950", Color(0xFFEF4444))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Alert Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFEF3C7)))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(28.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Adeudos pendientes", 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 16.sp, 
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Colegiatura Marzo 2026 vence el 15 de Marzo. Evita recargos por pago tardío.",
                                fontSize = 14.sp, 
                                color = Color(0xFF92400E), 
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Movimientos del periodo", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        "Ver histórico", 
                        fontSize = 14.sp, 
                        color = Color(0xFF2563EB), 
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Movimientos
                MovementCard(
                    title = "Colegiatura Feb", 
                    subtitle = "Folio: 45892 • 02 Feb 2026", 
                    amount = "- $4,250.00", 
                    status = "APLICADO", 
                    icon = Icons.Default.ReceiptLong
                )
                Spacer(modifier = Modifier.height(12.dp))
                MovementCard(
                    title = "Inscripción Semestral", 
                    subtitle = "Folio: 45710 • 15 Ene 2026", 
                    amount = "- $4,250.00", 
                    status = "APLICADO", 
                    icon = Icons.Default.Payments
                )
                Spacer(modifier = Modifier.height(12.dp))
                MovementCard(
                    title = "Cargo Colegiatura Mar", 
                    subtitle = "Folio: --- • 01 Mar 2026", 
                    amount = "+ $3,950.00", 
                    status = "PENDIENTE", 
                    icon = Icons.Default.AddCard
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    "* Los montos mostrados son de carácter informativo. Todos los pagos deben realizarse directamente en las ventanillas de tesorería del campus.",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, amount: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label, 
            fontSize = 9.sp, 
            fontWeight = FontWeight.Bold, 
            color = Color(0xFF9CA3AF), 
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = amount, 
            fontSize = 18.sp, 
            fontWeight = FontWeight.ExtraBold, 
            color = valueColor
        )
    }
}

@Composable
private fun MovementCard(
    title: String, 
    subtitle: String, 
    amount: String, 
    status: String, 
    icon: ImageVector
) {
    val isPending = status == "PENDIENTE"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F9FB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp, 
                    color = Color(0xFF1A1C1E)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle, 
                    fontSize = 12.sp, 
                    color = Color(0xFF9CA3AF)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (amount.startsWith("-")) Color(0xFF1A1C1E) else Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = if (isPending) Color(0xFFFFE4E6) else Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPending) Color(0xFFEF4444) else Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
