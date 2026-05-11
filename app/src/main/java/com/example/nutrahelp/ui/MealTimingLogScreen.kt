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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack", "Other")

private data class MealTimingEntry(
    val id: Long = System.nanoTime(),
    val mealType: String,
    val mealName: String,
    val time: String,
    val minutesSinceMidnight: Int
)

private fun parseTimeToMinutes(time: String): Int? {
    return try {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = sdf.parse(time) ?: return null
        val cal = Calendar.getInstance().apply { setTime(date) }
        cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    } catch (e: Exception) { null }
}

private fun minutesToTimeStr(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    val ampm = if (h < 12) "AM" else "PM"
    val hour = when {
        h == 0 -> 12
        h > 12 -> h - 12
        else -> h
    }
    return "%d:%02d %s".format(hour, m, ampm)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MealTimingLogScreen(onBack: () -> Unit) {
    val currentTime = remember { SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()) }

    var selectedMealType by remember { mutableStateOf(mealTypes[0]) }
    var mealName by remember { mutableStateOf("") }
    var timeInput by remember { mutableStateOf(currentTime) }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<MealTimingEntry>()) }

    val sortedEntries = entries.sortedBy { it.minutesSinceMidnight }
    val validMinutes = sortedEntries.map { it.minutesSinceMidnight }
    val windowStart = validMinutes.minOrNull()
    val windowEnd = validMinutes.maxOrNull()
    val windowMinutes = if (windowStart != null && windowEnd != null && windowEnd > windowStart) windowEnd - windowStart else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Timing") },
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
            if (windowMinutes != null && windowStart != null && windowEnd != null) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Today's Eating Window", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${windowMinutes / 60}h ${windowMinutes % 60}m",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "${minutesToTimeStr(windowStart)} → ${minutesToTimeStr(windowEnd)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val hint = when {
                                windowMinutes <= 480 -> "Great! A ≤8h window supports metabolic health."
                                windowMinutes <= 600 -> "Good — aim for a 10h window or less."
                                else -> "Consider narrowing your eating window over time."
                            }
                            Text(hint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log a Meal", style = MaterialTheme.typography.titleMedium)

                        Text("Meal type", style = MaterialTheme.typography.labelMedium)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            mealTypes.forEach { type ->
                                FilterChip(
                                    selected = selectedMealType == type,
                                    onClick = { selectedMealType = type },
                                    label = { Text(type, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = mealName,
                                onValueChange = { mealName = it },
                                label = { Text("Meal (optional)") },
                                singleLine = true,
                                modifier = Modifier.weight(2f)
                            )
                            OutlinedTextField(
                                value = timeInput,
                                onValueChange = { timeInput = it; formError = false },
                                label = { Text("Time") },
                                singleLine = true,
                                isError = formError,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (formError) {
                            Text(
                                "Enter a valid time (e.g. 8:30 AM).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                val minutes = parseTimeToMinutes(timeInput.trim())
                                if (minutes == null) {
                                    formError = true
                                } else {
                                    entries = listOf(
                                        MealTimingEntry(
                                            mealType = selectedMealType,
                                            mealName = mealName.trim(),
                                            time = timeInput.trim(),
                                            minutesSinceMidnight = minutes
                                        )
                                    ) + entries
                                    mealName = ""
                                    timeInput = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Log Meal") }
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
                        Text("Today's Meals", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { entries = listOf() }) { Text("Reset") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(sortedEntries, key = { it.id }) { entry ->
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
                                    if (entry.mealName.isNotBlank()) entry.mealName else entry.mealType,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (entry.mealName.isNotBlank()) {
                                    Text(
                                        entry.mealType,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                entry.time,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "No meals logged yet — start tracking your eating window!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
