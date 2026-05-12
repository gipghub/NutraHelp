package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nutrahelp.data.Meal
import com.example.nutrahelp.data.MealCategory
import com.example.nutrahelp.data.sampleMeals

private data class LoggedEntry(val id: Long = System.nanoTime(), val meal: Meal)

private sealed interface MealDialog {
    data class Picker(val category: MealCategory) : MealDialog
    data class Custom(val category: MealCategory) : MealDialog
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealLogScreen(onBack: () -> Unit = {}) {
    var loggedEntries by remember { mutableStateOf(listOf<LoggedEntry>()) }
    var activeDialog by remember { mutableStateOf<MealDialog?>(null) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val calorieGoal = 1600
    val proteinGoal = 120

    val totalCalories = loggedEntries.sumOf { it.meal.calories }
    val totalProtein = loggedEntries.sumOf { it.meal.proteinGrams }

    // dialogs
    when (val dialog = activeDialog) {
        is MealDialog.Picker -> MealPickerDialog(
            category = dialog.category,
            onSelect = { meal ->
                loggedEntries = loggedEntries + LoggedEntry(meal = meal)
                activeDialog = null
            },
            onCustom = { activeDialog = MealDialog.Custom(dialog.category) },
            onDismiss = { activeDialog = null }
        )
        is MealDialog.Custom -> CustomMealDialog(
            category = dialog.category,
            onAdd = { meal ->
                loggedEntries = loggedEntries + LoggedEntry(meal = meal)
                activeDialog = null
            },
            onDismiss = { activeDialog = null }
        )
        null -> Unit
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Log") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            // Daily summary card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Today's Nutrition",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            NutrientStat(label = "Calories", value = "$totalCalories", goal = "$calorieGoal kcal")
                            NutrientStat(label = "Protein", value = "${totalProtein}g", goal = "${proteinGoal}g goal")
                            NutrientStat(
                                label = "Meals",
                                value = "${loggedEntries.size}",
                                goal = "logged today"
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Calories",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "$totalCalories / $calorieGoal kcal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            LinearProgressIndicator(
                                progress = { (totalCalories.toFloat() / calorieGoal).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Protein",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "${totalProtein}g / ${proteinGoal}g",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            LinearProgressIndicator(
                                progress = { (totalProtein.toFloat() / proteinGoal).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }

            // Tab row for meal categories
            item {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    MealCategory.entries.forEachIndexed { index, category ->
                        val count = loggedEntries.count { it.meal.category == category }
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    if (count > 0) "${category.displayName} ($count)" else category.displayName,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                    }
                }
            }

            // Current category section
            val currentCategory = MealCategory.entries[selectedTabIndex]
            val categoryEntries = loggedEntries.filter { it.meal.category == currentCategory }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(currentCategory.displayName, style = MaterialTheme.typography.titleMedium)
                                if (categoryEntries.isNotEmpty()) {
                                    Text(
                                        "${categoryEntries.sumOf { it.meal.calories }} cal · ${categoryEntries.sumOf { it.meal.proteinGrams }}g protein",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(onClick = { activeDialog = MealDialog.Picker(currentCategory) }) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add ${currentCategory.displayName}",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (categoryEntries.isEmpty()) {
                            Text(
                                "Nothing logged yet — tap + to add",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            categoryEntries.forEachIndexed { i, entry ->
                                if (i > 0) HorizontalDivider()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
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
                                    IconButton(onClick = {
                                        loggedEntries = loggedEntries.filter { it.id != entry.id }
                                    }) {
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

            // All logged meals summary (if any)
            if (loggedEntries.isNotEmpty()) {
                item {
                    Text(
                        "All Logged Items",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            loggedEntries.forEachIndexed { i, entry ->
                                if (i > 0) HorizontalDivider()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                entry.meal.category.displayName,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(Modifier.width(6.dp))
                                            Text(entry.meal.name, style = MaterialTheme.typography.bodyMedium)
                                        }
                                        Text(
                                            "${entry.meal.calories} cal · ${entry.meal.proteinGrams}g protein",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = {
                                        loggedEntries = loggedEntries.filter { it.id != entry.id }
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NutrientStat(label: String, value: String, goal: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            goal,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MealPickerDialog(
    category: MealCategory,
    onSelect: (Meal) -> Unit,
    onCustom: () -> Unit,
    onDismiss: () -> Unit
) {
    val meals = sampleMeals.filter { it.category == category }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add ${category.displayName}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(2.dp)
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
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                TextButton(onClick = onCustom, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Enter custom meal…")
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun CustomMealDialog(
    category: MealCategory,
    onAdd: (Meal) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Custom ${category.displayName}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it.filter { c -> c.isDigit() } },
                        label = { Text("Calories") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it.filter { c -> c.isDigit() } },
                        label = { Text("Protein (g)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            Meal(
                                name = name.trim(),
                                calories = calories.toIntOrNull() ?: 0,
                                proteinGrams = protein.toIntOrNull() ?: 0,
                                description = "",
                                category = category
                            )
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}