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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nutrahelp.data.FoodSearchResult
import com.example.nutrahelp.data.OpenFoodFactsRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

private val timeOfDayOptions = listOf("Morning", "Afternoon", "Evening", "Night")

private fun energyLabel(level: Int): String = when (level) {
    in 1..2 -> "Exhausted"
    in 3..4 -> "Low"
    in 5..6 -> "Moderate"
    in 7..8 -> "Good"
    else -> "High Energy"
}

private data class EnergyEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val timeOfDay: String,
    val energyLevel: Int,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EnergyLevelLogScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()) }

    var selectedTimeOfDay by remember { mutableStateOf(timeOfDayOptions[0]) }
    var energyLevel by remember { mutableIntStateOf(5) }
    var notes by remember { mutableStateOf("") }
    var foodTrigger by remember { mutableStateOf("") }
    var entries by remember { mutableStateOf(listOf<EnergyEntry>()) }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FoodSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val todayEntries = entries.filter { it.date == todayStr }
    val avgToday = if (todayEntries.isNotEmpty()) todayEntries.map { it.energyLevel }.average() else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Energy Level") },
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
            if (avgToday != null) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "%.1f".format(avgToday),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        avgToday < 4 -> MaterialTheme.colorScheme.error
                                        avgToday < 7 -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.tertiary
                                    }
                                )
                                Text("today's avg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    energyLabel(avgToday.toInt()),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when {
                                        avgToday < 4 -> MaterialTheme.colorScheme.error
                                        avgToday < 7 -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.tertiary
                                    }
                                )
                                Text("${todayEntries.size} readings today", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log Energy Level", style = MaterialTheme.typography.titleMedium)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Time of day", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                timeOfDayOptions.forEach { option ->
                                    FilterChip(
                                        selected = selectedTimeOfDay == option,
                                        onClick = { selectedTimeOfDay = option },
                                        label = { Text(option, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Energy level — $energyLevel/10 (${energyLabel(energyLevel)})",
                                style = MaterialTheme.typography.labelMedium
                            )
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                (1..10).forEach { i ->
                                    SegmentedButton(
                                        selected = energyLevel == i,
                                        onClick = { energyLevel = i },
                                        shape = SegmentedButtonDefaults.itemShape(index = i - 1, count = 10)
                                    ) { Text("$i", style = MaterialTheme.typography.labelSmall) }
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Food / meal (optional)", style = MaterialTheme.typography.labelMedium)
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
                                        Text("Tap to set as food / meal:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        searchResults.forEach { result ->
                                            TextButton(
                                                onClick = {
                                                    foodTrigger = result.name
                                                    searchResults = emptyList(); searchQuery = ""
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                    Text(result.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                                    val details = listOfNotNull(
                                                        result.caloriesPer100g?.let { "${it} kcal" },
                                                        result.carbsPer100g?.let { "Carbs:${"%.1f".format(it)}g" },
                                                        result.proteinPer100g?.let { "P:${"%.1f".format(it)}g" }
                                                    ).joinToString(" · ")
                                                    if (details.isNotBlank()) Text(details, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                        }
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = foodTrigger,
                                onValueChange = { foodTrigger = it },
                                label = { Text("Food or meal that may have affected energy") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
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
                                val combinedNotes = listOfNotNull(
                                    foodTrigger.trim().takeIf { it.isNotBlank() }?.let { "Food: $it" },
                                    notes.trim().takeIf { it.isNotBlank() }
                                ).joinToString(" · ")
                                entries = (listOf(
                                    EnergyEntry(
                                        date = todayStr,
                                        timeOfDay = selectedTimeOfDay,
                                        energyLevel = energyLevel,
                                        notes = combinedNotes
                                    )
                                ) + entries).sortedByDescending { it.id }
                                notes = ""
                                energyLevel = 5
                                foodTrigger = ""
                                searchQuery = ""
                                searchResults = emptyList()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Log Energy") }
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
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${entry.date} · ${entry.timeOfDay}",
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
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "${entry.energyLevel}/10",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        entry.energyLevel <= 3 -> MaterialTheme.colorScheme.error
                                        entry.energyLevel <= 6 -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.tertiary
                                    }
                                )
                                Text(
                                    energyLabel(entry.energyLevel),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "No energy readings yet — log your first one!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
