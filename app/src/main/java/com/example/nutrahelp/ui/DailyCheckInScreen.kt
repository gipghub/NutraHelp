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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.CheckInEntryEntity
import com.example.nutrahelp.viewmodel.CheckInViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ciMoodLabels = listOf("Very Low", "Low", "Neutral", "Good", "Great")
private val ciEnergyLabels = listOf("Exhausted", "Low", "Moderate", "Good", "Energized")
private val ciHungerLabels = listOf("Very Hungry", "Hungry", "Neutral", "Satisfied", "Not Hungry")
private val ciSleepLabels = listOf("Poor", "Fair", "Good", "Great", "Excellent")

private fun overallScore(mood: Int, energy: Int, hunger: Int, sleep: Int): Float =
    (mood + energy + hunger + sleep) / 4f + 1f

private fun overallLabel(score: Float): String = when {
    score < 2f -> "Tough day"
    score < 3f -> "Below average"
    score < 4f -> "Okay"
    score < 4.5f -> "Good"
    else -> "Great"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckInScreen(onBack: () -> Unit, vm: CheckInViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    var mood by remember { mutableIntStateOf(2) }
    var energy by remember { mutableIntStateOf(2) }
    var hunger by remember { mutableIntStateOf(2) }
    var sleep by remember { mutableIntStateOf(2) }
    var notes by remember { mutableStateOf("") }

    val latestEntry = entries.firstOrNull()

    @Composable
    fun MetricRow(label: String, selected: Int, onSelect: (Int) -> Unit, labels: List<String>) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("$label — ${labels[selected]}", style = MaterialTheme.typography.labelMedium)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                labels.forEachIndexed { i, _ ->
                    SegmentedButton(
                        selected = selected == i,
                        onClick = { onSelect(i) },
                        shape = SegmentedButtonDefaults.itemShape(index = i, count = labels.size)
                    ) { Text("${i + 1}", style = MaterialTheme.typography.labelSmall) }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Check-in") },
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
            if (latestEntry != null) {
                item {
                    val score = overallScore(latestEntry.mood, latestEntry.energy, latestEntry.hunger, latestEntry.sleep)
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Last Check-in", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        overallLabel(score),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            score < 2.5f -> MaterialTheme.colorScheme.error
                                            score < 3.5f -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.tertiary
                                        }
                                    )
                                }
                                Text(
                                    "${latestEntry.date}\n${latestEntry.time}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                listOf(
                                    "Mood" to ciMoodLabels[latestEntry.mood],
                                    "Energy" to ciEnergyLabels[latestEntry.energy],
                                    "Hunger" to ciHungerLabels[latestEntry.hunger],
                                    "Sleep" to ciSleepLabels[latestEntry.sleep]
                                ).forEach { (metric, value) ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                        Text(metric, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("How are you today?", style = MaterialTheme.typography.titleMedium)

                        MetricRow("Mood", mood, { mood = it }, ciMoodLabels)
                        MetricRow("Energy", energy, { energy = it }, ciEnergyLabels)
                        MetricRow("Hunger control", hunger, { hunger = it }, ciHungerLabels)
                        MetricRow("Sleep last night", sleep, { sleep = it }, ciSleepLabels)

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            singleLine = false,
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                                vm.insert(CheckInEntryEntity(
                                    date = todayStr,
                                    time = time,
                                    mood = mood,
                                    energy = energy,
                                    hunger = hunger,
                                    sleep = sleep,
                                    notes = notes.trim()
                                ))
                                mood = 2; energy = 2; hunger = 2; sleep = 2; notes = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Submit Check-in") }
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
                        OutlinedButton(onClick = { vm.deleteAll() }) { Text("Reset") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    val score = overallScore(entry.mood, entry.energy, entry.hunger, entry.sleep)
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    overallLabel(score),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when {
                                        score < 2.5f -> MaterialTheme.colorScheme.error
                                        score < 3.5f -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.tertiary
                                    }
                                )
                                Text(
                                    "${entry.date} · ${entry.time}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                listOf(
                                    "Mood" to ciMoodLabels[entry.mood],
                                    "Energy" to ciEnergyLabels[entry.energy],
                                    "Hunger" to ciHungerLabels[entry.hunger],
                                    "Sleep" to ciSleepLabels[entry.sleep]
                                ).forEach { (metric, value) ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                        Text(metric, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            if (entry.notes.isNotBlank()) {
                                Text(entry.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}