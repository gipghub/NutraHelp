package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    useMetric: Boolean = true,
    onUseMetricChange: (Boolean) -> Unit = {},
    themePreference: String = "System",
    onThemeChange: (String) -> Unit = {}
) {
    var injectionReminder by remember { mutableStateOf(true) }
    var mealReminder by remember { mutableStateOf(false) }
    var waterReminder by remember { mutableStateOf(true) }

    var clearLogDialog by remember { mutableStateOf(false) }
    var clearWeightDialog by remember { mutableStateOf(false) }

    if (clearLogDialog) {
        ConfirmDialog(
            title = "Clear Food Log?",
            body = "All logged meals will be permanently deleted.",
            onConfirm = { clearLogDialog = false },
            onDismiss = { clearLogDialog = false }
        )
    }
    if (clearWeightDialog) {
        ConfirmDialog(
            title = "Clear Weight History?",
            body = "All weight entries will be permanently deleted.",
            onConfirm = { clearWeightDialog = false },
            onDismiss = { clearWeightDialog = false }
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item { SettingsSectionHeader("Notifications") }

            item {
                SwitchRow(
                    label = "Injection Day Reminder",
                    description = "Remind me on my weekly injection day",
                    checked = injectionReminder,
                    onCheckedChange = { injectionReminder = it }
                )
            }
            item {
                SwitchRow(
                    label = "Daily Meal Reminder",
                    description = "Prompt to log meals at noon",
                    checked = mealReminder,
                    onCheckedChange = { mealReminder = it }
                )
            }
            item {
                SwitchRow(
                    label = "Water Intake Reminder",
                    description = "Remind me to drink water throughout the day",
                    checked = waterReminder,
                    onCheckedChange = { waterReminder = it }
                )
            }

            item { SettingsSectionHeader("Measurements") }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Unit System", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Applies to weight, height, and body measurements across the app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val unitOptions = listOf("Metric (kg, cm)" to true, "Standard (lbs, in)" to false)
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        unitOptions.forEachIndexed { index, (label, isMetric) ->
                            SegmentedButton(
                                selected = useMetric == isMetric,
                                onClick = { onUseMetricChange(isMetric) },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = unitOptions.size),
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            item { SettingsSectionHeader("Appearance") }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Theme", style = MaterialTheme.typography.bodyMedium)
                    val themes = listOf("System", "Light", "Dark")
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        themes.forEachIndexed { index, label ->
                            SegmentedButton(
                                selected = themePreference == label,
                                onClick = { onThemeChange(label) },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = themes.size),
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            item { SettingsSectionHeader("Data") }

            item {
                OutlinedButton(
                    onClick = { clearLogDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Food Log", color = MaterialTheme.colorScheme.error)
                }
            }
            item {
                OutlinedButton(
                    onClick = { clearWeightDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Weight History", color = MaterialTheme.colorScheme.error)
                }
            }

            item { SettingsSectionHeader("About") }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("NutraHelp", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Version 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Nutritional guidance for GLP-1 medication users. Always consult your healthcare provider for medical advice.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        HorizontalDivider()
    }
}

@Composable
private fun SwitchRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    body: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
