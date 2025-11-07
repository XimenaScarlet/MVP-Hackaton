@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)

package com.example.univapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.univapp.ui.model.SubjectLite
import com.example.univapp.ui.model.subjectsByTerm

/* ---------- Paleta ---------- */
private val Title = Color(0xFF0F172A)
private val Muted = Color(0xFF64748B)
private val CardBlue = Color(0xFFE6F0FF)
private val CardLilac = Color(0xFFF1E9FF)
private val CardMint = Color(0xFFE7FFF6)
private val CardGray = Color(0xFFF6F7FB)
private val Accent = Color(0xFF2563EB)

/* ---------- Screen “Your Courses” ---------- */
@Composable
fun SubjectsScreen(
    onBack: () -> Unit,
    onOpenSubject: (term: Int, subjectId: Long) -> Unit
) {
    var term by remember { mutableStateOf(1) }
    var showPicker by remember { mutableStateOf(false) }
    val subjects = remember(term) { subjectsByTerm(term) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis materias") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showPicker = true },
                icon = { Icon(Icons.Outlined.Add, null) },
                text = { Text("Cuatrimestre $term") },
                containerColor = Accent,
                contentColor = Color.White
            )
        }
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(pv)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(6.dp))
            Text(
                "Tus materias",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Title,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { 0.35f },
                trackColor = CardGray,
                color = Accent,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.6f)
                    .height(6.dp)
                    .padding(bottom = 10.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (subjects.isNotEmpty()) {
                    item {
                        SubjectCardHighlight(
                            subject = subjects.first(),
                            bg = CardBlue,
                            onClick = { onOpenSubject(term, subjects.first().id) }
                        )
                    }
                }
                items(subjects.drop(if (subjects.isNotEmpty()) 1 else 0)) { s ->
                    SubjectCardSmall(
                        subject = s,
                        bg = when ((s.id % 3).toInt()) {
                            0 -> CardLilac
                            1 -> CardMint
                            else -> CardGray
                        },
                        onClick = { onOpenSubject(term, s.id) }
                    )
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }

    if (showPicker) {
        ModalBottomSheet(
            onDismissRequest = { showPicker = false },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Selecciona cuatrimestre", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Title)
                Spacer(Modifier.height(8.dp))
                repeat(10) { idx ->
                    val num = idx + 1
                    val selected = num == term
                    val bg = if (selected) Accent.copy(.08f) else Color.Transparent
                    val fg = if (selected) Accent else Title
                    Surface(
                        color = bg,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { term = num; showPicker = false }
                    ) {
                        Text(
                            "Cuatrimestre $num",
                            modifier = Modifier.padding(14.dp),
                            color = fg,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

/* ---------- Tarjetas ---------- */
@Composable
fun SubjectCardHighlight(
    subject: SubjectLite,
    bg: Color,
    onClick: () -> Unit
) {
    Surface(
        color = bg,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("CODING", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Accent)
            Spacer(Modifier.height(4.dp))
            Text(
                subject.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Title
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Schedule, null, tint = Title.copy(.7f), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(subject.schedule, fontSize = 12.sp, color = Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun SubjectCardSmall(
    subject: SubjectLite,
    bg: Color,
    onClick: () -> Unit
) {
    Surface(
        color = bg,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 110.dp)
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                subject.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Title
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Schedule, null, tint = Muted, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(subject.schedule, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp, color = Muted)
            }
            Spacer(Modifier.height(4.dp))
            Text(subject.room, fontSize = 11.sp, color = Muted)
        }
    }
}
