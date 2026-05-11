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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class WeightEntry(val date: String, val weight: Float, val unit: String)

@Composable
fun ProgressScreen(
    onNavigateToStats: () -> Unit = {},
    onNavigateToExercise: () -> Unit = {},
    onNavigateToLab: () -> Unit = {},
    onNavigateToGoals: () -> Unit = {},
    onNavigateToMeasurements: () -> Unit = {}
) {
    var weightInput by remember { mutableStateOf("") }
    var useKg by remember { mutableStateOf(true) }
    var entries by remember { mutableStateOf(listOf<WeightEntry>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val unit = if (useKg) "kg" else "lbs"
    val startWeight = entries.lastOrNull()?.weight
    val currentWeight = entries.firstOrNull()?.weight
    val totalLoss = if (startWeight != null && currentWeight != null) startWeight - currentWeight else null

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Weight Progress", style = MaterialTheme.typography.headlineSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToStats, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Text("BMI & Stats", modifier = Modifier.padding(start = 4.dp))
                    }
                    OutlinedButton(onClick = onNavigateToExercise, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null)
                        Text("Exercise", modifier = Modifier.padding(start = 4.dp))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToLab, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Science, contentDescription = null)
                        Text("Lab Results", modifier = Modifier.padding(start = 4.dp))
                    }
                    OutlinedButton(onClick = onNavigateToGoals, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Flag, contentDescription = null)
                        Text("Goals", modifier = Modifier.padding(start = 4.dp))
                    }
                }
                OutlinedButton(onClick = onNavigateToMeasurements, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Straighten, contentDescription = null)
                    Text("Body Measurements", modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Log Today's Weight", style = MaterialTheme.typography.titleMedium)

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = useKg,
                            onClick = { useKg = true },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) { Text("kg") }
                        SegmentedButton(
                            selected = !useKg,
                            onClick = { useKg = false },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) { Text("lbs") }
                    }

                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it; errorMessage = null },
                        label = { Text("Weight ($unit)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = errorMessage != null,
                        supportingText = errorMessage?.let { msg -> { Text(msg) } },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val w = weightInput.toFloatOrNull()
                            if (w == null || w <= 0f) {
                                errorMessage = "Enter a valid weight"
                            } else {
                                val today = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
                                entries = listOf(WeightEntry(today, w, unit)) + entries
                                weightInput = ""
                                errorMessage = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save")
                    }
                }
            }
        }

        if (entries.size >= 2 && startWeight != null && currentWeight != null) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "%.1f %s".format(startWeight, unit),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text("Starting", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "%.1f %s".format(currentWeight, unit),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Current", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (totalLoss != null && totalLoss > 0f) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "−%.1f %s".format(totalLoss, unit),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text("Lost", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        if (entries.isNotEmpty()) {
            item {
                Text("History", style = MaterialTheme.typography.titleMedium)
            }
            items(entries) { entry ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(entry.date, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "%.1f %s".format(entry.weight, entry.unit),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        } else {
            item {
                Text(
                    "Log your first weight entry to start tracking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
