package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class PortionRef(
    val visual: String,
    val amount: String,
    val example: String
)

private data class FoodPortionRow(
    val food: String,
    val serving: String,
    val visual: String
)

private val handPortions = listOf(
    PortionRef("Palm", "3 oz / ~85g", "Cooked meat, fish, poultry"),
    PortionRef("Fist", "1 cup / ~240ml", "Pasta, rice, vegetables"),
    PortionRef("Cupped hand", "½ cup / ~120ml", "Cooked grains, fruit, legumes"),
    PortionRef("Thumb (tip to base)", "1 oz / ~28g", "Nut butter, cheese, dressings"),
    PortionRef("Fingertip", "1 tsp / ~5ml", "Oil, butter, mayo")
)

private val objectPortions = listOf(
    PortionRef("Deck of cards", "3 oz protein", "Chicken breast, beef, fish"),
    PortionRef("Tennis ball", "1 cup", "Fruit, cooked veggies, yogurt"),
    PortionRef("Golf ball", "2 tbsp / 1 oz", "Peanut butter, nuts, dips"),
    PortionRef("Baseball", "1 cup", "Salad, soup, cereal"),
    PortionRef("Poker chip", "1 tbsp", "Salad dressing, oil"),
    PortionRef("Checkbook", "3 oz fish", "Salmon fillet, tuna steak")
)

private val proteinPortions = listOf(
    FoodPortionRow("Chicken breast", "3 oz (palm)", "~27g protein, 140 cal"),
    FoodPortionRow("Eggs", "2 large", "~12g protein, 140 cal"),
    FoodPortionRow("Greek yogurt", "¾ cup", "~15g protein, 100 cal"),
    FoodPortionRow("Cottage cheese", "½ cup", "~14g protein, 90 cal"),
    FoodPortionRow("Tofu", "½ cup (fist)", "~10g protein, 90 cal"),
    FoodPortionRow("Salmon", "3 oz (deck of cards)", "~22g protein, 175 cal")
)

private val carbPortions = listOf(
    FoodPortionRow("Cooked rice", "⅓ cup (cupped hand)", "~15g carbs, 70 cal"),
    FoodPortionRow("Cooked pasta", "½ cup (fist)", "~20g carbs, 100 cal"),
    FoodPortionRow("Bread", "1 slice", "~15g carbs, 80 cal"),
    FoodPortionRow("Sweet potato", "½ medium (fist)", "~20g carbs, 90 cal"),
    FoodPortionRow("Oatmeal (cooked)", "½ cup", "~27g carbs, 150 cal"),
    FoodPortionRow("Banana", "1 medium", "~27g carbs, 105 cal")
)

private val fatPortions = listOf(
    FoodPortionRow("Avocado", "¼ whole", "~8g fat, 80 cal"),
    FoodPortionRow("Olive oil", "1 tsp (fingertip)", "~5g fat, 40 cal"),
    FoodPortionRow("Almonds", "1 oz (small handful)", "~14g fat, 160 cal"),
    FoodPortionRow("Peanut butter", "1 tbsp (thumb)", "~8g fat, 95 cal"),
    FoodPortionRow("Cheese", "1 oz (thumb)", "~9g fat, 110 cal"),
    FoodPortionRow("Butter", "1 tsp (fingertip)", "~4g fat, 35 cal")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortionSizeGuideScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portion Size Guide") },
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("GLP-1 Tip", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            "Smaller, more frequent portions help manage nausea and fullness on GLP-1 medications. Aim for protein at every meal and eat slowly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            item {
                SectionCard(title = "Your Hand as a Measuring Tool") {
                    handPortions.forEachIndexed { index, ref ->
                        if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        PortionRow(visual = ref.visual, amount = ref.amount, detail = ref.example)
                    }
                }
            }

            item {
                SectionCard(title = "Everyday Object Reference") {
                    objectPortions.forEachIndexed { index, ref ->
                        if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        PortionRow(visual = ref.visual, amount = ref.amount, detail = ref.example)
                    }
                }
            }

            item {
                SectionCard(title = "Protein Sources") {
                    proteinPortions.forEachIndexed { index, row ->
                        if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        FoodRow(row)
                    }
                }
            }

            item {
                SectionCard(title = "Carbohydrates") {
                    carbPortions.forEachIndexed { index, row ->
                        if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        FoodRow(row)
                    }
                }
            }

            item {
                SectionCard(title = "Healthy Fats") {
                    fatPortions.forEachIndexed { index, row ->
                        if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        FoodRow(row)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            content()
        }
    }
}

@Composable
private fun PortionRow(visual: String, amount: String, detail: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(visual, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            amount,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun FoodRow(row: FoodPortionRow) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(row.food, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(row.serving, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            row.visual,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
