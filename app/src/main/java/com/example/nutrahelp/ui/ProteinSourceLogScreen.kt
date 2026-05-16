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
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.nutrahelp.data.FoodSearchResult
import com.example.nutrahelp.data.OpenFoodFactsRepository
import com.example.nutrahelp.data.ProteinEntryEntity
import com.example.nutrahelp.viewmodel.ProteinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

private val quickProteinFoods = listOf(
    "Chicken breast (4oz)" to 35f,
    "Greek yogurt (1 cup)" to 20f,
    "Eggs (2 large)" to 12f,
    "Cottage cheese (½ cup)" to 14f,
    "Salmon (4oz)" to 28f,
    "Tuna (3oz can)" to 22f,
    "Protein shake" to 25f,
    "Tofu (½ cup)" to 10f,
    "Lentils (½ cup)" to 9f,
    "Edamame (½ cup)" to 8f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProteinSourceLogScreen(onBack: () -> Unit, vm: ProteinViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    var dailyGoal by remember { mutableFloatStateOf(120f) }
    var goalInput by remember { mutableStateOf("120") }
    var customFood by remember { mutableStateOf("") }
    var customProtein by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FoodSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val totalProtein = entries.sumOf { it.proteinG.toDouble() }.toFloat()
    val progress = (totalProtein / dailyGoal).coerceAtMost(1f)
    val goalReached = totalProtein >= dailyGoal

    fun logFood(name: String, protein: Float) {
        vm.insert(ProteinEntryEntity(date = todayStr, foodName = name, proteinG = protein))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Protein Sources") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "%.0fg protein".format(totalProtein),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (goalReached) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                )
                                Text("of %.0fg daily goal".format(dailyGoal), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (goalReached) {
                                Text("Goal met!", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                            } else {
                                Text(
                                    "%.0fg to go".format(dailyGoal - totalProtein),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                        Text("${(progress * 100).toInt()}% of daily goal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = goalInput,
                                onValueChange = { goalInput = it },
                                label = { Text("Daily goal (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(onClick = {
                                goalInput.toFloatOrNull()?.let { if (it > 0) dailyGoal = it }
                            }) { Text("Set") }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Quick Add", style = MaterialTheme.typography.titleMedium)
                        quickProteinFoods.chunked(2).forEach { pair ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                pair.forEach { (name, protein) ->
                                    OutlinedButton(
                                        onClick = { logFood(name, protein) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(name, style = MaterialTheme.typography.bodySmall)
                                            Text("+%.0fg".format(protein), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                                if (pair.size == 1) androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                            }
                        }

                        HorizontalDivider()

                        Text("Search Food Database", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it; searchError = false },
                                label = { Text("Search Open Food Facts…") },
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
                                                customFood = result.name
                                                customProtein = result.proteinPer100g?.let { "%.1f".format(it) } ?: ""
                                                searchResults = emptyList(); searchQuery = ""
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Text(result.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                                val details = listOfNotNull(
                                                    result.proteinPer100g?.let { "${"%.1f".format(it)}g protein" },
                                                    result.caloriesPer100g?.let { "${it} kcal" }
                                                ).joinToString(" · ")
                                                if (details.isNotBlank()) Text(details, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                    }
                                }
                            }
                        }

                        HorizontalDivider()

                        Text("Custom food", style = MaterialTheme.typography.labelMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customFood,
                                onValueChange = { customFood = it },
                                label = { Text("Food name") },
                                singleLine = true,
                                modifier = Modifier.weight(2f)
                            )
                            OutlinedTextField(
                                value = customProtein,
                                onValueChange = { customProtein = it },
                                label = { Text("Protein (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Button(
                            onClick = {
                                val p = customProtein.toFloatOrNull()
                                if (customFood.isNotBlank() && p != null && p > 0) {
                                    logFood(customFood.trim(), p)
                                    customFood = ""; customProtein = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Add Food") }
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
                        Text("Today's Log", style = MaterialTheme.typography.titleMedium)
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
                            Text("+%.0fg".format(entry.proteinG), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
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
