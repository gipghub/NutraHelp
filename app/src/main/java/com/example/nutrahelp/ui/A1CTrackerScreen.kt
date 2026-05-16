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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.nutrahelp.data.A1CEntryEntity
import com.example.nutrahelp.viewmodel.A1CViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun categorizeA1C(v: Float): Triple<String, String, String> = when {
    v < 5.7f -> Triple("Normal", "< 5.7%", "tertiary")
    v < 6.5f -> Triple("Pre-diabetes", "5.7–6.4%", "secondary")
    else -> Triple("Diabetes range", "≥ 6.5%", "error")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun A1CTrackerScreen(onBack: () -> Unit, vm: A1CViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    var date by remember { mutableStateOf(todayStr) }
    var valueInput by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }

    val latestEntry = entries.firstOrNull()
    val previousEntry = entries.getOrNull(1)
    val trend = if (latestEntry != null && previousEntry != null) {
        latestEntry.value - previousEntry.value
    } else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("A1C Tracker") },
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
                        Text("What is A1C?", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "HbA1c measures your average blood sugar over 2–3 months. GLP-1 medications often improve A1C significantly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider()
                        listOf(
                            Triple("Normal", "< 5.7%", "tertiary"),
                            Triple("Pre-diabetes", "5.7 – 6.4%", "secondary"),
                            Triple("Diabetes", "≥ 6.5%", "error")
                        ).forEach { (label, range, _) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Text(range, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            if (latestEntry != null) {
                item {
                    val (catLabel, catRange, colorToken) = categorizeA1C(latestEntry.value)
                    val catColor: @Composable () -> Color = {
                        when (colorToken) {
                            "error" -> MaterialTheme.colorScheme.error
                            "secondary" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Latest Result", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    "%.1f%%".format(latestEntry.value),
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = catColor()
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(catLabel, style = MaterialTheme.typography.titleSmall, color = catColor(), fontWeight = FontWeight.SemiBold)
                                    Text(catRange, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            if (trend != null) {
                                val trendText = if (trend < 0) "%.1f%% from last reading".format(trend)
                                               else "+%.1f%% from last reading".format(trend)
                                Text(
                                    trendText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (trend < 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log Result", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it; formError = false },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            isError = formError && date.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = valueInput,
                            onValueChange = { valueInput = it; formError = false },
                            label = { Text("A1C value (%)") },
                            placeholder = { Text("e.g. 6.2") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            isError = formError && valueInput.toFloatOrNull() == null,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (formError) {
                            Text("Please enter a valid date and A1C value.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = {
                                val v = valueInput.toFloatOrNull()
                                if (date.isBlank() || v == null || v <= 0f) {
                                    formError = true
                                } else {
                                    vm.insert(A1CEntryEntity(date = date, value = v, notes = notes.trim()))
                                    valueInput = ""; notes = ""; date = todayStr; formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Save Result") }
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
                    val (catLabel, _, colorToken) = categorizeA1C(entry.value)
                    val catColor: @Composable () -> Color = {
                        when (colorToken) {
                            "error" -> MaterialTheme.colorScheme.error
                            "secondary" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(entry.date, style = MaterialTheme.typography.titleSmall)
                                if (entry.notes.isNotBlank()) {
                                    Text(entry.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("%.1f%%".format(entry.value), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = catColor())
                                Text(catLabel, style = MaterialTheme.typography.labelSmall, color = catColor())
                            }
                        }
                    }
                }
            }
        }
    }
}