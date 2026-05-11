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
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val moodLabels = listOf("Very Low", "Low", "Neutral", "Good", "Great")
private val emotionTags = listOf(
    "Happy", "Anxious", "Calm", "Motivated", "Irritable",
    "Sad", "Overwhelmed", "Content", "Hopeful", "Tired"
)

private data class MoodEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val time: String,
    val moodLevel: Int,
    val emotions: Set<String>,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MoodTrackerScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()) }

    var moodLevel by remember { mutableIntStateOf(2) }
    var selectedEmotions by remember { mutableStateOf(setOf<String>()) }
    var notes by remember { mutableStateOf("") }
    var entries by remember { mutableStateOf(listOf<MoodEntry>()) }

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
                                entries = listOf(
                                    MoodEntry(
                                        date = todayStr,
                                        time = time,
                                        moodLevel = moodLevel,
                                        emotions = selectedEmotions,
                                        notes = notes.trim()
                                    )
                                ) + entries
                                moodLevel = 2
                                selectedEmotions = setOf()
                                notes = ""
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
                            if (entry.emotions.isNotEmpty()) {
                                Text(
                                    entry.emotions.joinToString(", "),
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
