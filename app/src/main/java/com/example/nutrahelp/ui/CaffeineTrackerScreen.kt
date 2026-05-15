package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val quickCaffeineDrinks = listOf(
    "Coffee (8oz)" to 95,
    "Espresso (1 shot)" to 64,
    "Black tea (8oz)" to 47,
    "Green tea (8oz)" to 28,
    "Matcha latte (12oz)" to 70,
    "Energy drink (8oz)" to 80,
    "Cola soda (12oz)" to 35,
    "Dark chocolate (1oz)" to 12
)

private data class CaffeineEntry(
    val id: Long = System.nanoTime(),
    val drinkName: String,
    val caffeineMg: Int,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaffeineTrackerScreen(onBack: () -> Unit) {
    var dailyLimit by remember { mutableIntStateOf(400) }
    var limitInput by remember { mutableStateOf("400") }
    var customDrink by remember { mutableStateOf("") }
    var customMg by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<CaffeineEntry>()) }

    val totalMg = entries.sumOf { it.caffeineMg }
    val progress = (totalMg.toFloat() / dailyLimit).coerceAtMost(1f)
    val overLimit = totalMg > dailyLimit

    fun logDrink(name: String, mg: Int) {
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        entries = (listOf(CaffeineEntry(drinkName = name, caffeineMg = mg, time = time)) + entries).sortedByDescending { it.id }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Caffeine Tracker") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "${totalMg} mg",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (overLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "of ${dailyLimit} mg daily limit",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (overLimit) {
                                Text(
                                    "+${totalMg - dailyLimit} mg over",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    "${dailyLimit - totalMg} mg left",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (overLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "FDA recommends < 400 mg/day. Caffeine can worsen nausea on GLP-1 — consider cutting back if nausea is an issue.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = limitInput,
                                onValueChange = { limitInput = it },
                                label = { Text("Daily limit (mg)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(onClick = {
                                limitInput.toIntOrNull()?.let { if (it > 0) dailyLimit = it }
                            }) { Text("Set") }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Quick Add", style = MaterialTheme.typography.titleMedium)
                        quickCaffeineDrinks.chunked(2).forEach { pair ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                pair.forEach { (name, mg) ->
                                    OutlinedButton(
                                        onClick = { logDrink(name, mg) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(name, style = MaterialTheme.typography.bodySmall)
                                            Text(
                                                "+${mg} mg",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }

                        HorizontalDivider()

                        Text("Custom drink", style = MaterialTheme.typography.labelMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customDrink,
                                onValueChange = { customDrink = it; formError = false },
                                label = { Text("Drink name") },
                                singleLine = true,
                                isError = formError && customDrink.isBlank(),
                                modifier = Modifier.weight(2f)
                            )
                            OutlinedTextField(
                                value = customMg,
                                onValueChange = { customMg = it; formError = false },
                                label = { Text("mg") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = formError && customMg.toIntOrNull() == null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (formError) {
                            Text(
                                "Enter a drink name and caffeine amount.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Button(
                            onClick = {
                                val mg = customMg.toIntOrNull()
                                if (customDrink.isBlank() || mg == null || mg <= 0) {
                                    formError = true
                                } else {
                                    logDrink(customDrink.trim(), mg)
                                    customDrink = ""; customMg = ""; formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Add Drink") }
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
                        Text("Today's Log", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { entries = listOf() }) { Text("Reset") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(entry.drinkName, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    entry.time,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "${entry.caffeineMg} mg",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(onClick = { entries = entries.filter { it.id != entry.id } }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
