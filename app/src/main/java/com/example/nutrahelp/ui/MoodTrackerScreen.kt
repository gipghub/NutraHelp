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
import com.example.nutrahelp.data.MoodEntryEntity
import com.example.nutrahelp.data.OpenFoodFactsRepository
import com.example.nutrahelp.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

private val moodLabels = listOf("Very Low", "Low", "Neutral", "Good", "Great")
private val emotionTags = listOf(
    "Happy", "Anxious", "Calm", "Motivated", "Irritable",
    "Sad", "Overwhelmed", "Content", "Hopeful", "Tired"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MoodTrackerScreen(onBack: () -> Unit, vm: MoodViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    var moodLevel by remember { mutableIntStateOf(2) }
    var selectedEmotions by remember { mutableStateOf(setOf<String>()) }
    var notes by remember { mutableStateOf("") }
    var recentFood by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FoodSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val avgMood = if (entries.isNotEmpty()) entries.map { it.moodLevel }.average() else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mood Tracker") },
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
            if (avgMood != null) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Average Mood", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val avgIndex = avgMood.toInt().coerceIn(0, 4)
                            Text(
                                moodLabels[avgIndex],
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    avgMood < 1.5 -> MaterialTheme.colorScheme.error
                                    avgMood < 2.5 -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.tertiary
                                }
                            )
                            Text(
                                "${entries.size} entries logged",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("How are you feeling?", style = MaterialTheme.typography.titleMedium)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Mood — ${moodLabels[moodLevel]}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                moodLabels.forEachIndexed { i, _ ->
                                    SegmentedButton(
                                        selected = moodLevel == i,
                                        onClick = { moodLevel = i },
                                        shape = SegmentedButtonDefaults.itemShape(index = i, count = moodLabels.size)
                                    ) { Text("${i + 1}", style = MaterialTheme.typography.labelSmall) }
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Emotions (select all that apply)", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                emotionTags.forEach { emotion ->
                                    FilterChip(
                                        selected = emotion in selectedEmotions,
                                        onClick = {
                                            selectedEmotions = if (emotion in selectedEmotions)
                                                selectedEmotions - emotion
                                            else
                                                selectedEmotions + emotion
                                        },
                                        label = { Text(emotion, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Recent food / meal (optional)", style = MaterialTheme.typography.labelMedium)
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
                                        Text("Tap to set as recent food:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        searchResults.forEach { result ->
                                            TextButton(
                                                onClick = {
                                                    recentFood = result.name
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
                                value = recentFood,
                                onValueChange = { recentFood = it },
                                label = { Text("Food or meal that may have affected mood") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            singleLine = false,
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                                val combinedNotes = listOfNotNull(
                                    recentFood.trim().takeIf { it.isNotBlank() }?.let { "Food: $it" },
                                    notes.trim().takeIf { it.isNotBlank() }
                                ).joinToString(" · ")
                                vm.insert(
                                    MoodEntryEntity(
                                        date = todayStr,
                                        time = time,
                                        moodLevel = moodLevel,
                                        emotions = selectedEmotions.joinToString(","),
                                        notes = combinedNotes
                                    )
                                )
                                moodLevel = 2
                                selectedEmotions = setOf()
                                notes = ""
                                recentFood = ""
                                searchQuery = ""
                                searchResults = emptyList()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Log Mood") }
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
                        OutlinedButton(onClick = { vm.deleteAll() }) { Text("Reset") }
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
                                    moodLabels[entry.moodLevel],
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when {
                                        entry.moodLevel < 2 -> MaterialTheme.colorScheme.error
                                        entry.moodLevel == 2 -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.tertiary
                                    }
                                )
                                Text(
                                    "${entry.date} · ${entry.time}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (entry.emotions.isNotBlank()) {
                                Text(
                                    entry.emotions.split(",").filter { it.isNotBlank() }.joinToString(", "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (entry.notes.isNotBlank()) {
                                Text(entry.notes, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "No mood entries yet — log your first one!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}