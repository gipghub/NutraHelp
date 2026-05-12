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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.InjectionSiteEntryEntity
import com.example.nutrahelp.viewmodel.InjectionSiteViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

private val injectionSites = listOf(
    "Abdomen — Left Upper",
    "Abdomen — Left Lower",
    "Abdomen — Right Upper",
    "Abdomen — Right Lower",
    "Thigh — Left",
    "Thigh — Right",
    "Upper Arm — Left",
    "Upper Arm — Right"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InjectionSiteTrackerScreen(onBack: () -> Unit, vm: InjectionSiteViewModel = viewModel()) {
    val locale = LocalConfiguration.current.locales[0]
    val dateFormat = remember(locale) { SimpleDateFormat("MMM d, h:mm a", locale) }

    val entries by vm.entries.collectAsState()

    var selectedSite by remember { mutableStateOf(injectionSites[0]) }
    var dose by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val lastUsedBySite: Map<String, InjectionSiteEntryEntity> = remember(entries) {
        entries.groupBy { it.site }.mapValues { (_, es) -> es.first() }
    }

    fun daysSince(entry: InjectionSiteEntryEntity): Long {
        val diffMs = System.currentTimeMillis() - entry.timestampMs
        return TimeUnit.MILLISECONDS.toDays(diffMs)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Injection Site Tracker") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Site Status", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Rotate sites to avoid scar tissue. Each site should rest at least 7 days between injections.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider()
                        injectionSites.forEach { site ->
                            val last = lastUsedBySite[site]
                            val days = last?.let { daysSince(it) }
                            val (statusText, statusColor) = when {
                                last == null -> "Never used" to MaterialTheme.colorScheme.tertiary
                                days != null && days < 7 -> "${days}d ago — rest recommended" to MaterialTheme.colorScheme.error
                                days != null && days < 14 -> "${days}d ago — ok to use" to MaterialTheme.colorScheme.secondary
                                else -> "${days}d ago — available" to MaterialTheme.colorScheme.tertiary
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(site, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                Text(
                                    statusText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = statusColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Log Injection", style = MaterialTheme.typography.titleMedium)

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Injection site", style = MaterialTheme.typography.labelMedium)
                            injectionSites.forEach { site ->
                                val last = lastUsedBySite[site]
                                val days = last?.let { daysSince(it) }
                                val isRecent = days != null && days < 7
                                Card(
                                    onClick = { selectedSite = site },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = when {
                                            selectedSite == site -> MaterialTheme.colorScheme.primaryContainer
                                            isRecent -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            site,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (selectedSite == site) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                        if (isRecent) {
                                            Text(
                                                "Rest",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = dose,
                            onValueChange = { dose = it },
                            label = { Text("Dose (optional, e.g. 0.5 mg)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val now = System.currentTimeMillis()
                                vm.insert(
                                    InjectionSiteEntryEntity(
                                        dateTime = dateFormat.format(Date(now)),
                                        timestampMs = now,
                                        site = selectedSite,
                                        dose = dose.trim(),
                                        notes = notes.trim()
                                    )
                                )
                                dose = ""
                                notes = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Log Injection") }
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
                        Text("History (${entries.size})", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { vm.deleteAll() }) { Text("Clear") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    entry.site,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    entry.dateTime,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (entry.dose.isNotEmpty()) {
                                    Text(
                                        "Dose: ${entry.dose}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (entry.notes.isNotEmpty()) {
                                    Text(
                                        entry.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(onClick = { vm.delete(entry) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "Log your injections to track site rotation and avoid overuse.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
