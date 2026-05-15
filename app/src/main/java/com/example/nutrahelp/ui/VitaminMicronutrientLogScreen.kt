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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.LinearProgressIndicator
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

private data class NutrientDef(
    val name: String,
    val unit: String,
    val dailyTarget: Float,
    val glp1Note: String? = null
)

private val trackedNutrients = listOf(
    NutrientDef("Vitamin B12", "mcg", 2.4f, "Absorption often reduced with GLP-1 + metformin"),
    NutrientDef("Vitamin D", "IU", 600f, "Deficiency common with reduced food intake"),
    NutrientDef("Iron", "mg", 18f, "Monitor if experiencing fatigue or hair loss"),
    NutrientDef("Folate", "mcg", 400f),
    NutrientDef("Zinc", "mg", 8f, "May decrease with reduced overall food intake"),
    NutrientDef("Calcium", "mg", 1000f),
    NutrientDef("Magnesium", "mg", 310f, "Often low on calorie-restricted diets"),
    NutrientDef("Omega-3", "mg", 1000f)
)

private data class NutrientEntry(
    val id: Long = System.nanoTime(),
    val nutrientName: String,
    val amount: Float,
    val unit: String,
    val source: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitaminMicronutrientLogScreen(onBack: () -> Unit) {
    var selectedNutrient by remember { mutableStateOf(trackedNutrients[0]) }
    var expanded by remember { mutableStateOf(false) }
    var amountInput by remember { mutableStateOf("") }
    var sourceInput by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<NutrientEntry>()) }

    val totals = entries
        .groupBy { it.nutrientName }
        .mapValues { (_, list) -> list.sumOf { it.amount.toDouble() }.toFloat() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vitamins & Nutrients") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Today's Intake", style = MaterialTheme.typography.titleMedium)
                        trackedNutrients.forEach { nutrient ->
                            val logged = totals[nutrient.name] ?: 0f
                            val progress = (logged / nutrient.dailyTarget).coerceAtMost(1f)
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(nutrient.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                    Text(
                                        "%.1f / %.0f %s".format(logged, nutrient.dailyTarget, nutrient.unit),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (progress >= 1f) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = if (progress >= 1f) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Log Nutrient", style = MaterialTheme.typography.titleMedium)

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = "${selectedNutrient.name} (${selectedNutrient.unit})",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Nutrient") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                trackedNutrients.forEach { nutrient ->
                                    DropdownMenuItem(
                                        text = { Text("${nutrient.name} (${nutrient.unit})") },
                                        onClick = {
                                            selectedNutrient = nutrient
                                            expanded = false
                                            formError = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }

                        if (selectedNutrient.glp1Note != null) {
                            Text(
                                "⚠ ${selectedNutrient.glp1Note}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = amountInput,
                                onValueChange = { amountInput = it; formError = false },
                                label = { Text("Amount (${selectedNutrient.unit})") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                isError = formError,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = sourceInput,
                                onValueChange = { sourceInput = it },
                                label = { Text("Source") },
                                placeholder = { Text("e.g. Supplement") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (formError) {
                            Text(
                                "Enter a valid amount.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                val amount = amountInput.toFloatOrNull()
                                if (amount == null || amount <= 0f) {
                                    formError = true
                                } else {
                                    entries = (listOf(
                                        NutrientEntry(
                                            nutrientName = selectedNutrient.name,
                                            amount = amount,
                                            unit = selectedNutrient.unit,
                                            source = sourceInput.trim().ifBlank { "Supplement" }
                                        )
                                    ) + entries).sortedByDescending { it.id }
                                    amountInput = ""
                                    sourceInput = ""
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Log Nutrient") }
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
                                Text(entry.nutrientName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(entry.source, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                "%.1f ${entry.unit}".format(entry.amount),
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
