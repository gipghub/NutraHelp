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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.FoodSearchResult
import com.example.nutrahelp.data.InflammationEntryEntity
import com.example.nutrahelp.data.OpenFoodFactsRepository
import com.example.nutrahelp.viewmodel.InflammationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.launch

private val inflammationSymptoms = listOf(
    "Joint Pain", "Swelling", "Fatigue", "Skin Redness", "Muscle Ache",
    "Brain Fog", "Digestive Issues", "Headache", "Stiffness", "Heat/Warmth"
)

private val inflammationSeverityLabels = listOf("Minimal", "Mild", "Moderate", "Significant", "Severe")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InflammationLogScreen(onBack: () -> Unit, vm: InflammationViewModel = viewModel()) {
    val locale = LocalConfiguration.current.locales[0]
    val today = remember(locale) { SimpleDateFormat("MMM d, yyyy", locale).format(Date()) }

    val entries by vm.entries.collectAsState()

    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    var selectedSeverity by remember { mutableIntStateOf(0) }
    var notes by remember { mutableStateOf("") }
    var foodTrigger by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FoodSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inflammation Log") },
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
            if (entries.isNotEmpty()) {
                item {
                    val latest = entries.first()
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Latest Entry", style = MaterialTheme.typography.titleSmall)
                            Text(latest.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                inflammationSeverityLabels[latest.severity],
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = when (latest.severity) {
                                    0 -> MaterialTheme.colorScheme.tertiary
                                    1, 2 -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.error
                                }
                            )
                            val symptomsSet = latest.symptoms.split(",").filter { it.isNotBlank() }.toSet()
                            if (symptomsSet.isNotEmpty()) {
                                Text(
                                    symptomsSet.joinToString(" · "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log Inflammation", style = MaterialTheme.typography.titleMedium)
                        Text(today, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Symptoms", style = MaterialTheme.typography.labelMedium)
                            if (formError && selectedSymptoms.isEmpty()) {
                                Text(
                                    "Select at least one symptom.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                inflammationSymptoms.forEach { symptom ->
                                    FilterChip(
                                        selected = symptom in selectedSymptoms,
                                        onClick = {
                                            selectedSymptoms = if (symptom in selectedSymptoms)
                                                selectedSymptoms - symptom
                                            else
                                                selectedSymptoms + symptom
                                            formError = false
                                        },
                                        label = { Text(symptom, style = MaterialTheme.typography.bodySmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Severity: ${inflammationSeverityLabels[selectedSeverity]}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                inflammationSeverityLabels.forEachIndexed { index, label ->
                                    SegmentedButton(
                                        selected = selectedSeverity == index,
                                        onClick = { selectedSeverity = index },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = inflammationSeverityLabels.size),
                                        label = { Text((index + 1).toString()) }
                                    )
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
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
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
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
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
                                    vm.insert(
                                        InflammationEntryEntity(
                                            date = today,
                                            symptoms = selectedSymptoms.joinToString(","),
                                            severity = selectedSeverity,
                                            notes = combinedNotes
                                        )
                                    )
                                    selectedSymptoms = setOf()
                                    selectedSeverity = 0
                                    notes = ""
                                    foodTrigger = ""
                                    searchQuery = ""
                                    searchResults = emptyList()
                                    formError = false
                                }
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
                        Text("History (${entries.size})", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { vm.deleteAll() }) { Text("Clear") }
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
                                    Text(
                                        inflammationSeverityLabels[entry.severity],
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = when (entry.severity) {
                                            0 -> MaterialTheme.colorScheme.tertiary
                                            1, 2 -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                    )
                                    Text(
                                        entry.date,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                val symptomsSet = entry.symptoms.split(",").filter { it.isNotBlank() }
                                if (symptomsSet.isNotEmpty()) {
                                    Text(
                                        symptomsSet.joinToString(", "),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (entry.notes.isNotEmpty()) {
                                    Text(
                                        entry.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(onClick = { vm.delete(entry) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "Log symptoms above to track inflammation patterns.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}