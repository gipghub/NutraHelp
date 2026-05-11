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
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.unit.dp

private val appointmentTypes = listOf(
    "Endocrinologist", "Primary Care", "Nutritionist / Dietitian",
    "Cardiologist", "Mental Health", "Lab Work", "Pharmacy", "Other"
)

private data class Appointment(
    val id: Long = System.nanoTime(),
    val date: String,
    val provider: String,
    val type: String,
    val notes: String,
    var completed: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentTrackerScreen(onBack: () -> Unit) {
    var date by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("") }
    var apptType by remember { mutableStateOf(appointmentTypes[0]) }
    var typeExpanded by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var statusUpcoming by remember { mutableStateOf(true) }
    var appointments by remember { mutableStateOf(listOf<Appointment>()) }
    var formError by remember { mutableStateOf(false) }

    val upcoming = appointments.filter { !it.completed }
    val past = appointments.filter { it.completed }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointments") },
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
                        Text("Add Appointment", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it; formError = false },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            isError = formError && date.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = provider,
                            onValueChange = { provider = it; formError = false },
                            label = { Text("Provider / Clinic") },
                            singleLine = true,
                            isError = formError && provider.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = apptType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Appointment Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false }
                            ) {
                                appointmentTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = { apptType = type; typeExpanded = false }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Status", style = MaterialTheme.typography.labelMedium)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                SegmentedButton(
                                    selected = statusUpcoming,
                                    onClick = { statusUpcoming = true },
                                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                    label = { Text("Upcoming") }
                                )
                                SegmentedButton(
                                    selected = !statusUpcoming,
                                    onClick = { statusUpcoming = false },
                                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                    label = { Text("Completed") }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            minLines = 2,
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (formError) {
                            Text("Please fill in date and provider.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = {
                                if (date.isBlank() || provider.isBlank()) {
                                    formError = true
                                } else {
                                    appointments = listOf(
                                        Appointment(
                                            date = date,
                                            provider = provider,
                                            type = apptType,
                                            notes = notes.trim(),
                                            completed = !statusUpcoming
                                        )
                                    ) + appointments
                                    date = ""
                                    provider = ""
                                    notes = ""
                                    statusUpcoming = true
                                    formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Appointment")
                        }
                    }
                }
            }

            if (upcoming.isNotEmpty()) {
                item {
                    Text("Upcoming", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(upcoming, key = { it.id }) { appt ->
                    AppointmentCard(appt, onToggleComplete = {
                        appointments = appointments.map { a ->
                            if (a.id == appt.id) a.copy(completed = !a.completed) else a
                        }
                    })
                }
            }

            if (past.isNotEmpty()) {
                item {
                    Text("Completed", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(past, key = { it.id }) { appt ->
                    AppointmentCard(appt, onToggleComplete = {
                        appointments = appointments.map { a ->
                            if (a.id == appt.id) a.copy(completed = !a.completed) else a
                        }
                    })
                }
            }

            if (appointments.isEmpty()) {
                item {
                    Text("No appointments saved yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(appt: Appointment, onToggleComplete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(appt.provider, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${appt.type} · ${appt.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (appt.notes.isNotBlank()) {
                        Text(appt.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                FilterChip(
                    selected = appt.completed,
                    onClick = onToggleComplete,
                    label = { Text(if (appt.completed) "Done" else "Mark Done") }
                )
            }
        }
    }
}
