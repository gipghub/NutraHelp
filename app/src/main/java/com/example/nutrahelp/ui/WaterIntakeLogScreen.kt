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

private data class WaterLogEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val amountMl: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterIntakeLogScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    var dailyGoalMl by remember { mutableIntStateOf(2000) }
    var goalInput by remember { mutableStateOf("2000") }
    var customAmount by remember { mutableStateOf("") }
    var todayTotal by remember { mutableIntStateOf(0) }
    var entries by remember { mutableStateOf(listOf<WaterLogEntry>()) }

    val progress = if (dailyGoalMl > 0) (todayTotal.toFloat() / dailyGoalMl).coerceAtMost(1f) else 0f
    val goalReached = todayTotal >= dailyGoalMl

    fun logAmount(ml: Int) {
        todayTotal += ml
        entries = listOf(WaterLogEntry(date = todayStr, amountMl = ml)) + entries
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Water Intake") },
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
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "$todayTotal ml",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (goalReached) MaterialTheme.colorScheme.tertiary
                                            else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "of ${dailyGoalMl} ml goal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (goalReached) {
                                Text(
                                    "Goal reached!",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            "${(progress * 100).toInt()}% of daily goal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider()

                        Text("Quick Add", style = MaterialTheme.typography.labelMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(250, 500, 750).forEach { ml ->
                                OutlinedButton(
                                    onClick = { logAmount(ml) },
                                    modifier = Modifier.weight(1f)
                                ) { Text("${ml}ml") }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customAmount,
                                onValueChange = { customAmount = it },
                                label = { Text("Custom (ml)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    val ml = customAmount.toIntOrNull()
                                    if (ml != null && ml > 0) {
                                        logAmount(ml)
                                        customAmount = ""
                                    }
                                }
                            ) { Text("Add") }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = goalInput,
                                onValueChange = { goalInput = it },
                                label = { Text("Daily goal (ml)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(onClick = {
                                val g = goalInput.toIntOrNull()
                                if (g != null && g > 0) dailyGoalMl = g
                            }) { Text("Set") }
                        }
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
                        OutlinedButton(onClick = { todayTotal = 0; entries = listOf() }) {
                            Text("Reset")
                        }
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
                            Text(entry.date, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "+${entry.amountMl} ml",
                                style = MaterialTheme.typography.bodyMedium,
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
