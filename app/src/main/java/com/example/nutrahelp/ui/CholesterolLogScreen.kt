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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class CholesterolEntry(
    val id: Long = System.nanoTime(),
    val date: String,
    val total: Float?,
    val hdl: Float?,
    val ldl: Float?,
    val triglycerides: Float?,
    val notes: String
)

private fun totalCategory(v: Float) = when {
    v < 200f -> Triple("Desirable", "tertiary", "< 200 mg/dL")
    v < 240f -> Triple("Borderline High", "secondary", "200–239 mg/dL")
    else -> Triple("High", "error", "≥ 240 mg/dL")
}

private fun ldlCategory(v: Float) = when {
    v < 100f -> Triple("Optimal", "tertiary", "< 100 mg/dL")
    v < 130f -> Triple("Near Optimal", "tertiary", "100–129 mg/dL")
    v < 160f -> Triple("Borderline High", "secondary", "130–159 mg/dL")
    v < 190f -> Triple("High", "error", "160–189 mg/dL")
    else -> Triple("Very High", "error", "≥ 190 mg/dL")
}

private fun hdlCategory(v: Float) = when {
    v < 40f -> Triple("Low (risk)", "error", "< 40 mg/dL")
    v < 60f -> Triple("Acceptable", "secondary", "40–59 mg/dL")
    else -> Triple("Protective", "tertiary", "≥ 60 mg/dL")
}

private fun triglyceridesCategory(v: Float) = when {
    v < 150f -> Triple("Normal", "tertiary", "< 150 mg/dL")
    v < 200f -> Triple("Borderline High", "secondary", "150–199 mg/dL")
    v < 500f -> Triple("High", "error", "200–499 mg/dL")
    else -> Triple("Very High", "error", "≥ 500 mg/dL")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CholesterolLogScreen(onBack: () -> Unit) {
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) }

    var date by remember { mutableStateOf(todayStr) }
    var totalInput by remember { mutableStateOf("") }
    var hdlInput by remember { mutableStateOf("") }
    var ldlInput by remember { mutableStateOf("") }
    var triInput by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf(listOf<CholesterolEntry>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cholesterol Log") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Reference Ranges (mg/dL)", style = MaterialTheme.typography.titleSmall)
                        listOf(
                            "Total" to "< 200 desirable",
                            "LDL" to "< 100 optimal",
                            "HDL" to "≥ 60 protective",
                            "Triglycerides" to "< 150 normal"
                        ).forEach { (label, ref) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Text(ref, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
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
                        Text("Log Panel", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it; formError = false },
                            label = { Text("Date (MM/DD/YYYY)") },
                            singleLine = true,
                            isError = formError && date.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = totalInput,
                                onValueChange = { totalInput = it; formError = false },
                                label = { Text("Total") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = ldlInput,
                                onValueChange = { ldlInput = it },
                                label = { Text("LDL") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = hdlInput,
                                onValueChange = { hdlInput = it },
                                label = { Text("HDL") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = triInput,
                                onValueChange = { triInput = it },
                                label = { Text("Triglycerides") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (formError) {
                            Text("Please enter a date and at least one value.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = {
                                val t = totalInput.toFloatOrNull()
                                val h = hdlInput.toFloatOrNull()
                                val l = ldlInput.toFloatOrNull()
                                val tri = triInput.toFloatOrNull()
                                if (date.isBlank() || (t == null && h == null && l == null && tri == null)) {
                                    formError = true
                                } else {
                                    entries = (listOf(CholesterolEntry(date = date, total = t, hdl = h, ldl = l, triglycerides = tri, notes = notes.trim())) + entries).sortedByDescending { it.id }
                                    totalInput = ""; hdlInput = ""; ldlInput = ""; triInput = ""; notes = ""; date = todayStr; formError = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Save Panel") }
                    }
                }
            }

            if (entries.isNotEmpty()) {
                item {
                    Text("History", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(entry.date, style = MaterialTheme.typography.titleSmall)
                            entry.total?.let { v ->
                                val (lbl, tok, _) = totalCategory(v)
                                CholRow("Total", v, lbl, tok)
                            }
                            entry.ldl?.let { v ->
                                val (lbl, tok, _) = ldlCategory(v)
                                CholRow("LDL", v, lbl, tok)
                            }
                            entry.hdl?.let { v ->
                                val (lbl, tok, _) = hdlCategory(v)
                                CholRow("HDL", v, lbl, tok)
                            }
                            entry.triglycerides?.let { v ->
                                val (lbl, tok, _) = triglyceridesCategory(v)
                                CholRow("Triglycerides", v, lbl, tok)
                            }
                            if (entry.notes.isNotBlank()) {
                                Text(entry.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("No panels logged yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun CholRow(label: String, value: Float, category: String, colorToken: String) {
    val color: Color = when (colorToken) {
        "error" -> MaterialTheme.colorScheme.error
        "secondary" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.tertiary
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text("%.0f mg/dL".format(value), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(category, style = MaterialTheme.typography.labelSmall, color = color, modifier = Modifier.padding(start = 8.dp))
    }
}
