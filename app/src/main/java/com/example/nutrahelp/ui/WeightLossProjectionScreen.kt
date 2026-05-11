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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private data class Milestone(val label: String, val weightStr: String, val dateStr: String)

private data class ProjectionResult(
    val totalToLose: Float,
    val unit: String,
    val weeksToGoal: Float,
    val targetDate: String,
    val milestones: List<Milestone>
)

private fun projectWeightLoss(
    currentWeight: Float,
    goalWeight: Float,
    weeklyRate: Float,
    unit: String
): ProjectionResult? {
    if (currentWeight <= goalWeight || weeklyRate <= 0f) return null
    val totalToLose = currentWeight - goalWeight
    val weeks = totalToLose / weeklyRate
    val targetDate = addDaysToToday((weeks * 7).toInt())
    val milestones = listOf(0.25f, 0.5f, 0.75f, 1f).map { fraction ->
        val lost = totalToLose * fraction
        val w = currentWeight - lost
        val daysOut = ((lost / weeklyRate) * 7).toInt()
        Milestone(
            label = "${(fraction * 100).toInt()}% of goal (%.1f $unit lost)".format(lost),
            weightStr = "%.1f $unit".format(w),
            dateStr = addDaysToToday(daysOut)
        )
    }
    return ProjectionResult(totalToLose, unit, weeks, targetDate, milestones)
}

private fun addDaysToToday(days: Int): String {
    val cal = Calendar.getInstance()
    cal.time = Date()
    cal.add(Calendar.DAY_OF_YEAR, days)
    return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(cal.time)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightLossProjectionScreen(onBack: () -> Unit) {
    var currentWeightInput by remember { mutableStateOf("") }
    var goalWeightInput by remember { mutableStateOf("") }
    var weeklyRateInput by remember { mutableStateOf("1.0") }
    var useKg by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<ProjectionResult?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    val unit = if (useKg) "kg" else "lbs"
    val safeRange = if (useKg) "0.25–0.5 kg/week" else "0.5–1 lb/week"
    val maxSafe = if (useKg) 1f else 2f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weight Loss Projection") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("About this tool", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "This calculator projects your timeline based on a steady weekly rate. Actual results will vary — GLP-1 medications typically produce faster early loss that may slow over time. Safe rate: $safeRange.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Your Details", style = MaterialTheme.typography.titleMedium)

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            SegmentedButton(
                                selected = !useKg,
                                onClick = { useKg = false; result = null },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                            ) { Text("lbs") }
                            SegmentedButton(
                                selected = useKg,
                                onClick = { useKg = true; result = null },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                            ) { Text("kg") }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = currentWeightInput,
                                onValueChange = { currentWeightInput = it; formError = null; result = null },
                                label = { Text("Current ($unit)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                isError = formError != null,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = goalWeightInput,
                                onValueChange = { goalWeightInput = it; formError = null; result = null },
                                label = { Text("Goal ($unit)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                isError = formError != null,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = weeklyRateInput,
                            onValueChange = { weeklyRateInput = it; formError = null; result = null },
                            label = { Text("Weekly rate ($unit/week)") },
                            supportingText = { Text("Safe range: $safeRange") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (formError != null) {
                            Text(formError!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = {
                                val current = currentWeightInput.toFloatOrNull()
                                val goal = goalWeightInput.toFloatOrNull()
                                val rate = weeklyRateInput.toFloatOrNull()
                                when {
                                    current == null || current <= 0f -> formError = "Enter a valid current weight."
                                    goal == null || goal <= 0f -> formError = "Enter a valid goal weight."
                                    current <= goal -> formError = "Current weight must be greater than goal weight."
                                    rate == null || rate <= 0f -> formError = "Enter a valid weekly rate."
                                    rate > maxSafe -> formError = "Rate above $maxSafe $unit/week may be unsafe. Consider a lower rate."
                                    else -> {
                                        formError = null
                                        result = projectWeightLoss(current, goal, rate, unit)
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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Your Projection", style = MaterialTheme.typography.titleMedium)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "%.1f %s".format(r.totalToLose, r.unit),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text("to lose", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${r.weeksToGoal.toInt()} wks",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text("to goal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "%.1f mo".format(r.weeksToGoal / 4.33f),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text("months", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            HorizontalDivider()

                            Text("Goal date: ${r.targetDate}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                            HorizontalDivider()

                            Text("Milestones", style = MaterialTheme.typography.labelMedium)
                            r.milestones.forEach { milestone ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(milestone.label, style = MaterialTheme.typography.bodySmall)
                                        Text(milestone.weightStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text(milestone.dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
