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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.HeartRateEntryEntity
import com.example.nutrahelp.viewmodel.HeartRateViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val hrContexts = listOf("Morning (resting)", "Evening (resting)", "Pre-exercise", "Post-exercise", "Relaxed", "Other")

private fun hrCategory(bpm: Int) = when {
    bpm < 40 -> Triple("Very Low", "Consult doctor", "error")
    bpm < 60 -> Triple("Athletic", "Excellent", "tertiary")
    bpm <= 100 -> Triple("Normal", "Healthy range", "tertiary")
    bpm <= 120 -> Triple("Elevated", "Monitor closely", "secondary")
    else -> Triple("High", "Consult doctor", "error")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartRateLogScreen(onBack: () -> Unit, vm: HeartRateViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    var date by remember { mutableStateOf(todayStr) }
    var bpmInput by remember { mutableStateOf("") }
    var context by remember { mutableStateOf(hrContexts[0]) }
    var contextExpanded by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Heart Rate Log") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Reference Ranges", style = MaterialTheme.typography.titleSmall)
                        listOf(
                            "< 60 bpm" to "Athletic / Low",
                            "60–100 bpm" to "Normal",
                            "101–120 bpm" to "Elevated",
                            "> 120 bpm" to "High – consult doctor"
                        ).forEach { (range, label) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(range, style = MaterialTheme.typography.bodySmall)
                                Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Log Reading", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it; formError = false },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            isError = formError && date.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = bpmInput,
                            onValueChange = { bpmInput = it; formError = false },
                            label = { Text("Heart rate (bpm)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = formError && bpmInput.toIntOrNull() == null,
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = contextExpanded,
                            onExpandedChange = { contextExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = context,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Context") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(contextExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = contextExpanded, onDismissRequest = { contextExpanded = false }) {
                                hrContexts.forEach { c ->
                                    DropdownMenuItem(text = { Text(c) }, onClick = { context = c; contextExpanded = false })
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
                            Text("Please enter a valid date and heart rate.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = {
                                val bpm = bpmInput.toIntOrNull()
                                if (date.isBlank() || bpm == null || bpm <= 0) {
                                    formError = true
                                } else {
                                    vm.insert(HeartRateEntryEntity(date = date, bpm = bpm, context = context, notes = notes.trim()))
                                    bpmInput = ""; notes = ""; date = todayStr; formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Save Reading") }
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
                    val (catLabel, catDesc, colorToken) = hrCategory(entry.bpm)
                    val catColor: @Composable () -> Color = {
                        when (colorToken) {
                            "error" -> MaterialTheme.colorScheme.error
                            "secondary" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(entry.date, style = MaterialTheme.typography.titleSmall)
                                Text("$catLabel · $catDesc", style = MaterialTheme.typography.labelSmall, color = catColor(), fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${entry.bpm} bpm", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = catColor())
                                Text(entry.context, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (entry.notes.isNotBlank()) {
                                Text(entry.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("No readings logged yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}