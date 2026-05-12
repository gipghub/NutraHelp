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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val historyMedOptions = listOf(
    "Semaglutide (Ozempic)",
    "Semaglutide (Wegovy)",
    "Tirzepatide (Mounjaro)",
    "Tirzepatide (Zepbound)",
    "Liraglutide (Victoza)",
    "Liraglutide (Saxenda)",
    "Dulaglutide (Trulicity)",
    "Semaglutide (Rybelsus)",
    "Other",
)

private val titrationReasons = listOf(
    "Scheduled titration",
    "Tolerating well",
    "Side effects – dose reduced",
    "Doctor adjusted",
    "Switched medication",
    "Starting dose",
    "Other",
)

private data class TitrationEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val medication: String,
    val dose: String,
    val reason: String,
    val notes: String,
)

private data class InjectionRecord(
    val id: Long = System.nanoTime(),
    val date: String,
    val medication: String,
    val dose: String,
    val notes: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationHistoryScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    // Titration state
    var titrations by remember { mutableStateOf(listOf<TitrationEntry>()) }
    var showTitrationForm by remember { mutableStateOf(false) }
    var tDate by remember { mutableStateOf(todayStr) }
    var tMed by remember { mutableStateOf(historyMedOptions[0]) }
    var tMedExpanded by remember { mutableStateOf(false) }
    var tDose by remember { mutableStateOf("") }
    var tReason by remember { mutableStateOf(titrationReasons[0]) }
    var tReasonExpanded by remember { mutableStateOf(false) }
    var tNotes by remember { mutableStateOf("") }
    var tError by remember { mutableStateOf(false) }

    // Injection state
    var injections by remember { mutableStateOf(listOf<InjectionRecord>()) }
    var showInjectionForm by remember { mutableStateOf(false) }
    var iDate by remember { mutableStateOf(todayStr) }
    var iMed by remember { mutableStateOf(historyMedOptions[0]) }
    var iMedExpanded by remember { mutableStateOf(false) }
    var iDose by remember { mutableStateOf("") }
    var iNotes by remember { mutableStateOf("") }
    var iError by remember { mutableStateOf(false) }

    val currentMed = titrations.firstOrNull()?.medication ?: "—"
    val currentDose = titrations.firstOrNull()?.dose ?: "—"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medication History") },
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
            // ── Summary card ─────────────────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MedStatItem(
                            icon = Icons.Default.Medication,
                            label = "Medication",
                            value = if (currentMed == "—") "—" else currentMed.substringAfterLast("(").trimEnd(')').ifEmpty { currentMed }
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(48.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        MedStatItem(
                            icon = Icons.Default.Science,
                            label = "Current Dose",
                            value = currentDose
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(48.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        MedStatItem(
                            icon = Icons.Default.TrendingUp,
                            label = "Titrations",
                            value = titrations.size.toString()
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(48.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        MedStatItem(
                            icon = Icons.Default.Add,
                            label = "Injections",
                            value = injections.size.toString()
                        )
                    }
                }
            }

            // ── Dose escalation timeline ──────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dose Escalation Timeline", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { showTitrationForm = !showTitrationForm }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (showTitrationForm) "Cancel" else "Log Change")
                    }
                }
            }

            if (showTitrationForm) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Record Dose Change", style = MaterialTheme.typography.titleSmall)

                            OutlinedTextField(
                                value = tDate,
                                onValueChange = { tDate = it; tError = false },
                                label = { Text("Date (MM/DD/YYYY)") },
                                singleLine = true,
                                isError = tError && tDate.isBlank(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            ExposedDropdownMenuBox(tMedExpanded, { tMedExpanded = it }) {
                                OutlinedTextField(
                                    value = tMed,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Medication") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tMedExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(tMedExpanded, { tMedExpanded = false }) {
                                    historyMedOptions.forEach { med ->
                                        DropdownMenuItem(
                                            text = { Text(med) },
                                            onClick = { tMed = med; tMedExpanded = false },
                                            trailingIcon = if (tMed == med) ({ Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }) else null
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = tDose,
                                onValueChange = { tDose = it; tError = false },
                                label = { Text("New Dose (e.g. 1.0 mg)") },
                                singleLine = true,
                                isError = tError && tDose.isBlank(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            ExposedDropdownMenuBox(tReasonExpanded, { tReasonExpanded = it }) {
                                OutlinedTextField(
                                    value = tReason,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Reason") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tReasonExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(tReasonExpanded, { tReasonExpanded = false }) {
                                    titrationReasons.forEach { r ->
                                        DropdownMenuItem(
                                            text = { Text(r) },
                                            onClick = { tReason = r; tReasonExpanded = false },
                                            trailingIcon = if (tReason == r) ({ Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }) else null
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = tNotes,
                                onValueChange = { tNotes = it },
                                label = { Text("Notes (optional)") },
                                minLines = 2,
                                maxLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    if (tDate.isBlank() || tDose.isBlank()) {
                                        tError = true
                                    } else {
                                        titrations = listOf(
                                            TitrationEntry(
                                                date = tDate,
                                                medication = tMed,
                                                dose = tDose,
                                                reason = tReason,
                                                notes = tNotes.trim()
                                            )
                                        ) + titrations
                                        tDate = todayStr
                                        tDose = ""
                                        tNotes = ""
                                        tError = false
                                        showTitrationForm = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Dose Change")
                            }
                        }
                    }
                }
            }

            if (titrations.isEmpty()) {
                item {
                    Text(
                        "No dose changes logged yet. Tap \"Log Change\" to record your starting dose or a titration.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                itemsIndexed(titrations, key = { _, e -> e.id }) { index, entry ->
                    TitrationTimelineItem(
                        entry = entry,
                        isFirst = index == 0,
                        isLast = index == titrations.lastIndex
                    )
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            // ── Injection log ─────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Injection Log", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { showInjectionForm = !showInjectionForm }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (showInjectionForm) "Cancel" else "Log Dose")
                    }
                }
            }

            if (showInjectionForm) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Log Injection", style = MaterialTheme.typography.titleSmall)

                            OutlinedTextField(
                                value = iDate,
                                onValueChange = { iDate = it; iError = false },
                                label = { Text("Date (MM/DD/YYYY)") },
                                singleLine = true,
                                isError = iError && iDate.isBlank(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            ExposedDropdownMenuBox(iMedExpanded, { iMedExpanded = it }) {
                                OutlinedTextField(
                                    value = iMed,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Medication") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(iMedExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(iMedExpanded, { iMedExpanded = false }) {
                                    historyMedOptions.forEach { med ->
                                        DropdownMenuItem(
                                            text = { Text(med) },
                                            onClick = { iMed = med; iMedExpanded = false },
                                            trailingIcon = if (iMed == med) ({ Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }) else null
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = iDose,
                                onValueChange = { iDose = it; iError = false },
                                label = { Text("Dose (e.g. 0.5 mg)") },
                                singleLine = true,
                                isError = iError && iDose.isBlank(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = iNotes,
                                onValueChange = { iNotes = it },
                                label = { Text("Notes (optional)") },
                                minLines = 2,
                                maxLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    if (iDate.isBlank() || iDose.isBlank()) {
                                        iError = true
                                    } else {
                                        injections = listOf(
                                            InjectionRecord(
                                                date = iDate,
                                                medication = iMed,
                                                dose = iDose,
                                                notes = iNotes.trim()
                                            )
                                        ) + injections
                                        iDate = todayStr
                                        iDose = ""
                                        iNotes = ""
                                        iError = false
                                        showInjectionForm = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Injection")
                            }
                        }
                    }
                }
            }

            if (injections.isEmpty()) {
                item {
                    Text(
                        "No injections logged yet. Tap \"Log Dose\" to record each administration.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                itemsIndexed(injections, key = { _, r -> r.id }) { index, record ->
                    InjectionLogItem(record = record, index = injections.size - index)
                }
            }
        }
    }
}

@Composable
private fun MedStatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TitrationTimelineItem(entry: TitrationEntry, isFirst: Boolean, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(8.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )
            } else {
                Spacer(Modifier.height(8.dp))
            }
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFirst) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                    )
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Content
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (!isLast) 4.dp else 0.dp),
            colors = if (isFirst)
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            else
                CardDefaults.cardColors()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        entry.dose,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isFirst) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        entry.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    entry.medication,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    entry.reason,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (entry.notes.isNotBlank()) {
                    Text(
                        entry.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun InjectionLogItem(record: InjectionRecord, index: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "#$index",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "${record.medication.substringAfterLast("(").trimEnd(')')} · ${record.dose}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (record.notes.isNotBlank()) {
                        Text(
                            record.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Text(
                record.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
