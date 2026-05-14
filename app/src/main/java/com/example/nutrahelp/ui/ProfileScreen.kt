package com.example.nutrahelp.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.viewmodel.ProfileState
import com.example.nutrahelp.viewmodel.ProfileViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private val medications = listOf(
    "Semaglutide (Ozempic)",
    "Semaglutide (Wegovy)",
    "Tirzepatide (Mounjaro)",
    "Tirzepatide (Zepbound)",
    "Liraglutide (Victoza)",
    "Liraglutide (Saxenda)",
    "Dulaglutide (Trulicity)",
    "Semaglutide (Rybelsus)",
    "Other",
)
private val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
private val sexOptions = listOf("Male", "Female", "Prefer not to say")
private val goalOptions = listOf("Lose Weight", "Maintain Weight", "Build Muscle", "Improve Health")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToMedication: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    vm: ProfileViewModel = viewModel(),
) {
    val saved0 = vm.state.value

    // Personal info
    var name by remember { mutableStateOf(saved0.name) }
    var age by remember { mutableStateOf(saved0.age) }
    var sex by remember { mutableStateOf(saved0.sex) }
    var sexExpanded by remember { mutableStateOf(false) }
    var primaryGoal by remember { mutableStateOf(saved0.primaryGoal) }
    var goalExpanded by remember { mutableStateOf(false) }

    // Body metrics
    val useMetric = LocalUseMetric.current
    var heightCm by remember { mutableStateOf(saved0.heightCm) }
    var currentWeight by remember { mutableStateOf(saved0.currentWeight) }
    var startingWeight by remember { mutableStateOf(saved0.startingWeight) }
    var goalWeight by remember { mutableStateOf(saved0.goalWeight) }

    // Medication
    var medication by remember { mutableStateOf(saved0.medication) }
    var medExpanded by remember { mutableStateOf(false) }
    var dose by remember { mutableStateOf(saved0.dose) }
    var injectionDay by remember { mutableStateOf(saved0.injectionDay) }
    var dayExpanded by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(saved0.startDate) }
    var weeksOnMed by remember { mutableStateOf(saved0.weeksOnMed) }

    // Goals
    var calorieGoal by remember { mutableStateOf(saved0.calorieGoal) }
    var proteinGoal by remember { mutableStateOf(saved0.proteinGoal) }

    var saved by remember { mutableStateOf(false) }

    // Derived
    val initials = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }.ifEmpty { "?" }

    val weightUnit = if (useMetric) "kg" else "lbs"
    val heightUnit = if (useMetric) "cm" else "in"

    val bmi: Float? = run {
        val h = heightCm.toFloatOrNull() ?: return@run null
        val w = currentWeight.toFloatOrNull() ?: return@run null
        if (h <= 0f) return@run null
        if (useMetric) w / ((h / 100f) * (h / 100f))
        else 703f * w / (h * h)
    }
    val bmiCategory = bmi?.let {
        when {
            it < 18.5f -> "Underweight"
            it < 25f -> "Healthy"
            it < 30f -> "Overweight"
            else -> "Obese"
        }
    }

    val toGoal: Float? = run {
        val c = currentWeight.toFloatOrNull() ?: return@run null
        val g = goalWeight.toFloatOrNull() ?: return@run null
        c - g
    }
    val lostSoFar: Float? = run {
        val s = startingWeight.toFloatOrNull() ?: return@run null
        val c = currentWeight.toFloatOrNull() ?: return@run null
        s - c
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            // ── Avatar header ─────────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                initials,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            name.ifBlank { "Set your name below" },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            primaryGoal,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // ── Health snapshot ───────────────────────────────────────────────
            if (currentWeight.isNotBlank() || goalWeight.isNotBlank() || bmi != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (currentWeight.isNotBlank()) {
                            SnapshotCard("Current", "${currentWeight} $weightUnit", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                        }
                        if (goalWeight.isNotBlank()) {
                            SnapshotCard("Goal", "${goalWeight} $weightUnit", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                        }
                        if (toGoal != null && toGoal > 0f) {
                            SnapshotCard("To Go", "${"%.1f".format(toGoal)} $weightUnit", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                        }
                    }
                }
                if (bmi != null) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SnapshotCard("BMI", "%.1f".format(bmi), MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                            if (bmiCategory != null) SnapshotCard("Category", bmiCategory, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                            if (lostSoFar != null && lostSoFar > 0f) SnapshotCard("Lost", "${"%.1f".format(lostSoFar)} $weightUnit", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                        }
                    }
                }
            }

            // ── Personal info ─────────────────────────────────────────────────
            item { ProfileSectionHeader(Icons.Default.Person, "Personal Info") }
            item {
                ProfileCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; saved = false },
                            label = { Text("Full name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = age,
                                onValueChange = { age = it.filter { c -> c.isDigit() }; saved = false },
                                label = { Text("Age") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            ExposedDropdownMenuBox(
                                expanded = sexExpanded,
                                onExpandedChange = { sexExpanded = it },
                                modifier = Modifier.weight(2f)
                            ) {
                                OutlinedTextField(
                                    value = sex,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Sex") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(sexExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(sexExpanded, { sexExpanded = false }) {
                                    sexOptions.forEach { opt ->
                                        DropdownMenuItem(
                                            text = { Text(opt) },
                                            onClick = { sex = opt; sexExpanded = false; saved = false },
                                            trailingIcon = if (sex == opt) ({ Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }) else null
                                        )
                                    }
                                }
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Primary Goal", style = MaterialTheme.typography.bodyMedium)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                goalOptions.take(2).forEachIndexed { i, label ->
                                    SegmentedButton(
                                        selected = primaryGoal == label,
                                        onClick = { primaryGoal = label; saved = false },
                                        shape = SegmentedButtonDefaults.itemShape(i, 2),
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                goalOptions.drop(2).forEachIndexed { i, label ->
                                    SegmentedButton(
                                        selected = primaryGoal == label,
                                        onClick = { primaryGoal = label; saved = false },
                                        shape = SegmentedButtonDefaults.itemShape(i, 2),
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Body metrics ──────────────────────────────────────────────────
            item { ProfileSectionHeader(Icons.Default.Straighten, "Body Metrics") }
            item {
                ProfileCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = heightCm,
                            onValueChange = { heightCm = it.filter { c -> c.isDigit() || c == '.' }; saved = false },
                            label = { Text("Height ($heightUnit)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = startingWeight,
                                onValueChange = { startingWeight = it.filter { c -> c.isDigit() || c == '.' }; saved = false },
                                label = { Text("Starting ($weightUnit)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = currentWeight,
                                onValueChange = { currentWeight = it.filter { c -> c.isDigit() || c == '.' }; saved = false },
                                label = { Text("Current ($weightUnit)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = goalWeight,
                            onValueChange = { goalWeight = it.filter { c -> c.isDigit() || c == '.' }; saved = false },
                            label = { Text("Goal weight ($weightUnit)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ── Medication ────────────────────────────────────────────────────
            item { ProfileSectionHeader(Icons.Default.Biotech, "GLP-1 Medication") }
            item {
                ProfileCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ExposedDropdownMenuBox(medExpanded, { medExpanded = it }) {
                            OutlinedTextField(
                                value = medication,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Medication") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(medExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(medExpanded, { medExpanded = false }) {
                                medications.forEach { med ->
                                    DropdownMenuItem(
                                        text = { Text(med) },
                                        onClick = { medication = med; medExpanded = false; saved = false },
                                        trailingIcon = if (medication == med) ({ Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }) else null
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = dose,
                                onValueChange = { dose = it; saved = false },
                                label = { Text("Dose (e.g. 0.5 mg)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            ExposedDropdownMenuBox(dayExpanded, { dayExpanded = it }, modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = injectionDay,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Injection day") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dayExpanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(dayExpanded, { dayExpanded = false }) {
                                    daysOfWeek.forEach { day ->
                                        DropdownMenuItem(
                                            text = { Text(day) },
                                            onClick = { injectionDay = day; dayExpanded = false; saved = false },
                                            trailingIcon = if (injectionDay == day) ({ Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }) else null
                                        )
                                    }
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = startDate,
                                onValueChange = { startDate = it; saved = false },
                                label = { Text("Start date (MM/DD/YYYY)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = weeksOnMed,
                                onValueChange = { weeksOnMed = it.filter { c -> c.isDigit() }; saved = false },
                                label = { Text("Weeks on med") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = onNavigateToMedication, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Med History", style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(onClick = onNavigateToAppointments, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Appointments", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }

            // ── Nutrition goals ───────────────────────────────────────────────
            item { ProfileSectionHeader(Icons.Default.Flag, "Nutrition Goals") }
            item {
                ProfileCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = calorieGoal,
                                onValueChange = { calorieGoal = it.filter { c -> c.isDigit() }; saved = false },
                                label = { Text("Calories (kcal)") },
                                leadingIcon = { Icon(Icons.Default.LocalFireDepartment, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = proteinGoal,
                                onValueChange = { proteinGoal = it.filter { c -> c.isDigit() }; saved = false },
                                label = { Text("Protein (g)") },
                                leadingIcon = { Icon(Icons.Default.FitnessCenter, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Save button ───────────────────────────────────────────────────
            item {
                Button(
                    onClick = {
                        vm.save(ProfileState(
                            name = name, age = age, sex = sex, primaryGoal = primaryGoal,
                            heightCm = heightCm, currentWeight = currentWeight,
                            startingWeight = startingWeight, goalWeight = goalWeight,
                            medication = medication, dose = dose, injectionDay = injectionDay,
                            startDate = startDate, weeksOnMed = weeksOnMed,
                            calorieGoal = calorieGoal, proteinGoal = proteinGoal,
                        ))
                        saved = true
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    if (saved) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    else Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (saved) "Profile Saved" else "Save Profile")
                }
            }
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────

@Composable
private fun SnapshotCard(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ProfileSectionHeader(icon: ImageVector, title: String) {
    Column(modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        }
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun ProfileCard(content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}