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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    useMetric: Boolean = true,
    onUseMetricChange: (Boolean) -> Unit = {},
    themePreference: String = "System",
    onThemeChange: (String) -> Unit = {},
) {
    // Profile
    var displayName by remember { mutableStateOf("") }
    var primaryGoal by remember { mutableStateOf("Lose Weight") }

    // Daily goals
    var calorieGoal by remember { mutableStateOf("1600") }
    var proteinGoal by remember { mutableStateOf("120") }
    var waterGoal by remember { mutableStateOf("8") }
    var stepsGoal by remember { mutableStateOf("8000") }

    // Notifications
    var injectionReminder by remember { mutableStateOf(true) }
    var mealReminder by remember { mutableStateOf(false) }
    var waterReminder by remember { mutableStateOf(true) }
    var weightReminder by remember { mutableStateOf(false) }
    var habitReminder by remember { mutableStateOf(true) }

    // GLP-1
    var medicationType by remember { mutableStateOf("Semaglutide (Ozempic/Wegovy)") }
    var injectionDay by remember { mutableStateOf("Monday") }
    var medDropdownExpanded by remember { mutableStateOf(false) }
    var dayDropdownExpanded by remember { mutableStateOf(false) }

    // Data
    var clearAllDialog by remember { mutableStateOf(false) }

    val medications = listOf(
        "Semaglutide (Ozempic/Wegovy)",
        "Tirzepatide (Mounjaro/Zepbound)",
        "Dulaglutide (Trulicity)",
        "Liraglutide (Saxenda/Victoza)",
        "Exenatide (Byetta/Bydureon)",
        "Other",
    )
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val goalOptions = listOf("Lose Weight", "Maintain Weight", "Build Muscle", "Improve Health")

    if (clearAllDialog) {
        ConfirmDialog(
            title = "Clear All Data?",
            body = "This will permanently delete all logged meals, weight entries, and tracked data. This cannot be undone.",
            confirmLabel = "Delete All",
            onConfirm = { clearAllDialog = false },
            onDismiss = { clearAllDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            // ── Profile ──────────────────────────────────────────────────────
            item { SectionHeader(Icons.Default.Person, "Profile") }
            item {
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { Text("Display name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Primary Goal", style = MaterialTheme.typography.bodyMedium)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                goalOptions.take(2).forEachIndexed { i, label ->
                                    SegmentedButton(
                                        selected = primaryGoal == label,
                                        onClick = { primaryGoal = label },
                                        shape = SegmentedButtonDefaults.itemShape(i, 2),
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                goalOptions.drop(2).forEachIndexed { i, label ->
                                    SegmentedButton(
                                        selected = primaryGoal == label,
                                        onClick = { primaryGoal = label },
                                        shape = SegmentedButtonDefaults.itemShape(i, 2),
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Daily Goals ──────────────────────────────────────────────────
            item { SectionHeader(Icons.Default.Flag, "Daily Goals") }
            item {
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GoalField(
                                icon = Icons.Default.LocalFireDepartment,
                                label = "Calories (kcal)",
                                value = calorieGoal,
                                onValueChange = { calorieGoal = it },
                                modifier = Modifier.weight(1f)
                            )
                            GoalField(
                                icon = Icons.Default.FitnessCenter,
                                label = "Protein (g)",
                                value = proteinGoal,
                                onValueChange = { proteinGoal = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GoalField(
                                icon = Icons.Default.LocalDrink,
                                label = "Water (glasses)",
                                value = waterGoal,
                                onValueChange = { waterGoal = it },
                                modifier = Modifier.weight(1f)
                            )
                            GoalField(
                                icon = Icons.Default.Bolt,
                                label = "Steps",
                                value = stepsGoal,
                                onValueChange = { stepsGoal = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── GLP-1 Medication ─────────────────────────────────────────────
            item { SectionHeader(Icons.Default.Biotech, "GLP-1 Medication") }
            item {
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = medDropdownExpanded,
                            onExpandedChange = { medDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = medicationType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Medication") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(medDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = medDropdownExpanded,
                                onDismissRequest = { medDropdownExpanded = false }
                            ) {
                                medications.forEach { med ->
                                    DropdownMenuItem(
                                        text = { Text(med, style = MaterialTheme.typography.bodyMedium) },
                                        onClick = { medicationType = med; medDropdownExpanded = false },
                                        trailingIcon = if (medicationType == med) ({
                                            Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                                        }) else null
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = dayDropdownExpanded,
                            onExpandedChange = { dayDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = injectionDay,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Injection day") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dayDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = dayDropdownExpanded,
                                onDismissRequest = { dayDropdownExpanded = false }
                            ) {
                                daysOfWeek.forEach { day ->
                                    DropdownMenuItem(
                                        text = { Text(day, style = MaterialTheme.typography.bodyMedium) },
                                        onClick = { injectionDay = day; dayDropdownExpanded = false },
                                        trailingIcon = if (injectionDay == day) ({
                                            Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                                        }) else null
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Measurements ─────────────────────────────────────────────────
            item { SectionHeader(Icons.Default.Straighten, "Measurements") }
            item {
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Unit System", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Applies to weight, height, and body measurements across the app.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            listOf("Metric (kg, cm)" to true, "Standard (lbs, in)" to false).forEachIndexed { i, (label, isMetric) ->
                                SegmentedButton(
                                    selected = useMetric == isMetric,
                                    onClick = { onUseMetricChange(isMetric) },
                                    shape = SegmentedButtonDefaults.itemShape(i, 2),
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }
            }

            // ── Appearance ───────────────────────────────────────────────────
            item { SectionHeader(Icons.Default.ColorLens, "Appearance") }
            item {
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Theme", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            listOf("System", "Light", "Dark").forEachIndexed { i, label ->
                                SegmentedButton(
                                    selected = themePreference == label,
                                    onClick = { onThemeChange(label) },
                                    shape = SegmentedButtonDefaults.itemShape(i, 3),
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                }
            }

            // ── Notifications ─────────────────────────────────────────────────
            item { SectionHeader(Icons.Default.Notifications, "Notifications") }
            item {
                SettingsCard {
                    Column {
                        SwitchRow("Injection Day Reminder", "Remind me on my weekly injection day ($injectionDay)", injectionReminder) { injectionReminder = it }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        SwitchRow("Daily Meal Reminder", "Prompt to log meals at noon", mealReminder) { mealReminder = it }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        SwitchRow("Water Intake Reminder", "Remind me to drink water throughout the day", waterReminder) { waterReminder = it }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        SwitchRow("Weekly Weigh-In", "Remind me to log my weight each week", weightReminder) { weightReminder = it }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        SwitchRow("Daily Habit Check", "Evening prompt to review today's habits", habitReminder) { habitReminder = it }
                    }
                }
            }

            // ── Data ─────────────────────────────────────────────────────────
            item { SectionHeader(Icons.Default.DataObject, "Data & Privacy") }
            item {
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "All data is stored locally on your device and never shared.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = { clearAllDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Clear All App Data", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            // ── About ─────────────────────────────────────────────────────────
            item { SectionHeader(Icons.Default.Info, "About") }
            item {
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("NutraHelp", style = MaterialTheme.typography.bodyMedium)
                            Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Platform", style = MaterialTheme.typography.bodyMedium)
                            Text("Android", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Build", style = MaterialTheme.typography.bodyMedium)
                            Text("Jetpack Compose", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        HorizontalDivider()
                        Text(
                            "NutraHelp provides nutritional guidance for GLP-1 medication users. It is not a substitute for medical advice. Always consult your healthcare provider before making changes to your diet or medication.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Reusable composables ──────────────────────────────────────────────────────

@Composable
private fun SectionHeader(icon: ImageVector, title: String) {
    Column(modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        }
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun SwitchRow(label: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun GoalField(icon: ImageVector, label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

@Composable
private fun ConfirmDialog(title: String, body: String, confirmLabel: String = "Delete", onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}