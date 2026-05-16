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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.MindfulMealEntryEntity
import com.example.nutrahelp.viewmodel.MindfulMealViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
private val distractionTags = listOf("Phone", "TV", "Work", "Reading", "Driving", "None")
private val hungerLabels = listOf("1", "2", "3", "4", "5")
private val fullnessLabels = listOf("1", "2", "3", "4", "5")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MindfulEatingLogScreen(onBack: () -> Unit, vm: MindfulMealViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    var date by remember { mutableStateOf(todayStr) }
    var mealType by remember { mutableStateOf(mealTypes[0]) }
    var mealExpanded by remember { mutableStateOf(false) }
    var hungerBefore by remember { mutableIntStateOf(2) }
    var fullnessAfter by remember { mutableIntStateOf(3) }
    var eatingMins by remember { mutableStateOf("") }
    var distractions by remember { mutableStateOf(setOf<String>()) }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mindful Eating") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Log Meal", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(expanded = mealExpanded, onExpandedChange = { mealExpanded = it }) {
                            OutlinedTextField(
                                value = mealType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Meal type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(mealExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = mealExpanded, onDismissRequest = { mealExpanded = false }) {
                                mealTypes.forEach { t ->
                                    DropdownMenuItem(text = { Text(t) }, onClick = { mealType = t; mealExpanded = false })
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Hunger before (1=not hungry, 5=very hungry)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                hungerLabels.forEachIndexed { index, label ->
                                    SegmentedButton(
                                        selected = hungerBefore == index,
                                        onClick = { hungerBefore = index },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = hungerLabels.size),
                                        label = { Text(label) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Fullness after (1=still hungry, 5=very full)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                fullnessLabels.forEachIndexed { index, label ->
                                    SegmentedButton(
                                        selected = fullnessAfter == index,
                                        onClick = { fullnessAfter = index },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = fullnessLabels.size),
                                        label = { Text(label) }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = eatingMins,
                            onValueChange = { eatingMins = it },
                            label = { Text("Time spent eating (minutes, optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Distractions", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                distractionTags.forEach { tag ->
                                    FilterChip(
                                        selected = tag in distractions,
                                        onClick = {
                                            distractions = if (tag in distractions) distractions - tag else distractions + tag
                                        },
                                        label = { Text(tag) }
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
                                vm.insert(MindfulMealEntryEntity(
                                    date = date.ifBlank { todayStr },
                                    mealType = mealType,
                                    hungerBefore = hungerBefore,
                                    fullnessAfter = fullnessAfter,
                                    eatingMins = eatingMins.trim(),
                                    distractions = distractions.joinToString(","),
                                    notes = notes.trim()
                                ))
                                hungerBefore = 2; fullnessAfter = 3; eatingMins = ""; distractions = setOf(); notes = ""; date = todayStr
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Save Meal Log") }
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
                    val entryDistractions = entry.distractions.split(",").filter { it.isNotBlank() }.toSet()
                    val mindful = entry.fullnessAfter in 2..3 && entry.hungerBefore in 1..3 && "None" in entryDistractions
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${entry.date} · ${entry.mealType}", style = MaterialTheme.typography.titleSmall)
                                if (mindful) {
                                    Text("Mindful", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("Hunger: ${entry.hungerBefore + 1}/5", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Fullness: ${entry.fullnessAfter + 1}/5", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (entry.eatingMins.isNotBlank()) {
                                    Text("${entry.eatingMins} min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            if (entryDistractions.isNotEmpty() && "None" !in entryDistractions) {
                                Text("Distractions: ${entryDistractions.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (entry.notes.isNotBlank()) {
                                Text(entry.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("No meals logged yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
