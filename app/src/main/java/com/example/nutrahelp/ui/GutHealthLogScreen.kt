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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val gutSymptoms = listOf(
    "Bloating", "Nausea", "Constipation", "Diarrhea",
    "Cramping", "Heartburn", "Indigestion", "Gas",
    "Vomiting", "Reduced Appetite", "Stomach Pain"
)

private val gutSeverityLabels = listOf("Mild", "Moderate", "Significant", "Severe", "Very Severe")
private val gutTimeOptions = listOf("Morning", "Afternoon", "Evening", "Night")

private data class GutEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val timeOfDay: String,
    val symptoms: Set<String>,
    val severity: Int,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GutHealthLogScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()) }

    var selectedTimeOfDay by remember { mutableStateOf(gutTimeOptions[0]) }
    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    var severity by remember { mutableIntStateOf(0) }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<GutEntry>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gut Health Log") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("GLP-1 & Digestion", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "GLP-1 medications slow gastric emptying, which can cause nausea, constipation, and bloating — especially early on. Tracking symptoms helps identify patterns and discuss with your doctor.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log Symptoms", style = MaterialTheme.typography.titleMedium)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Time of day", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                gutTimeOptions.forEach { option ->
                                    FilterChip(
                                        selected = selectedTimeOfDay == option,
                                        onClick = { selectedTimeOfDay = option },
                                        label = { Text(option, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Symptoms (select all that apply)", style = MaterialTheme.typography.labelMedium)
                            if (formError && selectedSymptoms.isEmpty()) {
                                Text(
                                    "Select at least one symptom.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                gutSymptoms.forEach { symptom ->
                                    FilterChip(
                                        selected = symptom in selectedSymptoms,
                                        onClick = {
                                            selectedSymptoms = if (symptom in selectedSymptoms)
                                                selectedSymptoms - symptom
                                            else
                                                selectedSymptoms + symptom
                                            formError = false
                                        },
                                        label = { Text(symptom, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Severity — ${gutSeverityLabels[severity]}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                gutSeverityLabels.forEachIndexed { i, _ ->
                                    SegmentedButton(
                                        selected = severity == i,
                                        onClick = { severity = i },
                                        shape = SegmentedButtonDefaults.itemShape(index = i, count = gutSeverityLabels.size)
                                    ) { Text("${i + 1}", style = MaterialTheme.typography.labelSmall) }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            placeholder = { Text("e.g. Happened after eating fast food") },
                            singleLine = false,
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (selectedSymptoms.isEmpty()) {
                                    formError = true
                                } else {
                                    entries = listOf(
                                        GutEntry(
                                            date = todayStr,
                                            timeOfDay = selectedTimeOfDay,
                                            symptoms = selectedSymptoms,
                                            severity = severity,
                                            notes = notes.trim()
                                        )
                                    ) + entries
                                    selectedSymptoms = setOf()
                                    severity = 0
                                    notes = ""
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Log Symptoms") }
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
                        Text("History", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { entries = listOf() }) { Text("Reset") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${entry.date} · ${entry.timeOfDay}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    gutSeverityLabels[entry.severity],
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when (entry.severity) {
                                        0 -> MaterialTheme.colorScheme.tertiary
                                        1, 2 -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.error
                                    }
                                )
                            }
                            Text(
                                entry.symptoms.joinToString(", "),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (entry.notes.isNotBlank()) {
                                Text(
                                    entry.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "No symptoms logged yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
