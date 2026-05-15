package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class BpEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int?,
    val notes: String
)

private data class BpCategory(val label: String, val description: String, val colorToken: String)

private fun categorizeBp(sys: Int, dia: Int): BpCategory = when {
    sys > 180 || dia > 120 -> BpCategory("Crisis", "Seek emergency care", "error")
    sys >= 140 || dia >= 90 -> BpCategory("Stage 2 High", "Consult your doctor", "error")
    sys in 130..139 || dia in 80..89 -> BpCategory("Stage 1 High", "Monitor closely", "secondary")
    sys in 120..129 && dia < 80 -> BpCategory("Elevated", "Lifestyle changes advised", "secondary")
    sys < 120 && dia < 80 -> BpCategory("Normal", "Keep it up!", "tertiary")
    else -> BpCategory("Low", "Consult your doctor", "secondary")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureLogScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    var date by remember { mutableStateOf(todayStr) }
    var systolicInput by remember { mutableStateOf("") }
    var diastolicInput by remember { mutableStateOf("") }
    var pulseInput by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<BpEntry>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blood Pressure Log") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Reference Ranges", style = MaterialTheme.typography.titleSmall)
                        listOf(
                            Triple("Normal", "< 120 / < 80", "tertiary"),
                            Triple("Elevated", "120-129 / < 80", "secondary"),
                            Triple("Stage 1 High", "130-139 / 80-89", "secondary"),
                            Triple("Stage 2 High", "≥ 140 / ≥ 90", "error"),
                        ).forEach { (label, range, _) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, style = MaterialTheme.typography.bodySmall)
                                Text(range, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Log Reading", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it; formError = false },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            isError = formError && date.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = systolicInput,
                                onValueChange = { systolicInput = it; formError = false },
                                label = { Text("Systolic") },
                                placeholder = { Text("e.g. 120") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = formError && systolicInput.toIntOrNull() == null,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = diastolicInput,
                                onValueChange = { diastolicInput = it; formError = false },
                                label = { Text("Diastolic") },
                                placeholder = { Text("e.g. 80") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = formError && diastolicInput.toIntOrNull() == null,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = pulseInput,
                                onValueChange = { pulseInput = it },
                                label = { Text("Pulse") },
                                placeholder = { Text("bpm") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (formError) {
                            Text(
                                "Please enter a valid date, systolic, and diastolic value.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                val sys = systolicInput.toIntOrNull()
                                val dia = diastolicInput.toIntOrNull()
                                if (date.isBlank() || sys == null || dia == null) {
                                    formError = true
                                } else {
                                    entries = (listOf(
                                        BpEntry(
                                            date = date,
                                            systolic = sys,
                                            diastolic = dia,
                                            pulse = pulseInput.toIntOrNull(),
                                            notes = notes.trim()
                                        )
                                    ) + entries).sortedByDescending { it.id }
                                    systolicInput = ""; diastolicInput = ""; pulseInput = ""; notes = ""
                                    date = todayStr; formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Save Reading") }
                    }
                }
            }

            if (entries.isNotEmpty()) {
                item {
                    Text("History", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    val cat = categorizeBp(entry.systolic, entry.diastolic)
                    val catColor: Color = when (cat.colorToken) {
                        "tertiary" -> MaterialTheme.colorScheme.tertiary
                        "error" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.secondary
                    }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(entry.date, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    cat.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = catColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${entry.systolic} / ${entry.diastolic}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = catColor
                                )
                                if (entry.pulse != null) {
                                    Text(
                                        "${entry.pulse} bpm",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (entry.notes.isNotBlank()) {
                                Text(entry.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "No readings logged yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
