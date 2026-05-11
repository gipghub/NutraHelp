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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

private val groceryItems = linkedMapOf(
    "Produce" to listOf(
        "Mixed salad greens", "Baby spinach", "Cherry tomatoes", "Cucumber",
        "Bell peppers (red & yellow)", "Bok choy", "Broccoli florets",
        "Fresh berries (blueberries, strawberries)", "Banana", "Avocado",
        "Zucchini", "Asparagus", "Brussels sprouts", "Cauliflower",
        "Sweet potato", "Kale", "Celery", "Romaine lettuce",
        "Lemon", "Lime", "Garlic", "Fresh ginger", "Yellow onion",
        "Peaches", "Apples", "Edamame (frozen, shelled)"
    ),
    "Protein" to listOf(
        "Chicken breast (boneless, skinless)", "Lean ground turkey",
        "Sirloin steak", "Salmon fillet", "Eggs (1 dozen)",
        "Silken tofu", "Whey protein powder",
        "Shrimp (peeled & deveined)", "Cod or tilapia fillet",
        "Canned wild-caught tuna (in water)", "Smoked salmon",
        "Deli turkey slices (low-sodium)", "Chicken thighs (skinless)",
        "Black beans (canned, low-sodium)", "Kidney beans (canned)",
        "Chickpeas (canned)", "Protein bars (≥15g protein, low sugar)"
    ),
    "Dairy & Alternatives" to listOf(
        "Plain nonfat Greek yogurt", "Low-fat cottage cheese",
        "String cheese sticks", "Unsweetened almond milk",
        "Low-fat cream cheese", "Feta cheese (crumbled)",
        "Parmesan cheese (grated)", "Unsweetened oat milk"
    ),
    "Grains & Legumes" to listOf(
        "Quinoa", "Brown rice", "Low-sugar granola",
        "Green or brown lentils", "Chia seeds",
        "Rolled oats", "Whole-wheat tortillas (small)",
        "Whole-grain bread", "Corn tortillas", "Brown rice cakes"
    ),
    "Pantry" to listOf(
        "Raw almonds", "Extra-virgin olive oil", "Light vinaigrette dressing",
        "Low-sodium soy sauce", "Dried herbs & spices",
        "Natural peanut butter", "Hummus (store-bought)",
        "Capers", "Dijon mustard", "Apple cider vinegar",
        "Ponzu sauce", "Smoked paprika", "Garlic powder",
        "Red pepper flakes", "Chicken or vegetable broth (low-sodium)",
        "Canned diced tomatoes (no salt added)", "Tomato marinara (low-sugar)",
        "Chia seeds", "Hemp seeds", "Dill pickles"
    ),
    "Frozen" to listOf(
        "Frozen mixed vegetables", "Frozen cauliflower rice",
        "Frozen edamame (shelled)", "Frozen berries",
        "Frozen shrimp", "Frozen broccoli florets"
    ),
    "Beverages" to listOf(
        "Sparkling water (unsweetened)", "Green tea bags",
        "Herbal tea (ginger or peppermint — good for nausea)",
        "Electrolyte packets (no sugar)", "Black coffee"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListScreen(onBack: () -> Unit) {
    var checkedItems by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grocery List") },
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
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            groceryItems.forEach { (category, items) ->
                item(key = category) {
                    Text(
                        category,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                    HorizontalDivider()
                }
                items(items, key = { it }) { item ->
                    val checked = item in checkedItems
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                checkedItems = if (isChecked) checkedItems + item else checkedItems - item
                            }
                        )
                        Column {
                            Text(
                                item,
                                style = MaterialTheme.typography.bodyMedium,
                                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
