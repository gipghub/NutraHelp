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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private data class RecipeIngredient(
    val id: Long = System.nanoTime(),
    val name: String,
    val calories: Int,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeNutritionCalculatorScreen(onBack: () -> Unit) {
    var recipeName by remember { mutableStateOf("") }
    var servingsInput by remember { mutableStateOf("1") }
    var ingredients by remember { mutableStateOf(listOf<RecipeIngredient>()) }

    var ingName by remember { mutableStateOf("") }
    var ingCal by remember { mutableStateOf("") }
    var ingProtein by remember { mutableStateOf("") }
    var ingCarbs by remember { mutableStateOf("") }
    var ingFat by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }

    val servings = servingsInput.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val totalCal = ingredients.sumOf { it.calories }
    val totalProtein = ingredients.sumOf { it.proteinG.toDouble() }.toFloat()
    val totalCarbs = ingredients.sumOf { it.carbsG.toDouble() }.toFloat()
    val totalFat = ingredients.sumOf { it.fatG.toDouble() }.toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Calculator") },
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
                        Text("Recipe Details", style = MaterialTheme.typography.titleMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = recipeName,
                                onValueChange = { recipeName = it },
                                label = { Text("Recipe name") },
                                singleLine = true,
                                modifier = Modifier.weight(2f)
                            )
                            OutlinedTextField(
                                value = servingsInput,
                                onValueChange = { servingsInput = it },
                                label = { Text("Servings") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            if (ingredients.isNotEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Totals (whole recipe)", style = MaterialTheme.typography.titleSmall)
                                if (servings > 1) {
                                    Text(
                                        "÷$servings servings",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                listOf(
                                    "Cal" to totalCal.toFloat(),
                                    "Protein" to totalProtein,
                                    "Carbs" to totalCarbs,
                                    "Fat" to totalFat
                                ).forEach { (label, total) ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "%.0f".format(total),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            if (servings > 1) {
                                HorizontalDivider()
                                Text("Per serving", style = MaterialTheme.typography.titleSmall)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    listOf(
                                        "Cal" to totalCal.toFloat() / servings,
                                        "Protein" to totalProtein / servings,
                                        "Carbs" to totalCarbs / servings,
                                        "Fat" to totalFat / servings
                                    ).forEach { (label, perServing) ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                "%.0f".format(perServing),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Add Ingredient", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = ingName,
                            onValueChange = { ingName = it; formError = false },
                            label = { Text("Ingredient name") },
                            singleLine = true,
                            isError = formError && ingName.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = ingCal,
                                onValueChange = { ingCal = it; formError = false },
                                label = { Text("Cal") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = formError && ingCal.toIntOrNull() == null,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = ingProtein,
                                onValueChange = { ingProtein = it },
                                label = { Text("Pro g") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = ingCarbs,
                                onValueChange = { ingCarbs = it },
                                label = { Text("Carb g") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = ingFat,
                                onValueChange = { ingFat = it },
                                label = { Text("Fat g") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (formError) {
                            Text(
                                "Enter a name and calorie count.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                val cal = ingCal.toIntOrNull()
                                if (ingName.isBlank() || cal == null || cal < 0) {
                                    formError = true
                                } else {
                                    ingredients = ingredients + RecipeIngredient(
                                        name = ingName.trim(),
                                        calories = cal,
                                        proteinG = ingProtein.toFloatOrNull() ?: 0f,
                                        carbsG = ingCarbs.toFloatOrNull() ?: 0f,
                                        fatG = ingFat.toFloatOrNull() ?: 0f
                                    )
                                    ingName = ""; ingCal = ""; ingProtein = ""; ingCarbs = ""; ingFat = ""
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Add Ingredient") }
                    }
                }
            }

            if (ingredients.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ingredients (${ingredients.size})", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { ingredients = listOf() }) { Text("Clear") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(ingredients, key = { it.id }) { ing ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ing.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(
                                    "${ing.calories} cal · P:%.0fg · C:%.0fg · F:%.0fg".format(ing.proteinG, ing.carbsG, ing.fatG),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { ingredients = ingredients.filter { it.id != ing.id } }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "Add ingredients above to calculate nutrition totals.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
