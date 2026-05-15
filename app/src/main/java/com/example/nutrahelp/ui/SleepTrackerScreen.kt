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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

private val qualityLabels = listOf("Poor", "Fair", "Good", "Great", "Excellent")

private data class SleepEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val bedtime: String,
    val wakeTime: String,
    val hoursSlept: Float,
    val quality: Int,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTrackerScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    var date by remember { mutableStateOf(todayStr) }
    var bedtime by remember { mutableStateOf("") }
    var wakeTime by remember { mutableStateOf("") }
    var hoursSlept by remember { mutableStateOf("") }
    var quality by remember { mutableIntStateOf(2) }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<SleepEntry>()) }

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
                                    entries = (listOf(
                                        SleepEntry(
                                            date = date,
                                            bedtime = bedtime,
                                            wakeTime = wakeTime,
                                            hoursSlept = h,
                                            quality = quality,
                                            notes = notes.trim()
                                        )
                                    ) + entries).sortedByDescending { it.id }
                                    bedtime = ""
                                    wakeTime = ""
                                    hoursSlept = ""
                                    quality = 2
                                    notes = ""
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
                    Text("History", style = MaterialTheme.typography.titleMedium)
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
