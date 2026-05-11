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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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

private val defaultSupplements = listOf(
    "Vitamin B12",
    "Vitamin D3",
    "Magnesium Glycinate",
    "Omega-3 Fish Oil",
    "Zinc",
    "Iron",
    "Calcium",
    "Folate (B9)",
    "Fiber Supplement",
    "Probiotics",
    "Multivitamin"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplementTrackerScreen(onBack: () -> Unit) {
    var supplements by remember { mutableStateOf(defaultSupplements) }
    var checkedItems by remember { mutableStateOf(setOf<String>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newSupplementName by remember { mutableStateOf("") }

    val takenCount = checkedItems.size
    val totalCount = supplements.size

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false; newSupplementName = "" },
            title = { Text("Add Supplement") },
            text = {
                OutlinedTextField(
                    value = newSupplementName,
                    onValueChange = { newSupplementName = it },
                    label = { Text("Supplement name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = newSupplementName.trim()
                        if (name.isNotBlank() && name !in supplements) {
                            supplements = supplements + name
                        }
                        showAddDialog = false
                        newSupplementName = ""
                    }
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false; newSupplementName = "" }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supplement Tracker") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add supplement")
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Today: $takenCount of $totalCount taken",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (checkedItems.isNotEmpty()) {
                            OutlinedButton(
                                onClick = { checkedItems = emptySet() }
                            ) { Text("Reset") }
                        }
                    }
                    LinearProgressIndicator(
                        progress = { if (totalCount > 0) takenCount.toFloat() / totalCount else 0f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider()
                }
            }

            items(supplements, key = { it }) { supplement ->
                val checked = supplement in checkedItems
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { isChecked ->
                            checkedItems = if (isChecked) checkedItems + supplement else checkedItems - supplement
                        }
                    )
                    Text(
                        supplement,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Add Custom Supplement", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}
