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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private data class BmiResult(
    val bmi: Float,
    val category: String,
    val minHealthyWeight: Float,
    val maxHealthyWeight: Float,
    val maintenanceCalories: Int,
    val deficitCalories: Int,
    val proteinGoal: Int,
    val unit: String
)

private fun bmiCategory(bmi: Float) = when {
    bmi < 18.5f -> "Underweight"
    bmi < 25f   -> "Normal weight"
    bmi < 30f   -> "Overweight"
    else        -> "Obese"
}

private fun calculate(
    weightStr: String, useLbs: Boolean,
    heightCmStr: String, feetStr: String, inchesStr: String, useFtIn: Boolean,
    ageStr: String, isMale: Boolean
): BmiResult? {
    val weightKg = if (useLbs) (weightStr.toFloatOrNull() ?: return null) / 2.2046f
                   else weightStr.toFloatOrNull() ?: return null
    val heightCm = if (useFtIn) {
        val ft = feetStr.toFloatOrNull() ?: return null
        val inch = inchesStr.toFloatOrNull() ?: 0f
        (ft * 12f + inch) * 2.54f
    } else {
        heightCmStr.toFloatOrNull() ?: return null
    }
    val age = ageStr.toIntOrNull() ?: return null
    if (weightKg <= 0f || heightCm <= 0f || age <= 0) return null

    val heightM = heightCm / 100f
    val bmi = weightKg / (heightM * heightM)

    val minKg = 18.5f * heightM * heightM
    val maxKg = 24.9f * heightM * heightM
    val bmr = if (isMale)
        (10f * weightKg + 6.25f * heightCm - 5f * age + 5f)
    else
        (10f * weightKg + 6.25f * heightCm - 5f * age - 161f)
    val tdee = (bmr * 1.2f).roundToInt()
    val deficit = maxOf(1200, tdee - 500)
    val proteinGoal = (weightKg * 2.2f).roundToInt()

    val (minDisplay, maxDisplay) = if (useLbs)
        minKg * 2.2046f to maxKg * 2.2046f
    else
        minKg to maxKg
    val displayUnit = if (useLbs) "lbs" else "kg"

    return BmiResult(bmi, bmiCategory(bmi), minDisplay, maxDisplay, tdee, deficit, proteinGoal, displayUnit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiStatsScreen(onBack: () -> Unit) {
    var useLbs by remember { mutableStateOf(true) }
    var useFtIn by remember { mutableStateOf(true) }
    var weight by remember { mutableStateOf("") }
    var feet by remember { mutableStateOf("") }
    var inches by remember { mutableStateOf("") }
    var heightCm by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var isMale by remember { mutableStateOf(true) }
    var result by remember { mutableStateOf<BmiResult?>(null) }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BMI & Stats") },
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
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Your Measurements", style = MaterialTheme.typography.titleMedium)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Weight Unit", style = MaterialTheme.typography.labelMedium)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                listOf("lbs", "kg").forEachIndexed { i, label ->
                                    SegmentedButton(
                                        selected = (i == 0) == useLbs,
                                        onClick = { useLbs = i == 0 },
                                        shape = SegmentedButtonDefaults.itemShape(index = i, count = 2),
                                        label = { Text(label) }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it; showError = false },
                            label = { Text("Weight (${if (useLbs) "lbs" else "kg"})") },
                            singleLine = true,
                            isError = showError && weight.isBlank(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Height Unit", style = MaterialTheme.typography.labelMedium)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                listOf("ft / in", "cm").forEachIndexed { i, label ->
                                    SegmentedButton(
                                        selected = (i == 0) == useFtIn,
                                        onClick = { useFtIn = i == 0 },
                                        shape = SegmentedButtonDefaults.itemShape(index = i, count = 2),
                                        label = { Text(label) }
                                    )
                                }
                            }
                        }

                        if (useFtIn) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = feet,
                                    onValueChange = { feet = it; showError = false },
                                    label = { Text("Feet") },
                                    singleLine = true,
                                    isError = showError && feet.isBlank(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = inches,
                                    onValueChange = { inches = it },
                                    label = { Text("Inches") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = heightCm,
                                onValueChange = { heightCm = it; showError = false },
                                label = { Text("Height (cm)") },
                                singleLine = true,
                                isError = showError && heightCm.isBlank(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = age,
                                onValueChange = { age = it; showError = false },
                                label = { Text("Age") },
                                singleLine = true,
                                isError = showError && age.isBlank(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Sex", style = MaterialTheme.typography.labelMedium)
                                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                    listOf("Male", "Female").forEachIndexed { i, label ->
                                        SegmentedButton(
                                            selected = (i == 0) == isMale,
                                            onClick = { isMale = i == 0 },
                                            shape = SegmentedButtonDefaults.itemShape(index = i, count = 2),
                                            label = { Text(label) }
                                        )
                                    }
                                }
                            }
                        }

                        if (showError) {
                            Text(
                                "Please fill in all required fields.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                val r = calculate(weight, useLbs, heightCm, feet, inches, useFtIn, age, isMale)
                                if (r == null) showError = true else { result = r; showError = false }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Calculate")
                        }
                    }
                }
            }

            result?.let { r ->
                item {
                    val categoryColor = when (r.category) {
                        "Normal weight" -> MaterialTheme.colorScheme.tertiary
                        "Underweight"   -> MaterialTheme.colorScheme.secondary
                        else            -> MaterialTheme.colorScheme.error
                    }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Your Results", style = MaterialTheme.typography.titleMedium)
                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("BMI", style = MaterialTheme.typography.labelMedium)
                                    Text("%.1f".format(r.bmi), style = MaterialTheme.typography.headlineMedium)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Category", style = MaterialTheme.typography.labelMedium)
                                    Text(r.category, style = MaterialTheme.typography.titleMedium, color = categoryColor, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider()
                            StatRow("Healthy Weight Range", "%.0f – %.0f %s".format(r.minHealthyWeight, r.maxHealthyWeight, r.unit))
                            StatRow("Maintenance Calories", "${r.maintenanceCalories} kcal/day")
                            StatRow("Weight Loss Target", "${r.deficitCalories} kcal/day (−500 deficit)")
                            StatRow("Daily Protein Goal", "${r.proteinGoal}g (1g per lb)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
