package com.example.nutrahelp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val diaryMealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack", "Drink", "Other")

private data class DiaryEntry(
    val id: Long = System.nanoTime(),
    val time: String,
    val mealType: String,
    val foods: String,
    val calories: Int?,
    val protein: Int?,
    val hungerBefore: Int,   // 1–5
    val fullnessAfter: Int,  // 1–5
    val notes: String,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FoodDiaryScreen(onBack: () -> Unit) {
    // dateOffset: 0 = today, -1 = yesterday, etc.
    var dateOffset by remember { mutableIntStateOf(0) }
    var allEntries by remember { mutableStateOf(mapOf<Int, List<DiaryEntry>>()) }
    var showForm by remember { mutableStateOf(false) }

    // Form state
    var fTime by remember { mutableStateOf("") }
    var fMealType by remember { mutableStateOf(diaryMealTypes[0]) }
    var fFoods by remember { mutableStateOf("") }
    var fCalories by remember { mutableStateOf("") }
    var fProtein by remember { mutableStateOf("") }
    var fHunger by remember { mutableIntStateOf(3) }
    var fFullness by remember { mutableIntStateOf(3) }
    var fNotes by remember { mutableStateOf("") }
    var fError by remember { mutableStateOf(false) }

    val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, dateOffset) }
    val dateLabel = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(cal.time)
    val dayLabel = when (dateOffset) {
        0 -> "Today"
        -1 -> "Yesterday"
        else -> null
    }

    val todayEntries = allEntries[dateOffset] ?: emptyList()
    val totalCal = todayEntries.mapNotNull { it.calories }.sum()
    val totalProtein = todayEntries.mapNotNull { it.protein }.sum()

    fun resetForm() {
        fTime = ""; fMealType = diaryMealTypes[0]; fFoods = ""; fCalories = ""
        fProtein = ""; fHunger = 3; fFullness = 3; fNotes = ""; fError = false
        showForm = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Diary") },
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
            // ── Date navigator ────────────────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { dateOffset--; showForm = false }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous day")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (dayLabel != null) {
                                Text(dayLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(dateLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (dateOffset < 0) {
                                TextButton(onClick = { dateOffset = 0; showForm = false }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                                    Text("Today", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                            IconButton(
                                onClick = { if (dateOffset < 0) { dateOffset++; showForm = false } },
                                enabled = dateOffset < 0
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next day")
                            }
                        }
                    }
                }
            }

            // ── Daily summary ─────────────────────────────────────────────────
            if (todayEntries.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            DiaryStat("${todayEntries.size}", "Entries", Icons.Default.MenuBook)
                            Box(Modifier.width(1.dp).height(40.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
                            DiaryStat(
                                if (totalCal > 0) "${totalCal} kcal" else "—",
                                "Calories",
                                Icons.Default.LocalFireDepartment
                            )
                            Box(Modifier.width(1.dp).height(40.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
                            DiaryStat(
                                if (totalProtein > 0) "${totalProtein}g" else "—",
                                "Protein",
                                null
                            )
                        }
                    }
                }
            }

            // ── Add entry header ──────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (todayEntries.isEmpty()) "No entries yet" else "Entries (${todayEntries.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = { showForm = !showForm; if (!showForm) resetForm() }) {
                        Icon(if (showForm) Icons.Default.Close else Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (showForm) "Cancel" else "Add Entry")
                    }
                }
            }

            // ── Entry form ────────────────────────────────────────────────────
            if (showForm) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("New Diary Entry", style = MaterialTheme.typography.titleSmall)

                            // Time + meal type
                            OutlinedTextField(
                                value = fTime,
                                onValueChange = { fTime = it },
                                label = { Text("Time (e.g. 8:30 AM)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Meal type chips
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Meal Type", style = MaterialTheme.typography.labelMedium)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    diaryMealTypes.forEach { type ->
                                        FilterChip(
                                            selected = fMealType == type,
                                            onClick = { fMealType = type },
                                            label = { Text(type, style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }
                            }

                            // Foods eaten
                            OutlinedTextField(
                                value = fFoods,
                                onValueChange = { fFoods = it; fError = false },
                                label = { Text("What did you eat / drink?") },
                                minLines = 2,
                                maxLines = 4,
                                isError = fError && fFoods.isBlank(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Calories + protein
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = fCalories,
                                    onValueChange = { fCalories = it.filter { c -> c.isDigit() } },
                                    label = { Text("Calories (kcal)") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = fProtein,
                                    onValueChange = { fProtein = it.filter { c -> c.isDigit() } },
                                    label = { Text("Protein (g)") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Hunger before
                            RatingRow(
                                label = "Hunger before",
                                value = fHunger,
                                onValueChange = { fHunger = it },
                                lowLabel = "Not hungry",
                                highLabel = "Starving"
                            )

                            // Fullness after
                            RatingRow(
                                label = "Fullness after",
                                value = fFullness,
                                onValueChange = { fFullness = it },
                                lowLabel = "Still hungry",
                                highLabel = "Stuffed"
                            )

                            // Notes
                            OutlinedTextField(
                                value = fNotes,
                                onValueChange = { fNotes = it },
                                label = { Text("Notes / How did you feel? (optional)") },
                                minLines = 2,
                                maxLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (fError) {
                                Text("Please describe what you ate.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            }

                            Button(
                                onClick = {
                                    if (fFoods.isBlank()) {
                                        fError = true
                                    } else {
                                        val entry = DiaryEntry(
                                            time = fTime.trim(),
                                            mealType = fMealType,
                                            foods = fFoods.trim(),
                                            calories = fCalories.toIntOrNull(),
                                            protein = fProtein.toIntOrNull(),
                                            hungerBefore = fHunger,
                                            fullnessAfter = fFullness,
                                            notes = fNotes.trim()
                                        )
                                        val updated = (allEntries[dateOffset] ?: emptyList()) + entry
                                        allEntries = allEntries + (dateOffset to updated)
                                        resetForm()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Entry")
                            }
                        }
                    }
                }
            }

            // ── Entries timeline ──────────────────────────────────────────────
            if (todayEntries.isEmpty() && !showForm) {
                item {
                    Text(
                        "No food logged for this day. Tap \"Add Entry\" to start your diary.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else {
                items(todayEntries, key = { it.id }) { entry ->
                    DiaryEntryCard(
                        entry = entry,
                        onDelete = {
                            val updated = (allEntries[dateOffset] ?: emptyList()).filter { it.id != entry.id }
                            allEntries = allEntries + (dateOffset to updated)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaryStat(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (icon != null) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
    }
}

@Composable
private fun RatingRow(label: String, value: Int, onValueChange: (Int) -> Unit, lowLabel: String, highLabel: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(lowLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { rating ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (value == rating) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (value == rating) 0.dp else 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                            .clickable { onValueChange(rating) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            rating.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (value == rating) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Text(highLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        }
    }
}

@Composable
private fun DiaryEntryCard(entry: DiaryEntry, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                entry.mealType,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (entry.time.isNotBlank()) {
                            Text(entry.time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(entry.foods, style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Nutrition info
            if (entry.calories != null || entry.protein != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (entry.calories != null) {
                        Text(
                            "${entry.calories} kcal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (entry.protein != null) {
                        Text(
                            "${entry.protein}g protein",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Hunger / fullness
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HungerFullnessIndicator("Hunger", entry.hungerBefore)
                HungerFullnessIndicator("Fullness", entry.fullnessAfter)
            }

            // Notes
            if (entry.notes.isNotBlank()) {
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Text(entry.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun HungerFullnessIndicator(label: String, value: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("$label:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            (1..5).forEach { i ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (i <= value) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
        Text("$value/5", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}