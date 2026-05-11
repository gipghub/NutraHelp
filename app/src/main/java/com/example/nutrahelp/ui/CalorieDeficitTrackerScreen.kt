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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private data class CalorieEntry(
    val id: Long = System.nanoTime(),
    val foodName: String,
    val calories: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieDeficitTrackerScreen(onBack: () -> Unit) {
    var tdee by remember { mutableIntStateOf(2000) }
    var tdeeInput by remember { mutableStateOf("2000") }
    var deficitTarget by remember { mutableIntStateOf(500) }
    var deficitInput by remember { mutableStateOf("500") }

    var foodName by remember { mutableStateOf("") }
    var calorieInput by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<CalorieEntry>()) }

    val calorieGoal = (tdee - deficitTarget).coerceAtLeast(1200)
    val totalConsumed = entries.sumOf { it.calories }
    val remaining = calorieGoal - totalConsumed
    val progress = (totalConsumed.toFloat() / calorieGoal).coerceAtMost(1f)
    val overBudget = totalConsumed > calorieGoal

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calorie Deficit") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "$totalConsumed cal",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (overBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                                Text("consumed today", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    if (overBudget) "+${-remaining} over" else "$remaining left",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (overBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                                )
                                Text("goal: $calorieGoal cal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (overBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TDEE: $tdee", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Deficit: $deficitTarget cal/day", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("≈ ${(deficitTarget * 7 / 3500f * 10).toInt() / 10f} lbs/wk", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Log Food", style = MaterialTheme.typography.titleMedium)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = foodName,
                                onValueChange = { foodName = it; formError = false },
                                label = { Text("Food name") },
                                singleLine = true,
                                isError = formError && foodName.isBlank(),
                                modifier = Modifier.weight(2f)
                            )
                            OutlinedTextField(
                                value = calorieInput,
                                onValueChange = { calorieInput = it; formError = false },
                                label = { Text("Calories") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = formError && calorieInput.toIntOrNull() == null,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (formError) {
                            Text("Enter a food name and calorie count.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = {
                                val cal = calorieInput.toIntOrNull()
                                if (foodName.isBlank() || cal == null || cal <= 0) {
                                    formError = true
                                } else {
                                    entries = listOf(CalorieEntry(foodName = foodName.trim(), calories = cal)) + entries
                                    foodName = ""; calorieInput = ""; formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Add Food") }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Goals", style = MaterialTheme.typography.titleMedium)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = tdeeInput,
                                onValueChange = { tdeeInput = it },
                                label = { Text("TDEE (cal)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = deficitInput,
                                onValueChange = { deficitInput = it },
                                label = { Text("Deficit (cal)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Button(
                            onClick = {
                                tdeeInput.toIntOrNull()?.let { if (it > 0) tdee = it }
                                deficitInput.toIntOrNull()?.let { if (it >= 0) deficitTarget = it }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Update Goals") }
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
                        Text("Today's Food Log", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { entries = listOf() }) { Text("Reset") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(entry.foodName, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text("${entry.calories} cal", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
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
