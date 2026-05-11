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
import androidx.compose.material3.CardDefaults
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

private val cravingTriggers = listOf("Stress", "Boredom", "Hunger", "Habit", "Emotions", "Other")
private val cravingIntensityLabels = listOf("Mild", "Moderate", "Strong", "Very Strong", "Intense")
private val cravingOutcomes = listOf("Resisted", "Partially", "Gave In")

private data class CravingEntry(
    val id: Long = System.nanoTime(),
    val time: String,
    val foodName: String,
    val intensity: Int,
    val trigger: String,
    val outcome: String,
    val notes: String
)

private fun outcomeColor(outcome: String) = when (outcome) {
    "Resisted" -> "tertiary"
    "Partially" -> "secondary"
    else -> "error"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CravingLogScreen(onBack: () -> Unit) {
    var foodName by remember { mutableStateOf("") }
    var selectedIntensity by remember { mutableIntStateOf(2) }
    var selectedTrigger by remember { mutableStateOf(cravingTriggers[0]) }
    var selectedOutcome by remember { mutableStateOf(cravingOutcomes[0]) }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<CravingEntry>()) }

    val todayEntries = run {
        val today = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())
        entries.filter { it.time.startsWith(today) }
    }
    val resistedCount = todayEntries.count { it.outcome == "Resisted" }
    val resistRate = if (todayEntries.isNotEmpty())
        (resistedCount * 100f / todayEntries.size).toInt() else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Craving Log") },
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
            if (todayEntries.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${todayEntries.size}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("cravings today", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            if (resistRate != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "$resistRate%",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (resistRate >= 50) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                                    )
                                    Text("resisted", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log a Craving", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = foodName,
                            onValueChange = { foodName = it; formError = false },
                            label = { Text("What are you craving?") },
                            singleLine = true,
                            isError = formError && foodName.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Intensity: ${cravingIntensityLabels[selectedIntensity]}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                cravingIntensityLabels.forEachIndexed { index, _ ->
                                    SegmentedButton(
                                        selected = selectedIntensity == index,
                                        onClick = { selectedIntensity = index },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = cravingIntensityLabels.size),
                                        label = { Text("${index + 1}") }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Trigger", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                cravingTriggers.forEach { trigger ->
                                    FilterChip(
                                        selected = selectedTrigger == trigger,
                                        onClick = { selectedTrigger = trigger },
                                        label = { Text(trigger, style = MaterialTheme.typography.bodySmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Outcome", style = MaterialTheme.typography.labelMedium)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                cravingOutcomes.forEachIndexed { index, outcome ->
                                    SegmentedButton(
                                        selected = selectedOutcome == outcome,
                                        onClick = { selectedOutcome = outcome },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = cravingOutcomes.size),
                                        label = { Text(outcome, style = MaterialTheme.typography.bodySmall) }
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

                        if (formError) {
                            Text(
                                "Enter what you're craving.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                if (foodName.isBlank()) {
                                    formError = true
                                } else {
                                    val time = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date())
                                    entries = listOf(
                                        CravingEntry(
                                            time = time,
                                            foodName = foodName.trim(),
                                            intensity = selectedIntensity,
                                            trigger = selectedTrigger,
                                            outcome = selectedOutcome,
                                            notes = notes.trim()
                                        )
                                    ) + entries
                                    foodName = ""
                                    selectedIntensity = 2
                                    selectedTrigger = cravingTriggers[0]
                                    selectedOutcome = cravingOutcomes[0]
                                    notes = ""
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Log Craving") }
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
                                    Text(entry.foodName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    Text(
                                        entry.outcome,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = when (entry.outcome) {
                                            "Resisted" -> MaterialTheme.colorScheme.tertiary
                                            "Partially" -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                    )
                                }
                                Text(
                                    "${cravingIntensityLabels[entry.intensity]} · ${entry.trigger} · ${entry.time}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
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
                        "Log cravings to track patterns and measure how GLP-1 is affecting your appetite.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
