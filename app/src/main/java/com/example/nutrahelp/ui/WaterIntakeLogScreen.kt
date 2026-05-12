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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrahelp.data.WaterEntryEntity
import com.example.nutrahelp.viewmodel.WaterViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val ML_PER_OZ = 29.5735f
private fun mlToOz(ml: Int): Float = ml / ML_PER_OZ
private fun ozToMl(oz: Float): Int = (oz * ML_PER_OZ).toInt()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterIntakeLogScreen(onBack: () -> Unit, vm: WaterViewModel = viewModel()) {
    val useMetric = LocalUseMetric.current
    val timeFmt = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val dateFmt = remember { SimpleDateFormat("EEE", Locale.getDefault()) }
    val dateFmtKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val defaultGoalMl = if (useMetric) 2000 else ozToMl(64f)
    var goalMl by remember(useMetric) { mutableIntStateOf(defaultGoalMl) }
    var goalInput by remember(useMetric) { mutableStateOf(if (useMetric) "2000" else "64") }
    var customAmount by remember { mutableStateOf("") }

    val entries by vm.todayEntries.collectAsState()
    val weeklyTotalsDb by vm.weeklyTotals.collectAsState()

    val unit = if (useMetric) "ml" else "oz"
    val glassSize = if (useMetric) 250 else ozToMl(8f)
    val totalGlasses = 8

    val todayTotal = entries.sumOf { it.amountMl }
    val progress = if (goalMl > 0) (todayTotal.toFloat() / goalMl).coerceAtMost(1f) else 0f
    val glassesConsumed = (todayTotal / glassSize).coerceAtMost(totalGlasses)
    val goalReached = todayTotal >= goalMl

    // Build 7-day array from DB totals
    val weekTotals = remember(weeklyTotalsDb) {
        val map = weeklyTotalsDb.associate { it.date to it.total }
        IntArray(7) { offset ->
            val cal = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -offset) }
            map[dateFmtKey.format(cal.time)] ?: 0
        }
    }

    val quickAmounts = if (useMetric)
        listOf(150 to "150 ml", 250 to "250 ml", 500 to "500 ml", 750 to "750 ml")
    else
        listOf(ozToMl(4f) to "4 oz", ozToMl(8f) to "8 oz", ozToMl(12f) to "12 oz", ozToMl(16f) to "16 oz")

    fun displayAmt(ml: Int) = if (useMetric) "$ml ml" else "%.0f oz".format(mlToOz(ml))
    fun displayGoal() = if (useMetric) "$goalMl ml" else "%.0f oz".format(mlToOz(goalMl))

    fun logAmount(ml: Int) {
        vm.insert(WaterEntryEntity(
            date = dateFmtKey.format(Date()),
            time = timeFmt.format(Date()),
            amountMl = ml
        ))
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = if (goalReached) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface

    // Week day labels
    val dayLabels = remember {
        (0..6).map { offset ->
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }
            dateFmt.format(c.time)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Water Intake") },
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
            // ── Progress ring ─────────────────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                            Canvas(modifier = Modifier.size(160.dp)) {
                                val stroke = 18.dp.toPx()
                                val inset = stroke / 2f
                                val arcSize = Size(size.width - stroke, size.height - stroke)
                                val topLeft = Offset(inset, inset)
                                drawArc(
                                    color = trackColor,
                                    startAngle = -220f,
                                    sweepAngle = 260f,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(stroke, cap = StrokeCap.Round)
                                )
                                if (progress > 0f) {
                                    drawArc(
                                        color = progressColor,
                                        startAngle = -220f,
                                        sweepAngle = 260f * progress,
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = arcSize,
                                        style = Stroke(stroke, cap = StrokeCap.Round)
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    displayAmt(todayTotal),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = progressColor
                                )
                                Text(
                                    "of ${displayGoal()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (goalReached) {
                            Text(
                                "Daily goal reached!",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Glass counter
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "$glassesConsumed / $totalGlasses glasses",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                (1..totalGlasses).forEach { i ->
                                    Icon(
                                        Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp),
                                        tint = if (i <= glassesConsumed) progressColor
                                               else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            }
                        }

                        // Last drink
                        if (entries.isNotEmpty()) {
                            Text(
                                "Last drink at ${entries.first().time}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ── Quick add ─────────────────────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Quick Add", style = MaterialTheme.typography.titleSmall)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickAmounts.forEach { (ml, label) ->
                                OutlinedButton(
                                    onClick = { logAmount(ml) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                ) {
                                    Text(label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customAmount,
                                onValueChange = { customAmount = it },
                                label = { Text("Custom ($unit)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            Button(onClick = {
                                val ml = if (useMetric) customAmount.toIntOrNull()
                                         else customAmount.toFloatOrNull()?.let { ozToMl(it) }
                                if (ml != null && ml > 0) { logAmount(ml); customAmount = "" }
                            }) { Text("Add") }
                        }
                    }
                }
            }

            // ── Goal setting ──────────────────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = goalInput,
                            onValueChange = { goalInput = it },
                            label = { Text("Daily goal ($unit)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(onClick = {
                            val ml = if (useMetric) goalInput.toIntOrNull()
                                     else goalInput.toFloatOrNull()?.let { ozToMl(it) }
                            if (ml != null && ml > 0) goalMl = ml
                        }) { Text("Set Goal") }
                    }
                }
            }

            // ── 7-day bar chart ───────────────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("This Week", style = MaterialTheme.typography.titleSmall)
                        val barColor = progressColor
                        val trackCol = trackColor
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        ) {
                            val barWidth = size.width / 7f * 0.55f
                            val gap = size.width / 7f
                            val maxVal = weekTotals.max().coerceAtLeast(goalMl)
                            (0..6).reversed().forEachIndexed { displayIdx, dayIdx ->
                                val x = displayIdx * gap + gap * 0.225f
                                val fillRatio = if (maxVal > 0) weekTotals[dayIdx].toFloat() / maxVal else 0f
                                val barHeight = size.height * fillRatio
                                // track
                                drawRoundRect(
                                    color = trackCol,
                                    topLeft = Offset(x, 0f),
                                    size = Size(barWidth, size.height),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f)
                                )
                                // fill
                                if (barHeight > 0f) {
                                    drawRoundRect(
                                        color = if (dayIdx == 0) barColor else barColor.copy(alpha = 0.5f),
                                        topLeft = Offset(x, size.height - barHeight),
                                        size = Size(barWidth, barHeight),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f)
                                    )
                                }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            (6 downTo 0).forEach { dayIdx ->
                                Text(
                                    dayLabels[dayIdx],
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (dayIdx == 0) progressColor
                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // ── Today's log ───────────────────────────────────────────────────
            if (entries.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Today's Log (${entries.size})", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(
                            onClick = { vm.resetToday() },
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) { Text("Reset") }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(entries, key = { it.id }) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(
                                    Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text(
                                        "+${displayAmt(entry.amountMl)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        entry.time,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(
                                onClick = { vm.delete(entry) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "No water logged yet today. Use Quick Add above to get started.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}