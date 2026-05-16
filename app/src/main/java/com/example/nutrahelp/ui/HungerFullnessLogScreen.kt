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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.HungerEntryEntity
import com.example.nutrahelp.viewmodel.HungerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val hungerLabels = listOf("Starving", "Hungry", "Neutral", "Satisfied", "Full")
private val fullnessLabels = listOf("Empty", "Light", "Content", "Full", "Stuffed")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HungerFullnessLogScreen(onBack: () -> Unit, vm: HungerViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val currentTime = remember { SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    var mealName by remember { mutableStateOf("") }
    var timeInput by remember { mutableStateOf(currentTime) }
    var hungerBefore by remember { mutableIntStateOf(1) }
    var fullnessAfter by remember { mutableIntStateOf(3) }
    var formError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hunger & Fullness") },
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
                        Text("Why track this?", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "GLP-1 medications reduce hunger and increase satiety. Tracking these signals helps you understand how the medication is working and avoid under- or over-eating.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log a Meal", style = MaterialTheme.typography.titleMedium)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = mealName,
                                onValueChange = { mealName = it; formError = false },
                                label = { Text("Meal name") },
                                singleLine = true,
                                isError = formError && mealName.isBlank(),
                                modifier = Modifier.weight(2f)
                            )
                            OutlinedTextField(
                                value = timeInput,
                                onValueChange = { timeInput = it },
                                label = { Text("Time") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Hunger before — ${hungerLabels[hungerBefore]}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                hungerLabels.forEachIndexed { i, _ ->
                                    SegmentedButton(
                                        selected = hungerBefore == i,
                                        onClick = { hungerBefore = i },
                                        shape = SegmentedButtonDefaults.itemShape(index = i, count = hungerLabels.size)
                                    ) { Text("${i + 1}", style = MaterialTheme.typography.labelSmall) }
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Fullness after — ${fullnessLabels[fullnessAfter]}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                fullnessLabels.forEachIndexed { i, _ ->
                                    SegmentedButton(
                                        selected = fullnessAfter == i,
                                        onClick = { fullnessAfter = i },
                                        shape = SegmentedButtonDefaults.itemShape(index = i, count = fullnessLabels.size)
                                    ) { Text("${i + 1}", style = MaterialTheme.typography.labelSmall) }
                                }
                            }
                        }

                        if (formError) {
                            Text(
                                "Please enter a meal name.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                if (mealName.isBlank()) {
                                    formError = true
                                } else {
                                    vm.insert(HungerEntryEntity(
                                        date = todayStr,
                                        time = timeInput.trim(),
                                        mealName = mealName.trim(),
                                        hungerBefore = hungerBefore,
                                        fullnessAfter = fullnessAfter
                                    ))
                                    mealName = ""
                                    timeInput = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                                    hungerBefore = 1
                                    fullnessAfter = 3
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Log Meal") }
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
                        Text("Today's Log", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { vm.deleteAll() }) { Text("Reset") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(entry.mealName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(entry.time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "Before: ${hungerLabels[entry.hungerBefore]}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text("→", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "After: ${fullnessLabels[entry.fullnessAfter]}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "No meals logged yet — track your first one!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
