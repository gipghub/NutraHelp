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
import java.util.Date
import java.util.Locale

private val nsvCategories = listOf(
    "Energy", "Clothing Fit", "Mood", "Mobility", "Sleep Quality",
    "Confidence", "Reduced Cravings", "Social", "Exercise Endurance", "Custom"
)

private data class NsvEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val category: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NsvLogScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    var date by remember { mutableStateOf(todayStr) }
    var selectedCategory by remember { mutableStateOf(nsvCategories[0]) }
    var description by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<NsvEntry>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NSV Log") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("What is an NSV?", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Non-Scale Victories are progress wins that don't show on the scale — more energy, clothes fitting better, improved confidence, and more. They matter just as much!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log a Victory", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it; formError = false },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            isError = formError && date.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Category", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                nsvCategories.forEach { cat ->
                                    FilterChip(
                                        selected = selectedCategory == cat,
                                        onClick = { selectedCategory = cat },
                                        label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it; formError = false },
                            label = { Text("Describe your victory") },
                            placeholder = { Text("e.g. Walked up stairs without getting winded") },
                            singleLine = false,
                            minLines = 2,
                            isError = formError && description.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (formError) {
                            Text(
                                "Please enter a date and describe your victory.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                if (date.isBlank() || description.isBlank()) {
                                    formError = true
                                } else {
                                    entries = listOf(
                                        NsvEntry(date = date, category = selectedCategory, description = description.trim())
                                    ) + entries
                                    description = ""; date = todayStr; formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Save Victory 🎉") }
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
                        Text("My Victories", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${entries.size} total",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
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
                                Text(entry.date, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    entry.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(entry.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "No victories logged yet — log your first one!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
