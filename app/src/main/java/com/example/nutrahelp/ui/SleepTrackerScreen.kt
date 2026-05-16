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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.FoodSearchResult
import com.example.nutrahelp.data.OpenFoodFactsRepository
import com.example.nutrahelp.data.SleepEntryEntity
import com.example.nutrahelp.viewmodel.SleepViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

private val qualityLabels = listOf("Poor", "Fair", "Good", "Great", "Excellent")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTrackerScreen(onBack: () -> Unit, vm: SleepViewModel = viewModel()) {
    val entries by vm.entries.collectAsState()
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    var date by remember { mutableStateOf(todayStr) }
    var bedtime by remember { mutableStateOf("") }
    var wakeTime by remember { mutableStateOf("") }
    var hoursSlept by remember { mutableStateOf("") }
    var quality by remember { mutableIntStateOf(2) }
    var notes by remember { mutableStateOf("") }
    var eveningFood by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FoodSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sleep Tracker") },
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
                        Text("Log Sleep", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it; formError = false },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            isError = formError && date.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = bedtime,
                                onValueChange = { bedtime = it },
                                label = { Text("Bedtime") },
                                placeholder = { Text("e.g. 10:30 PM") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = wakeTime,
                                onValueChange = { wakeTime = it },
                                label = { Text("Wake time") },
                                placeholder = { Text("e.g. 6:30 AM") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = hoursSlept,
                            onValueChange = { hoursSlept = it; formError = false },
                            label = { Text("Hours slept") },
                            placeholder = { Text("e.g. 7.5") },
                            singleLine = true,
                            isError = formError && hoursSlept.toFloatOrNull() == null,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Sleep quality", style = MaterialTheme.typography.labelMedium)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                qualityLabels.forEachIndexed { index, label ->
                                    SegmentedButton(
                                        selected = quality == index,
                                        onClick = { quality = index },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = qualityLabels.size),
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Evening food / snack (optional)", style = MaterialTheme.typography.labelMedium)
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
                                        Text("Tap to set as evening food:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        searchResults.forEach { result ->
                                            TextButton(
                                                onClick = {
                                                    eveningFood = result.name
                                                    searchResults = emptyList(); searchQuery = ""
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                    Text(result.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                                    val details = listOfNotNull(
                                                        result.caloriesPer100g?.let { "${it} kcal" },
                                                        result.sugarsPer100g?.let { "Sugar:${"%.1f".format(it)}g" },
                                                        result.carbsPer100g?.let { "Carbs:${"%.1f".format(it)}g" }
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
                                value = eveningFood,
                                onValueChange = { eveningFood = it },
                                label = { Text("Food or snack before bed") },
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

                        if (formError) {
                            Text(
                                "Please enter a valid date and hours slept.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                val h = hoursSlept.toFloatOrNull()
                                if (date.isBlank() || h == null) {
                                    formError = true
                                } else {
                                    val combinedNotes = listOfNotNull(
                                        eveningFood.trim().takeIf { it.isNotBlank() }?.let { "Evening food: $it" },
                                        notes.trim().takeIf { it.isNotBlank() }
                                    ).joinToString(" · ")
                                    vm.insert(SleepEntryEntity(
                                        date = date,
                                        bedtime = bedtime,
                                        wakeTime = wakeTime,
                                        hoursSlept = h,
                                        quality = quality,
                                        notes = combinedNotes
                                    ))
                                    bedtime = ""
                                    wakeTime = ""
                                    hoursSlept = ""
                                    quality = 2
                                    notes = ""
                                    eveningFood = ""
                                    searchQuery = ""
                                    searchResults = emptyList()
                                    date = todayStr
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Sleep Log")
                        }
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
                    val qualityColor = when (entry.quality) {
                        0, 1 -> MaterialTheme.colorScheme.error
                        2 -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.tertiary
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
                                    qualityLabels[entry.quality],
                                    style = MaterialTheme.typography.labelSmall,
                                    color = qualityColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    "%.1f hrs".format(entry.hoursSlept),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (entry.bedtime.isNotBlank() || entry.wakeTime.isNotBlank()) {
                                    Text(
                                        buildString {
                                            if (entry.bedtime.isNotBlank()) append(entry.bedtime)
                                            if (entry.bedtime.isNotBlank() && entry.wakeTime.isNotBlank()) append(" → ")
                                            if (entry.wakeTime.isNotBlank()) append(entry.wakeTime)
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
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
                        "No sleep logs yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
