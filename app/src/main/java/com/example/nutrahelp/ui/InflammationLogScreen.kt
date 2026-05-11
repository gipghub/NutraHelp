package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date

private val inflammationSymptoms = listOf(
    "Joint Pain", "Swelling", "Fatigue", "Skin Redness", "Muscle Ache",
    "Brain Fog", "Digestive Issues", "Headache", "Stiffness", "Heat/Warmth"
)

private val inflammationSeverityLabels = listOf("Minimal", "Mild", "Moderate", "Significant", "Severe")

private data class InflammationEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val symptoms: Set<String>,
    val severity: Int,
    val notes: String
)

private fun inflammationSeverityColor(severity: Int) = when (severity) {
    0 -> "tertiary"
    1, 2 -> "secondary"
    else -> "error"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InflammationLogScreen(onBack: () -> Unit) {
    val locale = LocalConfiguration.current.locales[0]
    val today = remember(locale) { SimpleDateFormat("MMM d, yyyy", locale).format(Date()) }

    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    var selectedSeverity by remember { mutableIntStateOf(0) }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<InflammationEntry>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inflammation Log") },
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
            if (entries.isNotEmpty()) {
                item {
                    val latest = entries.first()
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Latest Entry", style = MaterialTheme.typography.titleSmall)
                            Text(latest.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                inflammationSeverityLabels[latest.severity],
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = when (latest.severity) {
                                    0 -> MaterialTheme.colorScheme.tertiary
                                    1, 2 -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.error
                                }
                            )
                            if (latest.symptoms.isNotEmpty()) {
                                Text(
                                    latest.symptoms.joinToString(" · "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log Inflammation", style = MaterialTheme.typography.titleMedium)
                        Text(today, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Symptoms", style = MaterialTheme.typography.labelMedium)
                            if (formError && selectedSymptoms.isEmpty()) {
                                Text(
                                    "Select at least one symptom.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                inflammationSymptoms.forEach { symptom ->
                                    FilterChip(
                                        selected = symptom in selectedSymptoms,
                                        onClick = {
                                            selectedSymptoms = if (symptom in selectedSymptoms)
                                                selectedSymptoms - symptom
                                            else
                                                selectedSymptoms + symptom
                                            formError = false
                                        },
                                        label = { Text(symptom, style = MaterialTheme.typography.bodySmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Severity: ${inflammationSeverityLabels[selectedSeverity]}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                inflammationSeverityLabels.forEachIndexed { index, label ->
                                    SegmentedButton(
                                        selected = selectedSeverity == index,
                                        onClick = { selectedSeverity = index },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = inflammationSeverityLabels.size),
                                        label = { Text((index + 1).toString()) }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        Button(
                            onClick = {
                                if (selectedSymptoms.isEmpty()) {
                                    formError = true
                                } else {
                                    entries = listOf(
                                        InflammationEntry(
                                            date = today,
                                            symptoms = selectedSymptoms,
                                            severity = selectedSeverity,
                                            notes = notes.trim()
                                        )
                                    ) + entries
                                    selectedSymptoms = setOf()
                                    selectedSeverity = 0
                                    notes = ""
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Save Entry") }
                    }
                }
            }

            if (entries.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("History (${entries.size})", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { entries = listOf() }) { Text("Clear") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        inflammationSeverityLabels[entry.severity],
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = when (entry.severity) {
                                            0 -> MaterialTheme.colorScheme.tertiary
                                            1, 2 -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                    )
                                    Text(
                                        entry.date,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (entry.symptoms.isNotEmpty()) {
                                    Text(
                                        entry.symptoms.joinToString(", "),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (entry.notes.isNotEmpty()) {
                                    Text(
                                        entry.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(onClick = { entries = entries.filter { it.id != entry.id } }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "Log symptoms above to track inflammation patterns.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
