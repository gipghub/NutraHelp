package com.example.nutrahelp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.AppointmentEntity
import com.example.nutrahelp.viewmodel.AppointmentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val apptTypes = listOf(
    "Endocrinologist",
    "Primary Care",
    "Nutritionist / Dietitian",
    "Cardiologist",
    "Mental Health",
    "Lab Work",
    "Pharmacy",
    "Telehealth",
    "Other",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentTrackerScreen(onBack: () -> Unit, vm: AppointmentViewModel = viewModel()) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    val appointments by vm.appointments.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showForm by remember { mutableStateOf(false) }

    // Form fields
    var fDate by remember { mutableStateOf(todayStr) }
    var fTime by remember { mutableStateOf("") }
    var fProvider by remember { mutableStateOf("") }
    var fType by remember { mutableStateOf(apptTypes[0]) }
    var fTypeExpanded by remember { mutableStateOf(false) }
    var fLocation by remember { mutableStateOf("") }
    var fNotes by remember { mutableStateOf("") }
    var fError by remember { mutableStateOf(false) }

    val upcoming = appointments.filter { !it.completed }
    val completed = appointments.filter { it.completed }
    val displayed = when (selectedTab) {
        0 -> upcoming
        1 -> completed
        else -> appointments
    }

    val nextAppt = upcoming.firstOrNull()

    fun resetForm() {
        fDate = todayStr; fTime = ""; fProvider = ""; fType = apptTypes[0]
        fLocation = ""; fNotes = ""; fError = false; showForm = false
    }

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
            // ── Next appointment banner ───────────────────────────────────────
            if (nextAppt != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val parts = nextAppt.date.split("/")
                            val month = if (parts.size >= 2) monthAbbr(parts[0].toIntOrNull() ?: 0) else ""
                            val day = parts.getOrNull(1) ?: ""
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(month, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary)
                                Text(day, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    "Next Appointment",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Text(
                                    nextAppt.provider,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    nextAppt.type,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                if (nextAppt.time.isNotBlank() || nextAppt.location.isNotBlank()) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (nextAppt.time.isNotBlank()) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                                Text(nextAppt.time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                            }
                                        }
                                        if (nextAppt.location.isNotBlank()) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                                Text(nextAppt.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Stats row ─────────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ApptStatCard("Upcoming", upcoming.size.toString(), Icons.Default.CalendarToday, Modifier.weight(1f))
                    ApptStatCard("Completed", completed.size.toString(), Icons.Default.Check, Modifier.weight(1f))
                    ApptStatCard("Total", appointments.size.toString(), Icons.Default.CalendarMonth, Modifier.weight(1f))
                }
            }

            // ── Schedule button / form ────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Schedule", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { showForm = !showForm; if (!showForm) resetForm() }) {
                        Icon(if (showForm) Icons.Default.Close else Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (showForm) "Cancel" else "Add Appointment")
                    }
                }
            }

            if (showForm) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = fDate,
                                    onValueChange = { fDate = it; fError = false },
                                    label = { Text("Date (MM/DD/YYYY)") },
                                    singleLine = true,
                                    leadingIcon = { Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp)) },
                                    isError = fError && fDate.isBlank(),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = fTime,
                                    onValueChange = { fTime = it },
                                    label = { Text("Time (optional)") },
                                    singleLine = true,
                                    leadingIcon = { Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(18.dp)) },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = fProvider,
                                onValueChange = { fProvider = it; fError = false },
                                label = { Text("Provider / Clinic") },
                                singleLine = true,
                                isError = fError && fProvider.isBlank(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            ExposedDropdownMenuBox(fTypeExpanded, { fTypeExpanded = it }) {
                                OutlinedTextField(
                                    value = fType,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Appointment Type") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(fTypeExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(fTypeExpanded, { fTypeExpanded = false }) {
                                    apptTypes.forEach { t ->
                                        DropdownMenuItem(
                                            text = { Text(t) },
                                            onClick = { fType = t; fTypeExpanded = false },
                                            trailingIcon = if (fType == t) ({ Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }) else null
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = fLocation,
                                onValueChange = { fLocation = it },
                                label = { Text("Location (optional)") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(18.dp)) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = fNotes,
                                onValueChange = { fNotes = it },
                                label = { Text("Notes (optional)") },
                                minLines = 2,
                                maxLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (fError) {
                                Text(
                                    "Date and provider are required.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Button(
                                onClick = {
                                    if (fDate.isBlank() || fProvider.isBlank()) {
                                        fError = true
                                    } else {
                                        vm.insert(
                                            AppointmentEntity(
                                                date = fDate,
                                                time = fTime.trim(),
                                                provider = fProvider.trim(),
                                                type = fType,
                                                location = fLocation.trim(),
                                                notes = fNotes.trim(),
                                                completed = false,
                                            )
                                        )
                                        resetForm()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Appointment")
                            }
                        }
                    }
                }
            }

            // ── Tabs ──────────────────────────────────────────────────────────
            item {
                TabRow(selectedTabIndex = selectedTab) {
                    listOf(
                        "Upcoming (${upcoming.size})",
                        "Completed (${completed.size})",
                        "All (${appointments.size})"
                    ).forEachIndexed { i, label ->
                        Tab(
                            selected = selectedTab == i,
                            onClick = { selectedTab = i },
                            text = { Text(label, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }

            // ── Appointment list ──────────────────────────────────────────────
            if (displayed.isEmpty()) {
                item {
                    Text(
                        when (selectedTab) {
                            0 -> "No upcoming appointments. Tap \"Add Appointment\" to schedule one."
                            1 -> "No completed appointments yet."
                            else -> "No appointments logged yet."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                items(displayed, key = { it.id }) { appt ->
                    ApptCard(
                        appt = appt,
                        onToggleComplete = { vm.toggleComplete(appt) },
                        onDelete = { vm.delete(appt) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ApptStatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ApptCard(appt: AppointmentEntity, onToggleComplete: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (appt.completed)
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        else
            CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            val parts = appt.date.split("/")
            val month = monthAbbr(parts.getOrNull(0)?.toIntOrNull() ?: 0)
            val day = parts.getOrNull(1) ?: "?"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (appt.completed) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.primaryContainer
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .width(36.dp)
            ) {
                Text(
                    month,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (appt.completed) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    day,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (appt.completed) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(appt.provider, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(appt.type, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (appt.time.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(11.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(appt.time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    if (appt.location.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(11.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(appt.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (appt.notes.isNotBlank()) {
                    Text(appt.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = onToggleComplete,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        if (appt.completed) Icons.Default.Undo else Icons.Default.Check,
                        null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (appt.completed) "Undo" else "Mark Done",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun monthAbbr(month: Int) = when (month) {
    1 -> "JAN"; 2 -> "FEB"; 3 -> "MAR"; 4 -> "APR"
    5 -> "MAY"; 6 -> "JUN"; 7 -> "JUL"; 8 -> "AUG"
    9 -> "SEP"; 10 -> "OCT"; 11 -> "NOV"; 12 -> "DEC"
    else -> "—"
}
