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
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val readingTypes = listOf("Fasting", "Before Meal", "After Meal (2h)", "Bedtime")

private data class BloodSugarEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val readingType: String,
    val valueMgDl: Float,
    val notes: String
)

private fun referenceText(readingType: String) = when (readingType) {
    "Fasting"         -> "Normal: 70–99 mg/dL · Pre-diabetes: 100–125 · Diabetes: ≥126"
    "After Meal (2h)" -> "Normal: <140 mg/dL · Pre-diabetes: 140–199 · Diabetes: ≥200"
    "Before Meal"     -> "Target (on meds): 80–130 mg/dL"
    "Bedtime"         -> "Target (on meds): 100–140 mg/dL"
    else              -> ""
}

private fun categorize(value: Float, readingType: String): String = when (readingType) {
    "Fasting" -> when {
        value < 70f   -> "Low"
        value <= 99f  -> "Normal"
        value <= 125f -> "Pre-diabetes"
        else          -> "High"
    }
    "After Meal (2h)" -> when {
        value < 70f   -> "Low"
        value <= 139f -> "Normal"
        value <= 199f -> "Pre-diabetes"
        else          -> "High"
    }
    else -> when {
        value < 70f   -> "Low"
        value <= 130f -> "Normal"
        else          -> "High"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodSugarLogScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    var date by remember { mutableStateOf(todayStr) }
    var readingType by remember { mutableStateOf(readingTypes[0]) }
    var typeExpanded by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var entries by remember { mutableStateOf(listOf<BloodSugarEntry>()) }
    var formError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blood Sugar Log") },
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
                        Text("Log Reading", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it; formError = false },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            isError = formError && date.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = readingType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Reading Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false }
                            ) {
                                readingTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = { readingType = type; typeExpanded = false }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = value,
                            onValueChange = { value = it; formError = false },
                            label = { Text("Blood Sugar (mg/dL)") },
                            singleLine = true,
                            isError = formError && value.isBlank(),
                            supportingText = {
                                Text(
                                    referenceText(readingType),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            minLines = 2,
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (formError) {
                            Text("Please fill in all required fields.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = {
                                val v = value.toFloatOrNull()
                                if (date.isBlank() || v == null || v <= 0f) {
                                    formError = true
                                } else {
                                    entries = (listOf(BloodSugarEntry(date = date, readingType = readingType, valueMgDl = v, notes = notes.trim())) + entries).sortedByDescending { it.id }
                                    date = todayStr
                                    value = ""
                                    notes = ""
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Log Reading")
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
                    val category = categorize(entry.valueMgDl, entry.readingType)
                    @Suppress("UnusedVariable")
                    val categoryColor: Color
                    categoryColor = when (category) {
                        "Normal" -> MaterialTheme.colorScheme.tertiary
                        "Low"    -> MaterialTheme.colorScheme.secondary
                        else     -> MaterialTheme.colorScheme.error
                    }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(entry.readingType, style = MaterialTheme.typography.titleSmall)
                                Text(entry.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (entry.notes.isNotBlank()) {
                                    Text(entry.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    "${entry.valueMgDl.toInt()} mg/dL",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = categoryColor,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(category, style = MaterialTheme.typography.labelSmall, color = categoryColor)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("No readings logged yet. Use the form above to track your blood sugar.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
