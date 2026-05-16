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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.Glp1EntryEntity
import com.example.nutrahelp.viewmodel.Glp1ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val glp1Medications = listOf("Ozempic", "Wegovy", "Mounjaro", "Zepbound", "Victoza", "Saxenda", "Trulicity", "Other")
private val injectionSites = listOf(
    "Abdomen – Left", "Abdomen – Right",
    "Thigh – Left", "Thigh – Right",
    "Upper Arm – Left", "Upper Arm – Right"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Glp1InjectionLogScreen(onBack: () -> Unit, vm: Glp1ViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    val entries by vm.entries.collectAsState()

    var date by remember { mutableStateOf(todayStr) }
    var medication by remember { mutableStateOf(glp1Medications[0]) }
    var medExpanded by remember { mutableStateOf(false) }
    var dose by remember { mutableStateOf("") }
    var site by remember { mutableStateOf(injectionSites[0]) }
    var siteExpanded by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }

    val lastSite = entries.firstOrNull()?.site

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GLP-1 Injection Log") },
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
            if (lastSite != null) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Last injection site", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(lastSite, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Rotate to a different site for this injection.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Log Injection", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it; formError = false },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            isError = formError && date.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = medExpanded,
                            onExpandedChange = { medExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = medication,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Medication") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(medExpanded) },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = medExpanded, onDismissRequest = { medExpanded = false }) {
                                glp1Medications.forEach { med ->
                                    DropdownMenuItem(text = { Text(med) }, onClick = { medication = med; medExpanded = false })
                                }
                            }
                        }

                        OutlinedTextField(
                            value = dose,
                            onValueChange = { dose = it; formError = false },
                            label = { Text("Dose (e.g. 0.5 mg)") },
                            singleLine = true,
                            isError = formError && dose.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = siteExpanded,
                            onExpandedChange = { siteExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = site,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Injection site") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(siteExpanded) },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = siteExpanded, onDismissRequest = { siteExpanded = false }) {
                                injectionSites.forEach { s ->
                                    DropdownMenuItem(text = { Text(s) }, onClick = { site = s; siteExpanded = false })
                                }
                            }
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (formError) {
                            Text(
                                "Please enter a date and dose.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                if (date.isBlank() || dose.isBlank()) {
                                    formError = true
                                } else {
                                    vm.insert(Glp1EntryEntity(
                                        date = date,
                                        medication = medication,
                                        dose = dose,
                                        site = site,
                                        notes = notes.trim()
                                    ))
                                    dose = ""; notes = ""; date = todayStr; formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Save Injection") }
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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(entry.date, style = MaterialTheme.typography.titleSmall)
                                Text(entry.dose, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                            Text("${entry.medication} · ${entry.site}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (entry.notes.isNotBlank()) {
                                Text(entry.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("No injections logged yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}