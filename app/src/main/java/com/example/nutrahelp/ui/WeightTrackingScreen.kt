package com.example.nutrahelp.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.WeightEntryEntity
import com.example.nutrahelp.viewmodel.WeightViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackingScreen(onBack: () -> Unit = {}, vm: WeightViewModel = viewModel()) {
    val useMetric = LocalUseMetric.current
    val unit = if (useMetric) "kg" else "lbs"

    val records by vm.entries.collectAsState()
    val goalWeight by vm.goalWeight.collectAsState()
    var weightInput by remember { mutableStateOf("") }
    var goalInput by remember { mutableStateOf("") }
    var weightError by remember { mutableStateOf<String?>(null) }
    var goalError by remember { mutableStateOf<String?>(null) }

    val firstWeight = records.lastOrNull()?.weight
    val latestWeight = records.firstOrNull()?.weight
    val previousWeight = records.getOrNull(1)?.weight
    val totalChange = if (firstWeight != null && latestWeight != null) latestWeight - firstWeight else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weight Tracker") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            // Summary card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Weight Overview",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            WeightStat(
                                label = "Current",
                                value = latestWeight?.let { "%.1f".format(it) } ?: "—",
                                unit = if (latestWeight != null) unit else "",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            WeightStat(
                                label = "Starting",
                                value = firstWeight?.let { "%.1f".format(it) } ?: "—",
                                unit = if (firstWeight != null) unit else "",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (totalChange != null) {
                                val sign = if (totalChange > 0) "+" else ""
                                WeightStat(
                                    label = "Total",
                                    value = "$sign${"%.1f".format(totalChange)}",
                                    unit = unit,
                                    color = when {
                                        totalChange < 0 -> MaterialTheme.colorScheme.tertiary
                                        totalChange > 0 -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                                    }
                                )
                            }
                        }

                        // Trend indicator
                        if (latestWeight != null && previousWeight != null) {
                            val diff = latestWeight - previousWeight
                            val trendIcon = when {
                                diff < -0.05f -> Icons.AutoMirrored.Filled.TrendingDown
                                diff > 0.05f -> Icons.AutoMirrored.Filled.TrendingUp
                                else -> Icons.AutoMirrored.Filled.TrendingFlat
                            }
                            val trendColor = when {
                                diff < -0.05f -> MaterialTheme.colorScheme.tertiary
                                diff > 0.05f -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            }
                            val trendText = when {
                                diff < -0.05f -> "Down %.1f %s since last entry".format(-diff, unit)
                                diff > 0.05f -> "Up %.1f %s since last entry".format(diff, unit)
                                else -> "No change since last entry"
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(trendIcon, contentDescription = null, tint = trendColor, modifier = Modifier.size(18.dp))
                                Text(trendText, style = MaterialTheme.typography.bodySmall, color = trendColor)
                            }
                        }
                    }
                }
            }

            // Sparkline chart
            if (records.size >= 2) {
                item {
                    WeightChart(records = records, unit = unit)
                }
            }

            // Goal card
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Flag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Goal Weight", style = MaterialTheme.typography.titleMedium)
                        }

                        if (goalWeight != null) {
                            val gw = goalWeight!!
                            val lw = latestWeight
                            if (lw != null) {
                                val startW = firstWeight ?: lw
                                val totalNeeded = startW - gw
                                val achieved = startW - lw
                                val progress = if (totalNeeded != 0f) (achieved / totalNeeded).coerceIn(0f, 1f) else 1f
                                val remaining = (lw - gw).coerceAtLeast(0f)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Goal: %.1f %s".format(gw, unit),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        if (remaining <= 0f) "Goal reached!" else "%.1f %s to go".format(remaining, unit),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (remaining <= 0f) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                androidx.compose.material3.TextButton(onClick = { vm.setGoalWeight(null); goalInput = "" }) {
                                    Text("Clear goal")
                                }
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = goalInput,
                                    onValueChange = { goalInput = it.filter { c -> c.isDigit() || c == '.' }; goalError = null },
                                    label = { Text("Target ($unit)") },
                                    singleLine = true,
                                    isError = goalError != null,
                                    supportingText = goalError?.let { msg -> { Text(msg) } },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        val g = goalInput.toFloatOrNull()
                                        if (g == null || g <= 0f) goalError = "Enter a valid weight"
                                        else { vm.setGoalWeight(g); goalError = null }
                                    }
                                ) { Text("Set") }
                            }
                        }
                    }
                }
            }

            // Log entry form
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Log Today's Weight", style = MaterialTheme.typography.titleMedium)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = { weightInput = it.filter { c -> c.isDigit() || c == '.' }; weightError = null },
                                label = { Text("Weight ($unit)") },
                                singleLine = true,
                                isError = weightError != null,
                                supportingText = weightError?.let { msg -> { Text(msg) } },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    val w = weightInput.toFloatOrNull()
                                    if (w == null || w <= 0f) {
                                        weightError = "Enter a valid weight"
                                    } else {
                                        val today = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
                                        vm.insert(WeightEntryEntity(date = today, weight = w, unit = unit))
                                        weightInput = ""
                                        weightError = null
                                    }
                                }
                            ) { Text("Save") }
                        }
                    }
                }
            }

            // History
            if (records.isNotEmpty()) {
                item {
                    Text("History", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            records.forEachIndexed { i, record ->
                                if (i > 0) HorizontalDivider()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(record.date, style = MaterialTheme.typography.bodyMedium)
                                        if (i < records.size - 1) {
                                            val prev = records[i + 1].weight
                                            val diff = record.weight - prev
                                            val sign = if (diff > 0) "+" else ""
                                            Text(
                                                "$sign${"%.1f".format(diff)} from previous",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = when {
                                                    diff < 0 -> MaterialTheme.colorScheme.tertiary
                                                    diff > 0 -> MaterialTheme.colorScheme.error
                                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "%.1f %s".format(record.weight, record.unit),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(
                                            onClick = { vm.delete(record) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "Log your first weight entry to start tracking.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun WeightStat(label: String, value: String, unit: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = color)
            if (unit.isNotEmpty()) {
                Spacer(Modifier.width(2.dp))
                Text(unit, style = MaterialTheme.typography.bodySmall, color = color, modifier = Modifier.padding(bottom = 2.dp))
            }
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
private fun WeightChart(records: List<WeightEntryEntity>, unit: String) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Trend", style = MaterialTheme.typography.titleMedium)

            // Show oldest → newest left to right
            val ordered = records.reversed()
            val weights = ordered.map { it.weight }
            val minW = weights.min()
            val maxW = weights.max()
            val range = (maxW - minW).takeIf { it > 0f } ?: 1f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val padLeft = 8.dp.toPx()
                    val padRight = 8.dp.toPx()
                    val padTop = 12.dp.toPx()
                    val padBottom = 24.dp.toPx()
                    val chartW = w - padLeft - padRight
                    val chartH = h - padTop - padBottom

                    val points = weights.mapIndexed { i, weight ->
                        val x = padLeft + if (weights.size == 1) chartW / 2 else (i.toFloat() / (weights.size - 1)) * chartW
                        val y = padTop + chartH - ((weight - minW) / range) * chartH
                        Offset(x, y)
                    }

                    // Filled area under line
                    if (points.size >= 2) {
                        val fillPath = Path().apply {
                            moveTo(points.first().x, h - padBottom)
                            points.forEach { lineTo(it.x, it.y) }
                            lineTo(points.last().x, h - padBottom)
                            close()
                        }
                        drawPath(fillPath, primaryColor.copy(alpha = 0.12f))

                        val linePath = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            points.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(linePath, primaryColor, style = Stroke(width = 2.5.dp.toPx()))
                    }

                    // Dots
                    points.forEach { pt ->
                        drawCircle(primaryColor, radius = 4.dp.toPx(), center = pt)
                        drawCircle(surfaceVariant, radius = 2.dp.toPx(), center = pt)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    ordered.firstOrNull()?.date ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    ordered.lastOrNull()?.date ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}