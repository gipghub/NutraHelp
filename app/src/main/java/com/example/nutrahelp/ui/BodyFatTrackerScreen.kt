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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val bfMethods = listOf("Smart Scale", "Calipers", "DEXA Scan", "Navy Method", "Other")

private data class BodyFatEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val bodyFatPercent: Float,
    val method: String
)

private fun bfCategory(percent: Float, isMale: Boolean): String =
    if (isMale) {
        when {
            percent < 6f -> "Essential fat"
            percent < 14f -> "Athletic"
            percent < 18f -> "Fitness"
            percent < 25f -> "Average"
            else -> "Above average"
        }
    } else {
        when {
            percent < 14f -> "Essential fat"
            percent < 21f -> "Athletic"
            percent < 25f -> "Fitness"
            percent < 32f -> "Average"
            else -> "Above average"
        }
    }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BodyFatTrackerScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()) }

    var dateInput by remember { mutableStateOf(todayStr) }
    var bfInput by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf(bfMethods[0]) }
    var isMale by remember { mutableStateOf(true) }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<BodyFatEntry>()) }

    val latest = entries.firstOrNull()
    val previous = entries.getOrNull(1)
    val trend = if (latest != null && previous != null) latest.bodyFatPercent - previous.bodyFatPercent else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Body Fat %") },
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
            if (latest != null) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "%.1f%%".format(latest.bodyFatPercent),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                bfCategory(latest.bodyFatPercent, isMale),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            if (trend != null) {
                                val sign = if (trend > 0f) "+" else ""
                                Text(
                                    "$sign%.1f%% since last reading".format(trend),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (trend <= 0f) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Reference Ranges", style = MaterialTheme.typography.titleSmall)
                        val ranges = if (isMale)
                            listOf("Essential" to "<6%", "Athletic" to "6–13%", "Fitness" to "14–17%", "Average" to "18–24%", "Above avg" to "25%+")
                        else
                            listOf("Essential" to "<14%", "Athletic" to "14–20%", "Fitness" to "21–24%", "Average" to "25–31%", "Above avg" to "32%+")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            ranges.forEach { (label, range) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(range, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { isMale = true },
                                modifier = Modifier.weight(1f)
                            ) { Text(if (isMale) "✓ Male" else "Male") }
                            OutlinedButton(
                                onClick = { isMale = false },
                                modifier = Modifier.weight(1f)
                            ) { Text(if (!isMale) "✓ Female" else "Female") }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Log Reading", style = MaterialTheme.typography.titleMedium)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = bfInput,
                                onValueChange = { bfInput = it; formError = false },
                                label = { Text("Body fat (%)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                isError = formError,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = dateInput,
                                onValueChange = { dateInput = it },
                                label = { Text("Date") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Text("Measurement method", style = MaterialTheme.typography.labelMedium)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            bfMethods.forEach { method ->
                                FilterChip(
                                    selected = selectedMethod == method,
                                    onClick = { selectedMethod = method },
                                    label = { Text(method, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        if (formError) {
                            Text(
                                "Enter a valid body fat percentage (1–70).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                val bf = bfInput.toFloatOrNull()
                                if (bf == null || bf < 1f || bf > 70f) {
                                    formError = true
                                } else {
                                    entries = (listOf(
                                        BodyFatEntry(date = dateInput.trim(), bodyFatPercent = bf, method = selectedMethod)
                                    ) + entries).sortedByDescending { it.id }
                                    bfInput = ""
                                    dateInput = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
                                    formError = false
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
                        OutlinedButton(onClick = { entries = listOf() }) { Text("Reset") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(entry.date, style = MaterialTheme.typography.bodyMedium)
                                Text(entry.method, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                "%.1f%%".format(entry.bodyFatPercent),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
