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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.StressEntryEntity
import com.example.nutrahelp.viewmodel.StressViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val stressTriggers = listOf("Work", "Health", "Family", "Sleep", "Food", "Exercise", "Social", "Financial")

private fun stressLabel(level: Int) = when (level) {
    in 1..3 -> "Low"
    in 4..6 -> "Moderate"
    in 7..8 -> "High"
    else -> "Very High"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StressTrackerScreen(onBack: () -> Unit, vm: StressViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    var date by remember { mutableStateOf(todayStr) }
    var level by remember { mutableIntStateOf(4) }
    var selectedTriggers by remember { mutableStateOf(setOf<String>()) }
    var notes by remember { mutableStateOf("") }

    val levels = (1..10).toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stress Tracker") },
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Log Stress", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Stress level", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    "$level/10 — ${stressLabel(level)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = stressColor(level, MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.error),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                levels.forEachIndexed { index, l ->
                                    SegmentedButton(
                                        selected = level == l,
                                        onClick = { level = l },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = levels.size),
                                        label = { Text("$l", style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Triggers (optional)", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                stressTriggers.forEach { trigger ->
                                    FilterChip(
                                        selected = trigger in selectedTriggers,
                                        onClick = {
                                            selectedTriggers = if (trigger in selectedTriggers)
                                                selectedTriggers - trigger else selectedTriggers + trigger
                                        },
                                        label = { Text(trigger) }
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
                                vm.insert(
                                    StressEntryEntity(
                                        date = date.ifBlank { todayStr },
                                        level = level,
                                        triggers = selectedTriggers.joinToString(","),
                                        notes = notes.trim()
                                    )
                                )
                                level = 4; selectedTriggers = setOf(); notes = ""; date = todayStr
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
                        Text("History", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { vm.deleteAll() }) { Text("Clear") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    val catColor: @Composable () -> Color = {
                        when {
                            entry.level <= 3 -> MaterialTheme.colorScheme.tertiary
                            entry.level <= 6 -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.error
                        }
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
                                    "${entry.level}/10 · ${stressLabel(entry.level)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = catColor(),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            val triggersSet = entry.triggers.split(",").filter { it.isNotBlank() }
                            if (triggersSet.isNotEmpty()) {
                                Text(
                                    triggersSet.joinToString(", "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
                        "No stress entries logged yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun stressColor(level: Int, low: Color, mid: Color, high: Color): Color = when {
    level <= 3 -> low
    level <= 6 -> mid
    else -> high
}