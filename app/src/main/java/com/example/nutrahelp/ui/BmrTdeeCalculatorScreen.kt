package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private data class ActivityLevel(val label: String, val description: String, val multiplier: Float)

private val activityLevels = listOf(
    ActivityLevel("Sedentary", "Little or no exercise", 1.2f),
    ActivityLevel("Lightly Active", "Exercise 1–3 days/week", 1.375f),
    ActivityLevel("Moderately Active", "Exercise 3–5 days/week", 1.55f),
    ActivityLevel("Very Active", "Hard exercise 6–7 days/week", 1.725f),
    ActivityLevel("Extra Active", "Physical job or 2× daily training", 1.9f)
)

private data class BmrResult(
    val bmr: Float,
    val tdee: Float,
    val deficit500: Float,
    val deficit1000: Float,
    val surplus250: Float
)

private fun calcMifflinBmr(
    weightKg: Float,
    heightCm: Float,
    ageYears: Int,
    isMale: Boolean
): Float {
    val base = (10f * weightKg) + (6.25f * heightCm) - (5f * ageYears)
    return if (isMale) base + 5f else base - 161f
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmrTdeeCalculatorScreen(onBack: () -> Unit) {
    var isMale by remember { mutableStateOf(true) }
    var useKg by remember { mutableStateOf(true) }
    var useCm by remember { mutableStateOf(true) }

    var weightInput by remember { mutableStateOf("") }
    var heightCmInput by remember { mutableStateOf("") }
    var heightFtInput by remember { mutableStateOf("") }
    var heightInInput by remember { mutableStateOf("") }
    var ageInput by remember { mutableStateOf("") }

    var activityExpanded by remember { mutableStateOf(false) }
    var selectedActivity by remember { mutableIntStateOf(0) }

    var result by remember { mutableStateOf<BmrResult?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BMR & TDEE Calculator") },
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
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Your Details", style = MaterialTheme.typography.titleMedium)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Sex", style = MaterialTheme.typography.labelMedium)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                SegmentedButton(
                                    selected = isMale,
                                    onClick = { isMale = true; result = null },
                                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                    label = { Text("Male") }
                                )
                                SegmentedButton(
                                    selected = !isMale,
                                    onClick = { isMale = false; result = null },
                                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                    label = { Text("Female") }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = ageInput,
                            onValueChange = { ageInput = it; formError = null; result = null },
                            label = { Text("Age (years)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Weight", style = MaterialTheme.typography.labelMedium)
                                SingleChoiceSegmentedButtonRow {
                                    SegmentedButton(
                                        selected = useKg,
                                        onClick = { useKg = true; weightInput = ""; result = null },
                                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                        label = { Text("kg") }
                                    )
                                    SegmentedButton(
                                        selected = !useKg,
                                        onClick = { useKg = false; weightInput = ""; result = null },
                                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                        label = { Text("lbs") }
                                    )
                                }
                            }
                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = { weightInput = it; formError = null; result = null },
                                label = { Text("Weight (${if (useKg) "kg" else "lbs"})") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Height", style = MaterialTheme.typography.labelMedium)
                                SingleChoiceSegmentedButtonRow {
                                    SegmentedButton(
                                        selected = useCm,
                                        onClick = { useCm = true; heightCmInput = ""; result = null },
                                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                        label = { Text("cm") }
                                    )
                                    SegmentedButton(
                                        selected = !useCm,
                                        onClick = { useCm = false; heightFtInput = ""; heightInInput = ""; result = null },
                                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                        label = { Text("ft/in") }
                                    )
                                }
                            }
                            if (useCm) {
                                OutlinedTextField(
                                    value = heightCmInput,
                                    onValueChange = { heightCmInput = it; formError = null; result = null },
                                    label = { Text("Height (cm)") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = heightFtInput,
                                        onValueChange = { heightFtInput = it; formError = null; result = null },
                                        label = { Text("ft") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = heightInInput,
                                        onValueChange = { heightInInput = it; formError = null; result = null },
                                        label = { Text("in") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Activity Level", style = MaterialTheme.typography.labelMedium)
                            ExposedDropdownMenuBox(
                                expanded = activityExpanded,
                                onExpandedChange = { activityExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = activityLevels[selectedActivity].label,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(
                                    expanded = activityExpanded,
                                    onDismissRequest = { activityExpanded = false }
                                ) {
                                    activityLevels.forEachIndexed { index, level ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(level.label, style = MaterialTheme.typography.bodyMedium)
                                                    Text(level.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            },
                                            onClick = {
                                                selectedActivity = index
                                                activityExpanded = false
                                                result = null
                                            }
                                        )
                                    }
                                }
                            }
                            Text(
                                activityLevels[selectedActivity].description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (formError != null) {
                            Text(
                                formError!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                val age = ageInput.toIntOrNull()
                                val weight = weightInput.toFloatOrNull()
                                val heightCm: Float? = if (useCm) {
                                    heightCmInput.toFloatOrNull()
                                } else {
                                    val ft = heightFtInput.toFloatOrNull()
                                    val inches = heightInInput.toFloatOrNull() ?: 0f
                                    if (ft != null) (ft * 12f + inches) * 2.54f else null
                                }
                                val weightKg = if (weight != null) {
                                    if (useKg) weight else weight * 0.453592f
                                } else null

                                when {
                                    age == null || age <= 0 -> formError = "Enter a valid age."
                                    weightKg == null || weightKg <= 0f -> formError = "Enter a valid weight."
                                    heightCm == null || heightCm <= 0f -> formError = "Enter a valid height."
                                    else -> {
                                        val bmr = calcMifflinBmr(weightKg, heightCm, age, isMale)
                                        val tdee = bmr * activityLevels[selectedActivity].multiplier
                                        result = BmrResult(
                                            bmr = bmr,
                                            tdee = tdee,
                                            deficit500 = tdee - 500f,
                                            deficit1000 = tdee - 1000f,
                                            surplus250 = tdee + 250f
                                        )
                                        formError = null
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Calculate") }
                    }
                }
            }

            result?.let { r ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Your Results", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "%.0f".format(r.bmr),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text("BMR (cal/day)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "%.0f".format(r.tdee),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text("TDEE (cal/day)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }

                            Text(
                                "BMR is calories burned at complete rest. TDEE is your total daily burn including activity.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            HorizontalDivider()

                            Text("Calorie Targets", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                            listOf(
                                Triple("Lose ~1 lb/week", r.deficit500, "500 cal deficit"),
                                Triple("Lose ~2 lbs/week", r.deficit1000, "1000 cal deficit"),
                                Triple("Maintain weight", r.tdee, "At TDEE"),
                                Triple("Gradual gain", r.surplus250, "250 cal surplus")
                            ).forEach { (goal, cals, note) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(goal, style = MaterialTheme.typography.bodyMedium)
                                        Text(note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                    Text(
                                        "%.0f cal".format(cals),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            HorizontalDivider()

                            Text(
                                "On GLP-1 medications, appetite suppression may naturally reduce intake. Work with your care team before pursuing deficits larger than 500 cal.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
