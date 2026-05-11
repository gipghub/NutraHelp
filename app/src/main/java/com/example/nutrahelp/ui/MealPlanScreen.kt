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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nutrahelp.data.Meal
import com.example.nutrahelp.data.MealCategory
import com.example.nutrahelp.data.sampleMeals

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    onNavigateToGrocery: () -> Unit = {},
    onNavigateToRecipes: () -> Unit = {},
    onNavigateToMealPrep: () -> Unit = {},
    onNavigateToRecipeCalc: () -> Unit = {},
    onNavigateToPortionGuide: () -> Unit = {}
) {
    val categories = MealCategory.entries
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(category.displayName) }
                )
            }
        }
        val meals = sampleMeals.filter { it.category == categories[selectedTab] }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                OutlinedButton(
                    onClick = onNavigateToRecipes,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text("View Recipes")
                }
            }
            item {
                OutlinedButton(
                    onClick = onNavigateToGrocery,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text("View Grocery List")
                }
            }
            item {
                OutlinedButton(
                    onClick = onNavigateToMealPrep,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Kitchen, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text("Meal Prep Planner")
                }
            }
            item {
                OutlinedButton(
                    onClick = onNavigateToRecipeCalc,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text("Recipe Nutrition Calculator")
                }
            }
            item {
                OutlinedButton(
                    onClick = onNavigateToPortionGuide,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Straighten, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text("Portion Size Guide")
                }
            }
            items(meals) { meal ->
                MealCard(meal)
            }
        }
    }
}

@Composable
private fun MealCard(meal: Meal) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(meal.name, style = MaterialTheme.typography.titleMedium)
            Text(
                meal.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(onClick = {}, label = { Text("${meal.calories} cal") })
                SuggestionChip(onClick = {}, label = { Text("${meal.proteinGrams}g protein") })
            }
        }
    }
}
