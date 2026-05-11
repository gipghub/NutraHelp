package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private data class GoalDef(
    val name: String,
    val unit: String,
    val defaultTarget: String,
    val period: String,
    val hint: String
)

private val presetGoals = listOf(
    GoalDef("Daily Protein",    "g",       "120",  "Daily",   "Aim for ~1g per lb of goal weight"),
    GoalDef("Daily Calories",   "kcal",    "1500", "Daily",   "Minimum recommended on GLP-1 therapy"),
    GoalDef("Daily Water",      "glasses", "8",    "Daily",   "Stay hydrated — dehydration is common"),
    GoalDef("Weekly Exercise",  "min",     "150",  "Weekly",  "WHO guidelines for moderate activity"),
    GoalDef("Weight to Lose",   "lbs",     "20",   "Total",   "Your overall weight loss goal"),
)

private data class GoalState(var target: String, var current: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalTrackerScreen(onBack: () -> Unit) {
    val goals = remember {
        mutableStateListOf(*presetGoals.map { GoalState(it.defaultTarget, "") }.toTypedArray())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goal Tracker") },
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
                Text(
                    "Set your targets and log your current progress.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            itemsIndexed(presetGoals) { index, def ->
                val state = goals[index]
                val target = state.target.toFloatOrNull() ?: 0f
                val current = state.current.toFloatOrNull() ?: 0f
                val progress = if (target > 0f) (current / target).coerceAtMost(1f) else 0f
                val pct = (progress * 100).toInt()

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(def.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(def.period, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                "$pct%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    pct >= 100 -> MaterialTheme.colorScheme.tertiary
                                    pct >= 50  -> MaterialTheme.colorScheme.primary
                                    else       -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            def.hint,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            var targetText by remember { mutableStateOf(state.target) }
                            var currentText by remember { mutableStateOf(state.current) }

                            OutlinedTextField(
                                value = targetText,
                                onValueChange = { v ->
                                    targetText = v
                                    goals[index] = state.copy(target = v)
                                },
                                label = { Text("Target (${def.unit})") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = currentText,
                                onValueChange = { v ->
                                    currentText = v
                                    goals[index] = state.copy(current = v)
                                },
                                label = { Text("Current (${def.unit})") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
