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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.MeasurementEntryEntity
import com.example.nutrahelp.viewmodel.MeasurementViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val measurementFields = listOf("Waist", "Hips", "Chest", "Left Arm", "Right Arm", "Left Thigh", "Right Thigh")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyMeasurementsScreen(onBack: () -> Unit, vm: MeasurementViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    val useInches = !LocalUseMetric.current
    var date by remember { mutableStateOf(todayStr) }
    var inputs by remember { mutableStateOf(measurementFields.associateWith { "" }) }
    var formError by remember { mutableStateOf(false) }

    val unit = if (useInches) "in" else "cm"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Body Measurements") },
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
                        Text("Log Measurements", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it; formError = false },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            isError = formError && date.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        measurementFields.chunked(2).forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                pair.forEach { field ->
                                    OutlinedTextField(
                                        value = inputs[field] ?: "",
                                        onValueChange = { v -> inputs = inputs + (field to v) },
                                        label = { Text("$field ($unit)") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (pair.size == 1) {
                                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        if (formError) {
                            Text("Please enter a date and at least one measurement.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = {
                                val parsedValues = inputs.mapNotNull { (k, v) ->
                                    v.toFloatOrNull()?.let { k to it }
                                }.toMap()
                                if (date.isBlank() || parsedValues.isEmpty()) {
                                    formError = true
                                } else {
                                    vm.insert(
                                        MeasurementEntryEntity(
                                            date = date,
                                            unit = unit,
                                            waist = parsedValues["Waist"] ?: -1f,
                                            hips = parsedValues["Hips"] ?: -1f,
                                            chest = parsedValues["Chest"] ?: -1f,
                                            leftArm = parsedValues["Left Arm"] ?: -1f,
                                            rightArm = parsedValues["Right Arm"] ?: -1f,
                                            leftThigh = parsedValues["Left Thigh"] ?: -1f,
                                            rightThigh = parsedValues["Right Thigh"] ?: -1f,
                                        )
                                    )
                                    inputs = measurementFields.associateWith { "" }
                                    date = todayStr
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Measurements")
                        }
                    }
                }
            }

            if (entries.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("History", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { vm.deleteAll() }) { Text("Clear") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    val valuesMap = listOfNotNull(
                        if (entry.waist >= 0f) "Waist" to entry.waist else null,
                        if (entry.hips >= 0f) "Hips" to entry.hips else null,
                        if (entry.chest >= 0f) "Chest" to entry.chest else null,
                        if (entry.leftArm >= 0f) "Left Arm" to entry.leftArm else null,
                        if (entry.rightArm >= 0f) "Right Arm" to entry.rightArm else null,
                        if (entry.leftThigh >= 0f) "Left Thigh" to entry.leftThigh else null,
                        if (entry.rightThigh >= 0f) "Right Thigh" to entry.rightThigh else null,
                    )
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(entry.date, style = MaterialTheme.typography.titleSmall)
                                Text(entry.unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            valuesMap.chunked(2).forEach { pair ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    pair.forEach { (name, value) ->
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("%.1f".format(value), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                    if (pair.size == 1) androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("No measurements logged yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}