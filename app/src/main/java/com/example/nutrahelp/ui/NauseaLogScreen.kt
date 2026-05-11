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

private val nauseaSeverityLabels = listOf("Mild", "Moderate", "Significant", "Severe", "Very Severe")
private val nauseaTimeOptions = listOf("Morning", "Afternoon", "Evening", "Night")
private val nauseaTriggers = listOf(
    "After eating", "Medication dose", "Empty stomach",
    "Movement", "Smell/Taste", "Stress", "Unknown", "Other"
)
private val nauseaRemedies = listOf(
    "Ginger", "Crackers/dry food", "Small sips of water",
    "Rest", "Fresh air", "Peppermint", "Anti-nausea med", "Nothing helped"
)
private val nauseaDurations = listOf("<30 min", "30–120 min", ">2 hours")

private data class NauseaEntry(
    val id: Long = System.nanoTime(),
    val dateTime: String,
    val timeOfDay: String,
    val severity: Int,
    val triggers: Set<String>,
    val remedies: Set<String>,
    val duration: String,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NauseaLogScreen(onBack: () -> Unit) {
    val locale = LocalConfiguration.current.locales[0]
    val dateFormat = remember(locale) { SimpleDateFormat("MMM d, h:mm a", locale) }

    var selectedSeverity by remember { mutableIntStateOf(0) }
    var selectedTimeOfDay by remember { mutableStateOf(nauseaTimeOptions[0]) }
    var selectedTriggers by remember { mutableStateOf(setOf<String>()) }
    var selectedRemedies by remember { mutableStateOf(setOf<String>()) }
    var selectedDuration by remember { mutableStateOf(nauseaDurations[0]) }
    var notes by remember { mutableStateOf("") }
    var entries by remember { mutableStateOf(listOf<NauseaEntry>()) }

    val topTrigger = entries
        .flatMap { it.triggers }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }?.key

    val topRemedy = entries
        .flatMap { it.remedies }
        .filter { it != "Nothing helped" }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }?.key

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nausea Log") },
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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Patterns", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Episodes logged", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${entries.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                if (topTrigger != null) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Top trigger", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(topTrigger, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    }
                                }
                                if (topRemedy != null) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Top remedy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(topRemedy, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log Episode", style = MaterialTheme.typography.titleMedium)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Severity: ${nauseaSeverityLabels[selectedSeverity]}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                nauseaSeverityLabels.forEachIndexed { index, _ ->
                                    SegmentedButton(
                                        selected = selectedSeverity == index,
                                        onClick = { selectedSeverity = index },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = nauseaSeverityLabels.size),
                                        label = { Text("${index + 1}") }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Time of day", style = MaterialTheme.typography.labelMedium)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                nauseaTimeOptions.forEachIndexed { index, label ->
                                    SegmentedButton(
                                        selected = selectedTimeOfDay == label,
                                        onClick = { selectedTimeOfDay = label },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = nauseaTimeOptions.size),
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Duration", style = MaterialTheme.typography.labelMedium)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                nauseaDurations.forEachIndexed { index, label ->
                                    SegmentedButton(
                                        selected = selectedDuration == label,
                                        onClick = { selectedDuration = label },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = nauseaDurations.size),
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Triggers (select all that apply)", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                nauseaTriggers.forEach { trigger ->
                                    FilterChip(
                                        selected = trigger in selectedTriggers,
                                        onClick = {
                                            selectedTriggers = if (trigger in selectedTriggers)
                                                selectedTriggers - trigger else selectedTriggers + trigger
                                        },
                                        label = { Text(trigger, style = MaterialTheme.typography.bodySmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("What helped? (select all that apply)", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                nauseaRemedies.forEach { remedy ->
                                    FilterChip(
                                        selected = remedy in selectedRemedies,
                                        onClick = {
                                            selectedRemedies = if (remedy in selectedRemedies)
                                                selectedRemedies - remedy else selectedRemedies + remedy
                                        },
                                        label = { Text(remedy, style = MaterialTheme.typography.bodySmall) }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                entries = listOf(
                                    NauseaEntry(
                                        dateTime = dateFormat.format(Date()),
                                        timeOfDay = selectedTimeOfDay,
                                        severity = selectedSeverity,
                                        triggers = selectedTriggers,
                                        remedies = selectedRemedies,
                                        duration = selectedDuration,
                                        notes = notes.trim()
                                    )
                                ) + entries
                                selectedSeverity = 0
                                selectedTimeOfDay = nauseaTimeOptions[0]
                                selectedTriggers = setOf()
                                selectedRemedies = setOf()
                                selectedDuration = nauseaDurations[0]
                                notes = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Save Episode") }
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
                                        nauseaSeverityLabels[entry.severity],
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = when (entry.severity) {
                                            0 -> MaterialTheme.colorScheme.tertiary
                                            1, 2 -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                    )
                                    Text("· ${entry.duration}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    "${entry.dateTime} · ${entry.timeOfDay}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (entry.triggers.isNotEmpty()) {
                                    Text(
                                        "Triggers: ${entry.triggers.joinToString(", ")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (entry.remedies.isNotEmpty()) {
                                    Text(
                                        "Helped: ${entry.remedies.joinToString(", ")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (entry.notes.isNotEmpty()) {
                                    Text(entry.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        "Log nausea episodes to find patterns in triggers and remedies.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
