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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private data class MacroEntry(
    val id: Long = System.nanoTime(),
    val foodName: String,
    val carbsG: Float,
    val proteinG: Float,
    val fatG: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroTrackerScreen(onBack: () -> Unit) {
    var carbGoal by remember { mutableFloatStateOf(150f) }
    var proteinGoal by remember { mutableFloatStateOf(120f) }
    var fatGoal by remember { mutableFloatStateOf(65f) }

    var carbGoalInput by remember { mutableStateOf("150") }
    var proteinGoalInput by remember { mutableStateOf("120") }
    var fatGoalInput by remember { mutableStateOf("65") }

    var foodName by remember { mutableStateOf("") }
    var carbInput by remember { mutableStateOf("") }
    var proteinInput by remember { mutableStateOf("") }
    var fatInput by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }

    var entries by remember { mutableStateOf(listOf<MacroEntry>()) }

    val totalCarbs = entries.sumOf { it.carbsG.toDouble() }.toFloat()
    val totalProtein = entries.sumOf { it.proteinG.toDouble() }.toFloat()
    val totalFat = entries.sumOf { it.fatG.toDouble() }.toFloat()

    fun progress(current: Float, goal: Float) = if (goal > 0) (current / goal).coerceAtMost(1f) else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Macro Tracker") },
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
                        Text("Today's Macros", style = MaterialTheme.typography.titleMedium)

                        MacroBar(
                            label = "Carbs",
                            current = totalCarbs,
                            goal = carbGoal,
                            progress = progress(totalCarbs, carbGoal),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        MacroBar(
                            label = "Protein",
                            current = totalProtein,
                            goal = proteinGoal,
                            progress = progress(totalProtein, proteinGoal),
                            color = MaterialTheme.colorScheme.primary
                        )
                        MacroBar(
                            label = "Fat",
                            current = totalFat,
                            goal = fatGoal,
                            progress = progress(totalFat, fatGoal),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Log Food", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = foodName,
                            onValueChange = { foodName = it; formError = false },
                            label = { Text("Food name") },
                            singleLine = true,
                            isError = formError && foodName.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = carbInput,
                                onValueChange = { carbInput = it; formError = false },
                                label = { Text("Carbs (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = proteinInput,
                                onValueChange = { proteinInput = it; formError = false },
                                label = { Text("Protein (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = fatInput,
                                onValueChange = { fatInput = it; formError = false },
                                label = { Text("Fat (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (formError) {
                            Text(
                                "Enter a food name and at least one macro value.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                val c = carbInput.toFloatOrNull() ?: 0f
                                val p = proteinInput.toFloatOrNull() ?: 0f
                                val f = fatInput.toFloatOrNull() ?: 0f
                                if (foodName.isBlank() || (c == 0f && p == 0f && f == 0f)) {
                                    formError = true
                                } else {
                                    entries = listOf(
                                        MacroEntry(foodName = foodName.trim(), carbsG = c, proteinG = p, fatG = f)
                                    ) + entries
                                    foodName = ""; carbInput = ""; proteinInput = ""; fatInput = ""
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Log Food") }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Daily Goals", style = MaterialTheme.typography.titleMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = carbGoalInput,
                                onValueChange = { carbGoalInput = it },
                                label = { Text("Carbs (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = proteinGoalInput,
                                onValueChange = { proteinGoalInput = it },
                                label = { Text("Protein (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = fatGoalInput,
                                onValueChange = { fatGoalInput = it },
                                label = { Text("Fat (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Button(
                            onClick = {
                                carbGoalInput.toFloatOrNull()?.let { if (it > 0) carbGoal = it }
                                proteinGoalInput.toFloatOrNull()?.let { if (it > 0) proteinGoal = it }
                                fatGoalInput.toFloatOrNull()?.let { if (it > 0) fatGoal = it }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Update Goals") }
                    }
                }
            }

            if (entries.isNotEmpty()) {
                item {
                    Text("Food Log", style = MaterialTheme.typography.titleMedium)
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
                                Text(entry.foodName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(
                                    buildString {
                                        if (entry.carbsG > 0) append("C: %.1fg  ".format(entry.carbsG))
                                        if (entry.proteinG > 0) append("P: %.1fg  ".format(entry.proteinG))
                                        if (entry.fatG > 0) append("F: %.1fg".format(entry.fatG))
                                    }.trim(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { entries = entries.filter { it.id != entry.id } }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroBar(label: String, current: Float, goal: Float, progress: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                "%.0fg / %.0fg".format(current, goal),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = color
        )
    }
}
