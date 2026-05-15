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

private val gutSymptoms = listOf(
    "Bloating", "Nausea", "Constipation", "Diarrhea",
    "Cramping", "Heartburn", "Indigestion", "Gas",
    "Vomiting", "Reduced Appetite", "Stomach Pain"
)

private val gutSeverityLabels = listOf("Mild", "Moderate", "Significant", "Severe", "Very Severe")
private val gutTimeOptions = listOf("Morning", "Afternoon", "Evening", "Night")

private data class GutEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val timeOfDay: String,
    val symptoms: Set<String>,
    val severity: Int,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GutHealthLogScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()) }

    var selectedTimeOfDay by remember { mutableStateOf(gutTimeOptions[0]) }
    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    var severity by remember { mutableIntStateOf(0) }
    var notes by remember { mutableStateOf("") }
    var foodTrigger by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<GutEntry>()) }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FoodSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gut Health Log") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("GLP-1 & Digestion", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "GLP-1 medications slow gastric emptying, which can cause nausea, constipation, and bloating — especially early on. Tracking symptoms helps identify patterns and discuss with your doctor.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log Symptoms", style = MaterialTheme.typography.titleMedium)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Time of day", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                gutTimeOptions.forEach { option ->
                                    FilterChip(
                                        selected = selectedTimeOfDay == option,
                                        onClick = { selectedTimeOfDay = option },
                                        label = { Text(option, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Symptoms (select all that apply)", style = MaterialTheme.typography.labelMedium)
                            if (formError && selectedSymptoms.isEmpty()) {
                                Text(
                                    "Select at least one symptom.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                gutSymptoms.forEach { symptom ->
                                    FilterChip(
                                        selected = symptom in selectedSymptoms,
                                        onClick = {
                                            selectedSymptoms = if (symptom in selectedSymptoms)
                                                selectedSymptoms - symptom
                                            else
                                                selectedSymptoms + symptom
                                            formError = false
                                        },
                                        label = { Text(symptom, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Severity — ${gutSeverityLabels[severity]}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                gutSeverityLabels.forEachIndexed { i, _ ->
                                    SegmentedButton(
                                        selected = severity == i,
                                        onClick = { severity = i },
                                        shape = SegmentedButtonDefaults.itemShape(index = i, count = gutSeverityLabels.size)
                                    ) { Text("${i + 1}", style = MaterialTheme.typography.labelSmall) }
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Food trigger (optional)", style = MaterialTheme.typography.labelMedium)
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
                                        Text("Tap to set as food trigger:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        searchResults.forEach { result ->
                                            TextButton(
                                                onClick = {
                                                    foodTrigger = result.name
                                                    searchResults = emptyList(); searchQuery = ""
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                    Text(result.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                                    val details = listOfNotNull(
                                                        result.caloriesPer100g?.let { "${it} kcal" },
                                                        result.fiberPer100g?.let { "Fiber:${"%.1f".format(it)}g" }
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
                                label = { Text("Food that may have triggered this") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            placeholder = { Text("e.g. Happened 30 min after eating") },
                            singleLine = false,
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (selectedSymptoms.isEmpty()) {
                                    formError = true
                                } else {
                                    val combinedNotes = listOfNotNull(
                                        foodTrigger.trim().takeIf { it.isNotBlank() }?.let { "Trigger: $it" },
                                        notes.trim().takeIf { it.isNotBlank() }
                                    ).joinToString(" · ")
                                    entries = listOf(
                                        GutEntry(
                                            date = todayStr,
                                            timeOfDay = selectedTimeOfDay,
                                            symptoms = selectedSymptoms,
                                            severity = severity,
                                            notes = combinedNotes
                                        )
                                    ) + entries
                                    selectedSymptoms = setOf()
                                    severity = 0
                                    notes = ""
                                    foodTrigger = ""
                                    searchQuery = ""
                                    searchResults = emptyList()
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Log Symptoms") }
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
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${entry.date} · ${entry.timeOfDay}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    gutSeverityLabels[entry.severity],
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when (entry.severity) {
                                        0 -> MaterialTheme.colorScheme.tertiary
                                        1, 2 -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.error
                                    }
                                )
                            }
                            Text(
                                entry.symptoms.joinToString(", "),
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
                    }
                }
            } else {
                item {
                    Text(
                        "No symptoms logged yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
