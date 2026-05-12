package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import java.util.Calendar

private enum class TipCategory(val label: String) {
    ALL("All"),
    EATING("Eating Habits"),
    NUTRITION("Nutrition"),
    LIFESTYLE("Lifestyle"),
    MEDICATION("Medication"),
    SIDE_EFFECTS("Side Effects"),
}

private data class NutritionTip(
    val icon: ImageVector,
    val title: String,
    val body: String,
    val category: TipCategory,
)

private val allTips = listOf(
    // Eating Habits
    NutritionTip(Icons.Default.Timer, "Eat Slowly", "Take at least 20–30 minutes per meal. GLP-1 slows gastric emptying — rushing leads to nausea, bloating, and overeating.", TipCategory.EATING),
    NutritionTip(Icons.Default.Restaurant, "Use Smaller Plates", "Downsizing your plate makes appropriate portions look full and helps recalibrate portion expectations.", TipCategory.EATING),
    NutritionTip(Icons.Default.Alarm, "Don't Skip Meals", "Even with reduced appetite, eating regularly maintains muscle, stabilizes blood sugar, and prevents rebound overeating.", TipCategory.EATING),
    NutritionTip(Icons.Default.NoFood, "Stop at First Fullness", "GLP-1 amplifies satiety signals. Stop eating at the first feeling of fullness — ignoring it causes nausea.", TipCategory.EATING),
    NutritionTip(Icons.Default.MoodBad, "Limit Alcohol", "Alcohol lowers blood sugar, worsens GLP-1 side effects, and adds empty calories. Keep it occasional and small.", TipCategory.EATING),
    NutritionTip(Icons.Default.Cake, "Reduce Sugary Foods", "High-glycaemic foods spike blood sugar and can cause reactive hypoglycaemia when combined with GLP-1. Choose whole foods.", TipCategory.EATING),
    NutritionTip(Icons.Default.Warning, "Avoid High-Fat Meals", "Very fatty meals are harder to empty from a slowed stomach and are a top trigger for nausea and vomiting on GLP-1.", TipCategory.EATING),
    NutritionTip(Icons.Default.Alarm, "Eat on a Schedule", "Regular mealtimes help your gut adapt and reduce unpredictable side effects. Aim for consistent breakfast, lunch, and dinner.", TipCategory.EATING),

    // Nutrition
    NutritionTip(Icons.Default.FitnessCenter, "Prioritize Protein", "Target 1 g of protein per pound of goal body weight daily. Adequate protein preserves muscle during rapid weight loss.", TipCategory.NUTRITION),
    NutritionTip(Icons.Default.WaterDrop, "Stay Hydrated", "Aim for 8–10 glasses of water daily. Dehydration is common because smaller meals mean less water from food.", TipCategory.NUTRITION),
    NutritionTip(Icons.Default.Grass, "Eat More Fiber", "Vegetables, legumes, and whole grains slow digestion, steady blood sugar, and feed beneficial gut bacteria.", TipCategory.NUTRITION),
    NutritionTip(Icons.Default.Scale, "Meet Minimum Calories", "Even with appetite suppression, eat at least 1,200 kcal/day (women) or 1,500 kcal (men) to meet micronutrient needs.", TipCategory.NUTRITION),
    NutritionTip(Icons.Default.HealthAndSafety, "Take a Multivitamin", "Small food intake can leave nutrient gaps. A daily multivitamin with iron, B12, and vitamin D covers the basics.", TipCategory.NUTRITION),
    NutritionTip(Icons.Default.Eco, "Choose Whole Foods", "Processed foods are calorie-dense but nutrient-poor. Fill your plate with lean proteins, vegetables, fruits, and whole grains.", TipCategory.NUTRITION),
    NutritionTip(Icons.Default.LocalFireDepartment, "Understand Macros", "Aim for roughly 40% protein, 35% carbs, 25% fat. Higher protein protects muscle; lower refined carbs reduces insulin spikes.", TipCategory.NUTRITION),
    NutritionTip(Icons.Default.LocalDrink, "Electrolytes Matter", "If you experience fatigue or cramps, you may be low in sodium, potassium, or magnesium — especially with low food intake.", TipCategory.NUTRITION),
    NutritionTip(Icons.Default.Restaurant, "Eat Protein First", "Starting your meal with protein and vegetables leaves less room for calorie-dense carbs and maximises satiety.", TipCategory.NUTRITION),
    NutritionTip(Icons.Default.Bolt, "Healthy Snacking", "High-protein snacks (Greek yogurt, hard-boiled eggs, cottage cheese) bridge meals and prevent energy crashes.", TipCategory.NUTRITION),

    // Lifestyle
    NutritionTip(Icons.AutoMirrored.Filled.DirectionsWalk, "Move Daily", "150 minutes of moderate exercise per week maximises fat loss while protecting muscle mass during GLP-1 therapy.", TipCategory.LIFESTYLE),
    NutritionTip(Icons.Default.FitnessCenter, "Add Strength Training", "Resistance exercise 2–3 × per week signals your body to retain muscle even in a significant calorie deficit.", TipCategory.LIFESTYLE),
    NutritionTip(Icons.Default.SelfImprovement, "Manage Stress", "Chronic stress raises cortisol, which stalls fat loss and drives cravings. Walking, meditation, or breathing exercises help.", TipCategory.LIFESTYLE),
    NutritionTip(Icons.Default.Bedtime, "Prioritize Sleep", "Sleep deprivation raises ghrelin (hunger hormone) and undercuts GLP-1 effectiveness. Aim for 7–9 hours per night.", TipCategory.LIFESTYLE),
    NutritionTip(Icons.Default.Psychology, "Build Mindful Habits", "Use smaller utensils, put down your fork between bites, and remove distractions while eating to stay in tune with fullness cues.", TipCategory.LIFESTYLE),
    NutritionTip(Icons.Default.Star, "Celebrate Non-Scale Wins", "Track energy levels, clothing fit, sleep quality, and blood sugar improvements — not just the number on the scale.", TipCategory.LIFESTYLE),
    NutritionTip(Icons.Default.LocalDrink, "Start Mornings with Water", "Drinking a glass of water before breakfast jumpstarts hydration and can help reduce the nausea some experience on waking.", TipCategory.LIFESTYLE),

    // Medication
    NutritionTip(Icons.Default.LocalPharmacy, "Inject Consistently", "Same day, same approximate time each week. Consistency maintains stable drug levels and reduces peaks and troughs.", TipCategory.MEDICATION),
    NutritionTip(Icons.Default.Medication, "Rotate Injection Sites", "Rotate between abdomen, thigh, and upper arm each week to prevent lipohypertrophy and maintain absorption.", TipCategory.MEDICATION),
    NutritionTip(Icons.Default.Warning, "Store Pens Properly", "Keep unopened pens refrigerated (2–8 °C). Once in use, most can be stored at room temperature for up to 56 days.", TipCategory.MEDICATION),
    NutritionTip(Icons.Default.HealthAndSafety, "Don't Double-Dose", "If you miss a dose and it's within 5 days, take it as soon as you remember. If more than 5 days, skip and resume your schedule.", TipCategory.MEDICATION),
    NutritionTip(Icons.Default.Alarm, "Set a Weekly Reminder", "A phone alarm on the same day each week removes the cognitive load of remembering your injection day.", TipCategory.MEDICATION),

    // Side Effects
    NutritionTip(Icons.Default.Sick, "Tackling Nausea", "Eat cold or room-temperature foods, avoid strong smells, sip ginger tea, and rest after meals to ease nausea.", TipCategory.SIDE_EFFECTS),
    NutritionTip(Icons.Default.Grass, "Relieve Constipation", "Increase water, fibre, and gentle movement. If persistent, ask your doctor about a stool softener — it is very common on GLP-1.", TipCategory.SIDE_EFFECTS),
    NutritionTip(Icons.Default.Warning, "Managing Fatigue", "Fatigue often means under-eating or dehydration. Track your intake and ensure you're meeting calorie and fluid minimums.", TipCategory.SIDE_EFFECTS),
    NutritionTip(Icons.Default.MoodBad, "Acid Reflux Relief", "Avoid eating within 3 hours of bedtime, stay upright after meals, and cut back on spicy, fatty, or acidic foods.", TipCategory.SIDE_EFFECTS),
    NutritionTip(Icons.Default.WaterDrop, "Preventing Hair Loss", "Rapid weight loss can trigger telogen effluvium. Adequate protein (≥ 80 g/day) and zinc significantly reduce shedding.", TipCategory.SIDE_EFFECTS),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TipsScreen() {
    var selectedCategory by remember { mutableStateOf(TipCategory.ALL) }

    val tipOfTheDay = remember {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        allTips[dayOfYear % allTips.size]
    }

    val visibleTips = remember(selectedCategory) {
        if (selectedCategory == TipCategory.ALL) allTips
        else allTips.filter { it.category == selectedCategory }
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Nutrition Tips", style = MaterialTheme.typography.headlineSmall)
            }
        }

        // Tip of the Day
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Text("Tip of the Day", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            tipOfTheDay.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(tipOfTheDay.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(tipOfTheDay.body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            SuggestionChip(
                                onClick = { selectedCategory = tipOfTheDay.category },
                                label = { Text(tipOfTheDay.category.label, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }
        }

        // Category filter chips
        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TipCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.label, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }
        }

        item {
            Text(
                "${visibleTips.size} tip${if (visibleTips.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(visibleTips, key = { it.title }) { tip ->
            TipCard(tip = tip, showCategory = selectedCategory == TipCategory.ALL)
        }
    }
}

@Composable
private fun TipCard(tip: NutritionTip, showCategory: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                tip.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (showCategory) {
                    Text(
                        tip.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(tip.title, style = MaterialTheme.typography.titleSmall)
                Text(
                    tip.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}