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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.FoodSearchResult
import com.example.nutrahelp.data.NutrientEntryEntity
import com.example.nutrahelp.data.OpenFoodFactsRepository
import com.example.nutrahelp.viewmodel.NutrientViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitaminMicronutrientLogScreen(onBack: () -> Unit, vm: NutrientViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    var selectedNutrient by remember { mutableStateOf(trackedNutrients[0]) }
    var expanded by remember { mutableStateOf(false) }
    var amountInput by remember { mutableStateOf("") }
    var sourceInput by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FoodSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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

                        Text("Search Food Source", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it; searchError = false },
                                label = { Text("Search Open Food Facts…") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSearching) {
                                CircularProgressIndicator(modifier = Modifier.padding(4.dp), strokeWidth = 2.dp)
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        if (searchQuery.isNotBlank()) {
                                            scope.launch {
                                                isSearching = true; searchError = false
                                                val results = OpenFoodFactsRepository.search(searchQuery)
                                                searchResults = results
                                                searchError = results.isEmpty()
                                                isSearching = false
                                            }
                                        }
                                    },
                                    enabled = searchQuery.isNotBlank()
                                ) { Text("Search") }
                            }
                        }
                        if (searchError) {
                            Text("No results found.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }
                        if (searchResults.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("Tap to fill source (enter amount manually):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    searchResults.forEach { result ->
                                        TextButton(
                                            onClick = {
                                                sourceInput = result.name
                                                searchResults = emptyList(); searchQuery = ""
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Text(result.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                                val details = listOfNotNull(
                                                    result.caloriesPer100g?.let { "${it} kcal" },
                                                    result.proteinPer100g?.let { "P:${"%.1f".format(it)}g" },
                                                    result.fiberPer100g?.let { "Fiber:${"%.1f".format(it)}g" }
                                                ).joinToString(" · ")
                                                if (details.isNotBlank()) Text(details, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                    }
                                }
                            }
                        }

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
                                    vm.insert(NutrientEntryEntity(
                                        date = todayStr,
                                        nutrientName = selectedNutrient.name,
                                        amount = amount,
                                        unit = selectedNutrient.unit,
                                        source = sourceInput.trim().ifBlank { "Supplement" }
                                    ))
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
                        OutlinedButton(onClick = { vm.deleteAll() }) { Text("Reset") }
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
                            IconButton(onClick = { vm.delete(entry) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
