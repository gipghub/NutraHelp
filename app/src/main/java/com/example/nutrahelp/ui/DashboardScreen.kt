package com.example.nutrahelp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nutrahelp.data.MealCategory
import com.example.nutrahelp.data.sampleMeals
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    userName: String = "",
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMealLog: () -> Unit = {},
    onNavigateToWeightTracking: () -> Unit = {},
    onNavigateToFasting: () -> Unit = {},
    onNavigateToHabits: () -> Unit = {},
    onNavigateToWater: () -> Unit = {},
    onNavigateToDailyCheckIn: () -> Unit = {},
) {
    // Quick-log state
    var waterGlasses by remember { mutableIntStateOf(0) }
    var proteinGrams by remember { mutableIntStateOf(0) }
    var steps by remember { mutableIntStateOf(0) }
    var calories by remember { mutableIntStateOf(0) }

    val waterGoal = 8
    val proteinGoal = 120
    val stepsGoal = 8000
    val calorieGoal = 1600

    val today = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }
    val featuredMeal = remember { sampleMeals.filter { it.category == MealCategory.DINNER }.random() }
    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    val greetingText = if (userName.isNotBlank()) "$greeting, $userName!" else "$greeting!"

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(greetingText, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    Text(today, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // Daily Rings card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Today's Goals",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GoalRing(
                            label = "Calories",
                            value = calories,
                            goal = calorieGoal,
                            unit = "kcal",
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            size = 88.dp
                        )
                        GoalRing(
                            label = "Protein",
                            value = proteinGrams,
                            goal = proteinGoal,
                            unit = "g",
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
                            size = 88.dp
                        )
                        GoalRing(
                            label = "Water",
                            value = waterGlasses,
                            goal = waterGoal,
                            unit = "gl",
                            color = MaterialTheme.colorScheme.tertiary,
                            trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f),
                            size = 88.dp
                        )
                        GoalRing(
                            label = "Steps",
                            value = steps,
                            goal = stepsGoal,
                            unit = "k",
                            displayValue = if (steps >= 1000) "%.1f".format(steps / 1000f) else "$steps",
                            color = MaterialTheme.colorScheme.error,
                            trackColor = MaterialTheme.colorScheme.error.copy(alpha = 0.18f),
                            size = 88.dp
                        )
                    }
                }
            }
        }

        // Quick-log strip
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Quick Log", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickLogButton(
                            icon = Icons.Default.LocalDrink,
                            label = "Water",
                            value = "$waterGlasses / $waterGoal",
                            onAdd = { if (waterGlasses < waterGoal) waterGlasses++ },
                            onRemove = { if (waterGlasses > 0) waterGlasses-- },
                            modifier = Modifier.weight(1f)
                        )
                        QuickLogButton(
                            icon = Icons.Default.FitnessCenter,
                            label = "Protein",
                            value = "${proteinGrams}g",
                            onAdd = { proteinGrams += 10 },
                            onRemove = { if (proteinGrams >= 10) proteinGrams -= 10 },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickLogButton(
                            icon = Icons.Default.LocalFireDepartment,
                            label = "Calories",
                            value = "$calories kcal",
                            onAdd = { calories += 100 },
                            onRemove = { if (calories >= 100) calories -= 100 },
                            modifier = Modifier.weight(1f)
                        )
                        QuickLogButton(
                            icon = Icons.AutoMirrored.Filled.DirectionsRun,
                            label = "Steps",
                            value = "$steps",
                            onAdd = { steps += 500 },
                            onRemove = { if (steps >= 500) steps -= 500 },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Daily snapshot stats row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard("Calories", "$calories", "/ $calorieGoal kcal", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                StatCard("Protein", "${proteinGrams}g", "/ ${proteinGoal}g goal", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard("Water", "$waterGlasses gl", "/ $waterGoal glasses", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                StatCard("Steps", "$steps", "/ $stepsGoal goal", MaterialTheme.colorScheme.error, Modifier.weight(1f))
            }
        }

        // Quick-nav actions
        item {
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(Icons.Default.Restaurant, "Log Meals", onNavigateToMealLog, Modifier.weight(1f))
                ActionButton(Icons.Default.LocalFireDepartment, "Track Weight", onNavigateToWeightTracking, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(Icons.Default.Timer, "Fasting Timer", onNavigateToFasting, Modifier.weight(1f))
                ActionButton(Icons.AutoMirrored.Filled.EventNote, "Habit Tracker", onNavigateToHabits, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(Icons.Default.LocalDrink, "Water Log", onNavigateToWater, Modifier.weight(1f))
                ActionButton(Icons.Default.WbSunny, "Daily Check-in", onNavigateToDailyCheckIn, Modifier.weight(1f))
            }
        }

        // Tonight's suggestion
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Tonight's Suggestion", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(featuredMeal.name, style = MaterialTheme.typography.titleMedium)
                    Text(featuredMeal.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SuggestionChip(onClick = {}, label = { Text("${featuredMeal.calories} cal") })
                        SuggestionChip(onClick = {}, label = { Text("${featuredMeal.proteinGrams}g protein") })
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalRing(
    label: String,
    value: Int,
    goal: Int,
    unit: String,
    color: Color,
    trackColor: Color,
    size: Dp,
    displayValue: String = "$value",
) {
    val progress = (value.toFloat() / goal).coerceIn(0f, 1f)
    val strokeWidth = 8.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
            Canvas(modifier = Modifier.size(size)) {
                val sw = strokeWidth.toPx()
                val diameter = minOf(this.size.width, this.size.height) - sw
                val topLeft = Offset((this.size.width - diameter) / 2, (this.size.height - diameter) / 2)
                val arcSize = Size(diameter, diameter)
                // Track
                drawArc(color = trackColor, startAngle = -90f, sweepAngle = 360f, useCenter = false,
                    topLeft = topLeft, size = arcSize, style = Stroke(sw, cap = StrokeCap.Round))
                // Progress
                if (progress > 0f) {
                    drawArc(color = color, startAngle = -90f, sweepAngle = 360f * progress, useCenter = false,
                        topLeft = topLeft, size = arcSize, style = Stroke(sw, cap = StrokeCap.Round))
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(displayValue, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color)
                Text(unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun QuickLogButton(
    icon: ImageVector,
    label: String,
    value: String,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Text(label, style = MaterialTheme.typography.labelMedium)
            }
            Text(value, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                }
                Button(
                    onClick = onAdd,
                    modifier = Modifier.weight(1f).height(32.dp),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, sub: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Bold)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ActionButton(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier.height(48.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}