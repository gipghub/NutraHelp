package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nutrahelp.data.Meal
import com.example.nutrahelp.data.MealCategory
import com.example.nutrahelp.data.sampleMeals

private data class LogEntry(val id: Long = System.nanoTime(), val meal: Meal)

@Composable
fun LogScreen(
    onNavigateToMealLog: () -> Unit = {},
    onNavigateToSideEffects: () -> Unit = {},
    onNavigateToSupplements: () -> Unit = {},
    onNavigateToJournal: () -> Unit = {},
    onNavigateToBloodSugar: () -> Unit = {},
    onNavigateToSleep: () -> Unit = {},
    onNavigateToMacros: () -> Unit = {},
    onNavigateToStress: () -> Unit = {},
    onNavigateToGlp1: () -> Unit = {},
    onNavigateToFiber: () -> Unit = {},
    onNavigateToFoodSensitivity: () -> Unit = {},
    onNavigateToMindfulEating: () -> Unit = {},
    onNavigateToCalories: () -> Unit = {},
    onNavigateToProtein: () -> Unit = {},
    onNavigateToHunger: () -> Unit = {},
    onNavigateToMealTiming: () -> Unit = {},
    onNavigateToAlcohol: () -> Unit = {},
    onNavigateToMood: () -> Unit = {},
    onNavigateToEnergy: () -> Unit = {},
    onNavigateToVitamins: () -> Unit = {},
    onNavigateToSodium: () -> Unit = {},
    onNavigateToGutHealth: () -> Unit = {},
    onNavigateToSugar: () -> Unit = {},
    onNavigateToCaffeine: () -> Unit = {},
    onNavigateToInflammation: () -> Unit = {},
    onNavigateToCravings: () -> Unit = {},
    onNavigateToHydration: () -> Unit = {},
    onNavigateToNausea: () -> Unit = {},
    onNavigateToInjectionSites: () -> Unit = {}
) {
    var loggedMeals by remember { mutableStateOf(listOf<LogEntry>()) }
    var dialogCategory by remember { mutableStateOf<MealCategory?>(null) }

    val totalCalories = loggedMeals.sumOf { it.meal.calories }
    val totalProtein = loggedMeals.sumOf { it.meal.proteinGrams }

    dialogCategory?.let { category ->
        MealPickerDialog(
            category = category,
            onSelect = { meal ->
                loggedMeals = loggedMeals + LogEntry(meal = meal)
                dialogCategory = null
            },
            onDismiss = { dialogCategory = null }
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Today's Log", style = MaterialTheme.typography.headlineSmall)
                Button(
                    onClick = onNavigateToMealLog,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text("Log Meals")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToSideEffects, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.MoodBad, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Symptoms")
                    }
                    OutlinedButton(onClick = onNavigateToSupplements, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Medication, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Supplements")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToBloodSugar, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Bloodtype, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Blood Sugar")
                    }
                    OutlinedButton(onClick = onNavigateToJournal, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Create, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Journal")
                    }
                }
                OutlinedButton(onClick = onNavigateToSleep, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Bedtime, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 2.dp))
                    Text("Sleep Tracker")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToMacros, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.PieChart, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Macros")
                    }
                    OutlinedButton(onClick = onNavigateToStress, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.SentimentNeutral, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Stress")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToGlp1, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Biotech, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("GLP-1 Log")
                    }
                    OutlinedButton(onClick = onNavigateToFiber, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Grass, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Fiber")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToFoodSensitivity, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.NoFood, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Sensitivity")
                    }
                    OutlinedButton(onClick = onNavigateToMindfulEating, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.SelfImprovement, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Mindful")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToCalories, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Calories")
                    }
                    OutlinedButton(onClick = onNavigateToProtein, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Protein")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToHunger, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Dining, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Hunger")
                    }
                    OutlinedButton(onClick = onNavigateToMealTiming, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Meal Timing")
                    }
                }
                OutlinedButton(onClick = onNavigateToAlcohol, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.LocalBar, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 2.dp))
                    Text("Alcohol Tracker")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToMood, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Mood, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Mood")
                    }
                    OutlinedButton(onClick = onNavigateToEnergy, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Bolt, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Energy")
                    }
                }
                OutlinedButton(onClick = onNavigateToVitamins, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.HealthAndSafety, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 2.dp))
                    Text("Vitamins & Nutrients")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToSodium, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Opacity, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Sodium")
                    }
                    OutlinedButton(onClick = onNavigateToGutHealth, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Healing, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Gut Health")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToSugar, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Cake, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Sugar")
                    }
                    OutlinedButton(onClick = onNavigateToCaffeine, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.LocalCafe, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Caffeine")
                    }
                }
                OutlinedButton(onClick = onNavigateToInflammation, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Thermostat, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 2.dp))
                    Text("Inflammation Log")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToCravings, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Fastfood, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Cravings")
                    }
                    OutlinedButton(onClick = onNavigateToHydration, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.LocalDrink, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Hydration")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onNavigateToNausea, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Sick, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Nausea")
                    }
                    OutlinedButton(onClick = onNavigateToInjectionSites, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Vaccines, contentDescription = null)
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Injection Sites")
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$totalCalories", style = MaterialTheme.typography.headlineMedium)
                        Text("calories", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    VerticalDivider(modifier = Modifier.height(48.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${totalProtein}g",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("protein", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        MealCategory.entries.forEach { category ->
            item(key = category.name) {
                MealSection(
                    category = category,
                    entries = loggedMeals.filter { it.meal.category == category },
                    onAdd = { dialogCategory = category },
                    onRemove = { entry -> loggedMeals = loggedMeals.filter { it.id != entry.id } }
                )
            }
        }
    }
}

@Composable
private fun MealSection(
    category: MealCategory,
    entries: List<LogEntry>,
    onAdd: () -> Unit,
    onRemove: (LogEntry) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(category.displayName, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onAdd) {
                    Icon(Icons.Default.Add, contentDescription = "Add ${category.displayName}")
                }
            }
            if (entries.isEmpty()) {
                Text(
                    "Nothing logged yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                entries.forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.meal.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${entry.meal.calories} cal · ${entry.meal.proteinGrams}g protein",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onRemove(entry) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MealPickerDialog(
    category: MealCategory,
    onSelect: (Meal) -> Unit,
    onDismiss: () -> Unit
) {
    val meals = sampleMeals.filter { it.category == category }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add ${category.displayName}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                meals.forEach { meal ->
                    TextButton(
                        onClick = { onSelect(meal) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(meal.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${meal.calories} cal · ${meal.proteinGrams}g protein",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
