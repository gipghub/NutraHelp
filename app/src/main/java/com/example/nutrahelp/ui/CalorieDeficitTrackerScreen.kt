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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.CalorieEntryEntity
import com.example.nutrahelp.data.FoodSearchResult
import com.example.nutrahelp.data.OpenFoodFactsRepository
import com.example.nutrahelp.viewmodel.CalorieViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieDeficitTrackerScreen(onBack: () -> Unit, vm: CalorieViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    var tdee by remember { mutableIntStateOf(2000) }
    var tdeeInput by remember { mutableStateOf("2000") }
    var deficitTarget by remember { mutableIntStateOf(500) }
    var deficitInput by remember { mutableStateOf("500") }

    var foodName by remember { mutableStateOf("") }
    var calorieInput by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FoodSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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

                        // Open Food Facts search
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it; searchError = false },
                                label = { Text("Search food database…") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSearching) {
                                CircularProgressIndicator(modifier = Modifier.padding(4.dp), strokeWidth = 2.dp)
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        if (searchQuery.isNotBlank()) {
                                            scope.launch {
                                                isSearching = true; searchError = false
                                                val results = OpenFoodFactsRepository.search(searchQuery)
                                                searchResults = results
                                                searchError = results.isEmpty()
                                                isSearching = false
                                            }
                                        }
                                    },
                                    enabled = searchQuery.isNotBlank()
                                ) { Text("Search") }
                            }
                        }
                        if (searchError) {
                            Text("No results found.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }
                        if (searchResults.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("Tap to fill (per 100g):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    searchResults.forEach { result ->
                                        TextButton(
                                            onClick = {
                                                foodName = result.name
                                                calorieInput = result.caloriesPer100g?.toString() ?: ""
                                                searchResults = emptyList(); searchQuery = ""
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Text(result.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                                val details = listOfNotNull(
                                                    result.caloriesPer100g?.let { "${it} kcal" },
                                                    result.proteinPer100g?.let { "${"%.1f".format(it)}g protein" }
                                                ).joinToString(" · ")
                                                if (details.isNotBlank()) Text(details, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                    }
                                }
                            }
                        }

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
                                    vm.insert(CalorieEntryEntity(date = todayStr, foodName = foodName.trim(), calories = cal))
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
                        OutlinedButton(onClick = { vm.deleteAll() }) { Text("Reset") }
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
                            IconButton(onClick = { vm.delete(entry) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
