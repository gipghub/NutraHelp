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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private val medications = listOf("Ozempic", "Wegovy", "Mounjaro", "Zepbound", "Victoza", "Saxenda", "Rybelsus", "Other")
private val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Daily")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("lbs") }

    var medication by remember { mutableStateOf(medications[0]) }
    var medExpanded by remember { mutableStateOf(false) }
    var dose by remember { mutableStateOf("") }
    var injectionDay by remember { mutableStateOf(daysOfWeek[0]) }
    var dayExpanded by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf("") }

    var currentWeight by remember { mutableStateOf("") }
    var goalWeight by remember { mutableStateOf("") }
    var dailyProtein by remember { mutableStateOf("120") }
    var dailyCalories by remember { mutableStateOf("1500") }

    var saved by remember { mutableStateOf(false) }

    fun markChanged() { saved = false }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
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
                SectionHeader("Personal Info")
            }
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; markChanged() },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Weight Unit", style = MaterialTheme.typography.labelMedium)
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        listOf("lbs", "kg").forEachIndexed { index, label ->
                            SegmentedButton(
                                selected = unit == label,
                                onClick = { unit = label; markChanged() },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = 2),
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            item { SectionHeader("Medication") }
            item {
                ExposedDropdownMenuBox(
                    expanded = medExpanded,
                    onExpandedChange = { medExpanded = it }
                ) {
                    OutlinedTextField(
                        value = medication,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Medication") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = medExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = medExpanded,
                        onDismissRequest = { medExpanded = false }
                    ) {
                        medications.forEach { med ->
                            DropdownMenuItem(
                                text = { Text(med) },
                                onClick = { medication = med; medExpanded = false; markChanged() }
                            )
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = dose,
                    onValueChange = { dose = it; markChanged() },
                    label = { Text("Dose (e.g. 0.5 mg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                ExposedDropdownMenuBox(
                    expanded = dayExpanded,
                    onExpandedChange = { dayExpanded = it }
                ) {
                    OutlinedTextField(
                        value = injectionDay,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Injection / Dose Day") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = dayExpanded,
                        onDismissRequest = { dayExpanded = false }
                    ) {
                        daysOfWeek.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = { injectionDay = day; dayExpanded = false; markChanged() }
                            )
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it; markChanged() },
                    label = { Text("Start Date (MM/DD/YYYY)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item { SectionHeader("Goals") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = currentWeight,
                        onValueChange = { currentWeight = it; markChanged() },
                        label = { Text("Current Weight ($unit)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = goalWeight,
                        onValueChange = { goalWeight = it; markChanged() },
                        label = { Text("Goal Weight ($unit)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = dailyProtein,
                        onValueChange = { dailyProtein = it; markChanged() },
                        label = { Text("Protein Goal (g)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dailyCalories,
                        onValueChange = { dailyCalories = it; markChanged() },
                        label = { Text("Calorie Goal (kcal)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Button(
                    onClick = { saved = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(if (saved) "Saved ✓" else "Save Profile")
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )
        HorizontalDivider()
    }
}
