package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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

private data class HydrationActivityLevel(val label: String, val description: String, val mlPerKg: Float)

private val hydrationActivityLevels = listOf(
    HydrationActivityLevel("Sedentary", "Little or no exercise", 30f),
    HydrationActivityLevel("Light", "Light exercise 1–3 days/week", 33f),
    HydrationActivityLevel("Moderate", "Exercise 3–5 days/week", 35f),
    HydrationActivityLevel("Active", "Hard exercise 6–7 days/week", 38f),
    HydrationActivityLevel("Very Active", "Physical job or twice-daily training", 41f)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationCalculatorScreen(onBack: () -> Unit) {
    val useMetric = LocalUseMetric.current

    var weightInput by remember { mutableStateOf("") }
    var activityExpanded by remember { mutableStateOf(false) }
    var selectedActivity by remember { mutableIntStateOf(1) }
    var targetMl by remember { mutableStateOf<Int?>(null) }
    var cupsLogged by remember { mutableIntStateOf(0) }
    var formError by remember { mutableStateOf(false) }

    val weightUnit = if (useMetric) "kg" else "lbs"
    val cupSizeMl = if (useMetric) 250 else 237  // 250ml metric, 8oz standard
    val cupLabel = if (useMetric) "250 ml" else "8 oz"

    val progress = if (targetMl != null && targetMl!! > 0) {
        (cupsLogged * cupSizeMl).toFloat() / targetMl!!
    } else 0f
    val loggedMl = cupsLogged * cupSizeMl
    val remaining = if (targetMl != null) maxOf(0, targetMl!! - loggedMl) else null
    val overTarget = targetMl != null && loggedMl > targetMl!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hydration Calculator") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Calculate Your Daily Target", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it; formError = false },
                            label = { Text("Weight ($weightUnit)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            isError = formError,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Activity Level", style = MaterialTheme.typography.labelMedium)
                            ExposedDropdownMenuBox(
                                expanded = activityExpanded,
                                onExpandedChange = { activityExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = hydrationActivityLevels[selectedActivity].label,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(
                                    expanded = activityExpanded,
                                    onDismissRequest = { activityExpanded = false }
                                ) {
                                    hydrationActivityLevels.forEachIndexed { index, level ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(level.label, style = MaterialTheme.typography.bodyMedium)
                                                    Text(level.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            },
                                            onClick = { selectedActivity = index; activityExpanded = false }
                                        )
                                    }
                                }
                            }
                        }

                        if (formError) {
                            Text("Enter a valid weight.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = {
                                val weight = weightInput.toFloatOrNull()
                                if (weight == null || weight <= 0f) {
                                    formError = true
                                } else {
                                    val weightKg = if (useMetric) weight else weight * 0.453592f
                                    val ml = (weightKg * hydrationActivityLevels[selectedActivity].mlPerKg).toInt()
                                    targetMl = ml
                                    cupsLogged = 0
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Set My Target") }
                    }
                }
            }

            targetMl?.let { target ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (overTarget) MaterialTheme.colorScheme.tertiaryContainer
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    val displayLogged = if (useMetric) "${loggedMl} ml" else "${"%.1f".format(loggedMl / 29.5735f)} oz"
                                    val displayTarget = if (useMetric) "${target} ml" else "${"%.1f".format(target / 29.5735f)} oz"
                                    Text(
                                        displayLogged,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (overTarget) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        "of $displayTarget daily target",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "$cupsLogged cups",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        "($cupLabel each)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            LinearProgressIndicator(
                                progress = { progress.coerceAtMost(1f) },
                                modifier = Modifier.fillMaxWidth(),
                                color = if (overTarget) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )

                            if (overTarget) {
                                Text(
                                    "Target reached! Great hydration today.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else if (remaining != null) {
                                val remDisplay = if (useMetric) "${remaining} ml" else "${"%.1f".format(remaining / 29.5735f)} oz"
                                Text(
                                    "$remDisplay remaining (${(remaining / cupSizeMl.toFloat()).let { "%.1f".format(it) }} cups)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { cupsLogged++ },
                                    modifier = Modifier.weight(1f)
                                ) { Text("+ 1 Cup ($cupLabel)") }
                                OutlinedButton(
                                    onClick = { if (cupsLogged > 0) cupsLogged-- },
                                    modifier = Modifier.weight(1f),
                                    enabled = cupsLogged > 0
                                ) { Text("− 1 Cup") }
                            }

                            OutlinedButton(
                                onClick = { cupsLogged = 0 },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = cupsLogged > 0
                            ) { Text("Reset Today") }
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Hydration Tips", style = MaterialTheme.typography.titleSmall)
                            HorizontalDivider()
                            listOf(
                                "GLP-1 medications can cause nausea — staying hydrated helps manage it.",
                                "Drink water before meals to support fullness and digestion.",
                                "If urine is pale yellow, you're well hydrated. Dark yellow = drink more.",
                                "Herbal teas and sparkling water count toward your total.",
                                "Increase target by 500–750 ml on days with heavy exercise or heat."
                            ).forEach { tip ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    Text(tip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            if (targetMl == null) {
                item {
                    Text(
                        "Enter your weight and activity level above to calculate your personalised daily water target.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
