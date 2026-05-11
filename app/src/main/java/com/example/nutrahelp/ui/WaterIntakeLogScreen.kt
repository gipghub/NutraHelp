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

private const val ML_PER_OZ = 29.5735f

private fun mlToOz(ml: Int): Float = ml / ML_PER_OZ
private fun ozToMl(oz: Float): Int = (oz * ML_PER_OZ).toInt()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterIntakeLogScreen(onBack: () -> Unit) {
    val useMetric = LocalUseMetric.current
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    // Default goal: 2000 ml metric / 64 oz standard (~1893 ml)
    val defaultGoalMl = if (useMetric) 2000 else ozToMl(64f)
    var dailyGoalMl by remember(useMetric) { mutableIntStateOf(defaultGoalMl) }
    var goalInput by remember(useMetric) { mutableStateOf(if (useMetric) "2000" else "64") }
    var customAmount by remember { mutableStateOf("") }
    var todayTotal by remember { mutableIntStateOf(0) }
    var entries by remember { mutableStateOf(listOf<WaterLogEntry>()) }

    val unit = if (useMetric) "ml" else "oz"
    val quickAmounts = if (useMetric) {
        listOf(250 to "250 ml", 500 to "500 ml", 750 to "750 ml")
    } else {
        listOf(237 to "8 oz", 355 to "12 oz", 473 to "16 oz")
    }

    fun displayAmount(ml: Int): String =
        if (useMetric) "$ml ml" else "%.0f oz".format(mlToOz(ml))

    fun displayGoal(): String =
        if (useMetric) "$dailyGoalMl ml" else "%.0f oz".format(mlToOz(dailyGoalMl))

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
                                    displayAmount(todayTotal),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (goalReached) MaterialTheme.colorScheme.tertiary
                                            else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "of ${displayGoal()} goal",
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
                            quickAmounts.forEach { (ml, label) ->
                                OutlinedButton(
                                    onClick = { logAmount(ml) },
                                    modifier = Modifier.weight(1f)
                                ) { Text(label) }
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
                                label = { Text("Custom ($unit)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    val ml = if (useMetric) {
                                        customAmount.toIntOrNull()
                                    } else {
                                        customAmount.toFloatOrNull()?.let { ozToMl(it) }
                                    }
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
                                label = { Text("Daily goal ($unit)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(onClick = {
                                val ml = if (useMetric) {
                                    goalInput.toIntOrNull()
                                } else {
                                    goalInput.toFloatOrNull()?.let { ozToMl(it) }
                                }
                                if (ml != null && ml > 0) dailyGoalMl = ml
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
                                "+${displayAmount(entry.amountMl)}",
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
