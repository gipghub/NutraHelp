package com.example.nutrahelp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class Recipe(
    val name: String,
    val prepMins: Int,
    val cookMins: Int,
    val servings: Int,
    val difficulty: String,
    val calories: Int,
    val proteinGrams: Int,
    val ingredients: List<String>,
    val steps: List<String>
)

private val recipes = listOf(
    Recipe(
        name = "Lemon Herb Baked Salmon",
        prepMins = 10, cookMins = 15, servings = 2, difficulty = "Easy",
        calories = 420, proteinGrams = 40,
        ingredients = listOf(
            "2 salmon fillets (6 oz each)",
            "1 tbsp extra-virgin olive oil",
            "2 tbsp fresh lemon juice",
            "2 cloves garlic, minced",
            "1 tbsp fresh dill or parsley, chopped",
            "Salt and pepper to taste",
            "2 cups broccoli florets"
        ),
        steps = listOf(
            "Preheat oven to 400°F (200°C). Line a baking sheet with parchment paper.",
            "Mix olive oil, lemon juice, garlic, and herbs. Brush over salmon fillets; season with salt and pepper.",
            "Toss broccoli with a drizzle of olive oil and spread around the salmon on the same sheet.",
            "Bake 12–15 minutes until salmon flakes easily and broccoli is tender-crisp.",
            "Serve immediately with lemon wedges."
        )
    ),
    Recipe(
        name = "Greek Chicken Bowl",
        prepMins = 15, cookMins = 20, servings = 2, difficulty = "Easy",
        calories = 400, proteinGrams = 38,
        ingredients = listOf(
            "2 chicken breasts (6 oz each)",
            "1 cup quinoa, cooked",
            "1 cucumber, diced",
            "1 cup cherry tomatoes, halved",
            "¼ red onion, thinly sliced",
            "¼ cup crumbled feta cheese",
            "2 tbsp olive oil",
            "1 tbsp lemon juice",
            "1 tsp dried oregano",
            "Salt and pepper to taste"
        ),
        steps = listOf(
            "Season chicken with oregano, salt, and pepper. Heat 1 tbsp oil in a skillet over medium-high.",
            "Cook chicken 6–7 minutes per side until internal temp reaches 165°F. Rest 5 minutes, then slice.",
            "Whisk remaining olive oil with lemon juice for dressing.",
            "Divide quinoa into bowls. Top with chicken, cucumber, tomatoes, onion, and feta.",
            "Drizzle with dressing and serve."
        )
    ),
    Recipe(
        name = "Veggie Egg Muffins",
        prepMins = 10, cookMins = 25, servings = 6, difficulty = "Easy",
        calories = 210, proteinGrams = 17,
        ingredients = listOf(
            "6 large eggs",
            "½ cup diced bell peppers",
            "½ cup baby spinach, roughly chopped",
            "¼ cup crumbled feta cheese",
            "2 tbsp diced red onion",
            "Salt, pepper, and garlic powder to taste"
        ),
        steps = listOf(
            "Preheat oven to 375°F (190°C). Grease a 6-cup muffin tin with cooking spray.",
            "Whisk eggs with salt, pepper, and a pinch of garlic powder.",
            "Divide veggies and feta evenly among the muffin cups.",
            "Pour egg mixture over the veggies until each cup is about ¾ full.",
            "Bake 20–25 minutes until eggs are set and lightly golden. Cool 5 minutes before removing."
        )
    ),
    Recipe(
        name = "Shrimp & Veggie Stir-Fry",
        prepMins = 10, cookMins = 10, servings = 2, difficulty = "Easy",
        calories = 360, proteinGrams = 32,
        ingredients = listOf(
            "1 lb shrimp, peeled and deveined",
            "2 cups bok choy, chopped",
            "1 red bell pepper, sliced",
            "2 cloves garlic, minced",
            "1 tsp fresh ginger, grated",
            "2 tbsp low-sodium soy sauce",
            "1 tsp sesame oil",
            "2 cups frozen cauliflower rice, cooked"
        ),
        steps = listOf(
            "Heat a large skillet or wok over high heat. Add sesame oil.",
            "Add garlic and ginger; stir-fry 30 seconds until fragrant.",
            "Add shrimp and cook 2–3 minutes per side until pink. Remove and set aside.",
            "Add bok choy and bell pepper to the pan; stir-fry 3–4 minutes until tender-crisp.",
            "Return shrimp to pan, add soy sauce, and toss to combine. Serve over cauliflower rice."
        )
    ),
    Recipe(
        name = "Turkey Meatballs & Zucchini Noodles",
        prepMins = 20, cookMins = 20, servings = 4, difficulty = "Medium",
        calories = 370, proteinGrams = 36,
        ingredients = listOf(
            "1 lb lean ground turkey",
            "1 egg",
            "2 tbsp whole-wheat breadcrumbs",
            "2 cloves garlic, minced",
            "1 tsp Italian seasoning",
            "3 medium zucchini, spiralized",
            "1½ cups low-sugar marinara sauce",
            "2 tbsp grated Parmesan",
            "Salt and pepper to taste"
        ),
        steps = listOf(
            "Mix turkey, egg, breadcrumbs, garlic, Italian seasoning, salt, and pepper. Roll into 1-inch balls.",
            "Heat a skillet over medium heat with a drizzle of olive oil. Brown meatballs on all sides, about 8 minutes.",
            "Add marinara sauce, cover, and simmer 10 minutes until meatballs are cooked through.",
            "Meanwhile, sauté zucchini noodles in a separate pan over medium-high heat for 2–3 minutes.",
            "Plate zucchini noodles, top with meatballs and sauce, and sprinkle with Parmesan."
        )
    ),
    Recipe(
        name = "Red Lentil & Vegetable Soup",
        prepMins = 10, cookMins = 30, servings = 4, difficulty = "Easy",
        calories = 320, proteinGrams = 18,
        ingredients = listOf(
            "1 cup red lentils, rinsed",
            "1 yellow onion, diced",
            "3 cloves garlic, minced",
            "2 carrots, diced",
            "2 stalks celery, diced",
            "1 can (14 oz) diced tomatoes, no salt added",
            "4 cups low-sodium vegetable broth",
            "1 tsp cumin",
            "½ tsp turmeric",
            "2 cups baby spinach"
        ),
        steps = listOf(
            "Sauté onion, garlic, carrots, and celery in a pot over medium heat until softened, about 5 minutes.",
            "Stir in cumin and turmeric; cook 1 minute until fragrant.",
            "Add lentils, diced tomatoes, and broth. Bring to a boil.",
            "Reduce heat and simmer 20–25 minutes until lentils are soft.",
            "Stir in spinach and cook until wilted. Season with pepper and serve."
        )
    ),
    Recipe(
        name = "Tuna-Stuffed Avocado",
        prepMins = 10, cookMins = 0, servings = 2, difficulty = "Easy",
        calories = 340, proteinGrams = 30,
        ingredients = listOf(
            "2 ripe avocados, halved and pitted",
            "2 cans (5 oz each) wild-caught tuna in water, drained",
            "2 stalks celery, finely diced",
            "1 tbsp Dijon mustard",
            "1 tbsp fresh lemon juice",
            "2 tbsp red onion, finely diced",
            "1 tbsp fresh parsley, chopped",
            "Salt and pepper to taste"
        ),
        steps = listOf(
            "Scoop a little extra avocado from each half to create more space; dice the scooped avocado.",
            "Mix tuna, celery, mustard, lemon juice, onion, parsley, and diced avocado until combined. Season with salt and pepper.",
            "Spoon tuna mixture generously into each avocado half and serve immediately."
        )
    ),
    Recipe(
        name = "Protein Overnight Oats",
        prepMins = 5, cookMins = 0, servings = 1, difficulty = "Easy",
        calories = 310, proteinGrams = 22,
        ingredients = listOf(
            "½ cup rolled oats",
            "1 scoop vanilla whey protein powder",
            "¾ cup unsweetened almond milk",
            "1 tbsp chia seeds",
            "¼ cup plain nonfat Greek yogurt",
            "½ cup mixed berries (fresh or frozen)",
            "½ tsp vanilla extract"
        ),
        steps = listOf(
            "Combine oats, protein powder, almond milk, chia seeds, and vanilla in a jar or container. Stir well.",
            "Fold in Greek yogurt until combined.",
            "Cover and refrigerate overnight (at least 6 hours).",
            "Top with berries before eating. Add a splash of almond milk if too thick."
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipesScreen(onBack: () -> Unit) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipes") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(recipes, key = { _, r -> r.name }) { index, recipe ->
                val expanded = expandedIndex == index
                RecipeCard(
                    recipe = recipe,
                    expanded = expanded,
                    onToggle = { expandedIndex = if (expanded) null else index }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecipeCard(recipe: Recipe, expanded: Boolean, onToggle: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.clickable(onClick = onToggle).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(recipe.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                InfoChip("${recipe.prepMins + recipe.cookMins} min")
                InfoChip("${recipe.servings} servings")
                InfoChip(recipe.difficulty)
                InfoChip("${recipe.calories} cal")
                InfoChip("${recipe.proteinGrams}g protein")
            }
            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider()
                    Text("Ingredients", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        recipe.ingredients.forEach { ingredient ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("•", color = MaterialTheme.colorScheme.primary)
                                Text(ingredient, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Text("Instructions", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recipe.steps.forEachIndexed { i, step ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "${i + 1}.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(step, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(label: String) {
    FilterChip(selected = false, onClick = {}, label = { Text(label, style = MaterialTheme.typography.labelSmall) })
}
