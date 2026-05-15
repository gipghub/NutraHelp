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
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val sideEffectOptions = listOf(
    "Nausea", "Fatigue", "Constipation", "Diarrhea", "Vomiting",
    "Headache", "Dizziness", "Injection Site Reaction", "Appetite Loss", "Other"
)

private val severityLabels = mapOf(1 to "Mild", 2 to "Moderate", 3 to "Noticeable", 4 to "Severe", 5 to "Extreme")

private data class SideEffectEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val effect: String,
    val severity: Int,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SideEffectsScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    var date by remember { mutableStateOf(todayStr) }
    var effect by remember { mutableStateOf(sideEffectOptions[0]) }
    var effectExpanded by remember { mutableStateOf(false) }
    var severity by remember { mutableIntStateOf(1) }
    var notes by remember { mutableStateOf("") }
    var entries by remember { mutableStateOf(listOf<SideEffectEntry>()) }
    var formError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Side Effects Log") },
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
                        Text("Log Side Effect", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it; formError = false },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            isError = formError && date.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = effectExpanded,
                            onExpandedChange = { effectExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = effect,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Side Effect") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = effectExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = effectExpanded,
                                onDismissRequest = { effectExpanded = false }
                            ) {
                                sideEffectOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = { effect = option; effectExpanded = false }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Severity: ${severityLabels[severity]}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                (1..5).forEach { level ->
                                    FilterChip(
                                        selected = severity == level,
                                        onClick = { severity = level },
                                        label = { Text(level.toString()) }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            minLines = 2,
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (date.isBlank()) {
                                    formError = true
                                } else {
                                    entries = (listOf(
                                        SideEffectEntry(
                                            date = date,
                                            effect = effect,
                                            severity = severity,
                                            notes = notes.trim()
                                        )
                                    ) + entries).sortedByDescending { it.id }
                                    date = todayStr
                                    severity = 1
                                    notes = ""
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Log Entry")
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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(entry.effect, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "Severity ${entry.severity}/5",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = when (entry.severity) {
                                            in 1..2 -> MaterialTheme.colorScheme.tertiary
                                            3 -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                    )
                                }
                                Text(
                                    entry.date,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                }
            } else {
                item {
                    Text(
                        "No side effects logged yet. Use the form above to track symptoms.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
