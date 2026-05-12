package com.example.nutrahelp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// ── Data model ───────────────────────────────────────────────────────────────

private data class DataPoint(val date: LocalDate, val value: Float)

private enum class Period(val label: String, val days: Int) {
    WEEK("7 Days", 7),
    MONTH("30 Days", 30),
    THREE_MONTHS("90 Days", 90),
}

private enum class Metric(
    val label: String,
    val unit: String,
    val goal: Float?,
    val isLine: Boolean,
    val decimals: Int = 1,
) {
    WEIGHT("Weight", "kg", null, true),
    CALORIES("Calories", "kcal", 1600f, false, 0),
    PROTEIN("Protein", "g", 120f, false, 0),
    WATER("Water", "gl", 8f, false, 0),
    STEPS("Steps", "", 8000f, false, 0),
}

// ── Sample data (90 days, realistic GLP-1 trends) ────────────────────────────

private val sampleData: Map<Metric, List<DataPoint>> by lazy {
    val today = LocalDate.now()
    mapOf(
        Metric.WEIGHT to (0..89).map { i ->
            val base = 102f - i * 0.07f
            val noise = listOf(0f, 0.3f, -0.2f, 0.1f, -0.4f, 0.2f, -0.1f)[i % 7]
            DataPoint(today.minusDays((89 - i).toLong()), (base + noise).coerceAtLeast(85f))
        },
        Metric.CALORIES to (0..89).map { i ->
            val base = listOf(1250f, 1380f, 1100f, 1450f, 1320f, 980f, 1510f)[i % 7]
            DataPoint(today.minusDays((89 - i).toLong()), base)
        },
        Metric.PROTEIN to (0..89).map { i ->
            val base = listOf(82f, 105f, 78f, 115f, 95f, 70f, 118f)[i % 7]
            DataPoint(today.minusDays((89 - i).toLong()), base)
        },
        Metric.WATER to (0..89).map { i ->
            val base = listOf(5f, 7f, 8f, 6f, 8f, 4f, 7f)[i % 7]
            DataPoint(today.minusDays((89 - i).toLong()), base)
        },
        Metric.STEPS to (0..89).map { i ->
            val base = listOf(5200f, 7800f, 9100f, 4300f, 8600f, 11200f, 6400f)[i % 7]
            DataPoint(today.minusDays((89 - i).toLong()), base)
        },
    )
}

// ── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressChartScreen(onBack: () -> Unit = {}) {
    var selectedMetricIndex by remember { mutableIntStateOf(0) }
    var selectedPeriod by remember { mutableIntStateOf(0) }

    val metric = Metric.entries[selectedMetricIndex]
    val period = Period.entries[selectedPeriod]
    val points = remember(metric, period) {
        sampleData[metric]!!.takeLast(period.days)
    }

    val avg = points.map { it.value }.average().toFloat()
    val min = points.minOf { it.value }
    val max = points.maxOf { it.value }
    val latest = points.lastOrNull()?.value ?: 0f
    val first = points.firstOrNull()?.value ?: 0f
    val change = latest - first

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress Charts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
            // Metric tabs
            item {
                TabRow(selectedTabIndex = selectedMetricIndex) {
                    Metric.entries.forEachIndexed { i, m ->
                        Tab(
                            selected = selectedMetricIndex == i,
                            onClick = { selectedMetricIndex = i },
                            text = { Text(m.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // Period filter chips
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    Period.entries.forEachIndexed { i, p ->
                        FilterChip(
                            selected = selectedPeriod == i,
                            onClick = { selectedPeriod = i },
                            label = { Text(p.label, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }

            // Chart card
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(metric.label, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${period.label} overview",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                val fmt = if (metric.decimals > 0) "%.1f" else "%.0f"
                                Text(
                                    "${"$fmt".format(latest)} ${metric.unit}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("latest", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            val textMeasurer = rememberTextMeasurer()
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                if (metric.isLine) {
                                    drawLineChart(
                                        points = points,
                                        lineColor = primaryColor,
                                        fillColor = primaryColor.copy(alpha = 0.12f),
                                        trackColor = surfaceVariant,
                                        goalValue = metric.goal,
                                        goalColor = secondaryColor,
                                    )
                                } else {
                                    drawBarChart(
                                        points = points,
                                        barColor = primaryColor,
                                        goalBarColor = secondaryColor,
                                        goalValue = metric.goal,
                                        surfaceVariant = surfaceVariant,
                                        textMeasurer = textMeasurer,
                                        labelColor = onSurfaceVariant,
                                    )
                                }
                            }
                        }

                        // X-axis date labels
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            val fmt = DateTimeFormatter.ofPattern("d MMM")
                            Text(points.firstOrNull()?.date?.format(fmt) ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(points.lastOrNull()?.date?.format(fmt) ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Summary stats card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("${period.label} Summary", style = MaterialTheme.typography.titleSmall)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            SummaryStatItem(
                                label = if (metric == Metric.WEIGHT) "Start" else "Avg",
                                value = if (metric == Metric.WEIGHT)
                                    "%.1f".format(first)
                                else
                                    "%.${metric.decimals}f".format(avg),
                                unit = metric.unit
                            )
                            SummaryStatItem(label = if (metric == Metric.WEIGHT) "Current" else "Best", value = "%.${metric.decimals}f".format(if (metric == Metric.WEIGHT) latest else max), unit = metric.unit)
                            SummaryStatItem(label = if (metric == Metric.WEIGHT) "Change" else "Low", value = (if (metric == Metric.WEIGHT) {
                                val sign = if (change > 0) "+" else ""
                                "$sign${"%.1f".format(change)}"
                            } else "%.${metric.decimals}f".format(min)), unit = metric.unit)
                        }

                        if (metric.goal != null) {
                            HorizontalDivider()
                            val daysOnGoal = points.count { it.value >= metric.goal }
                            val pct = (daysOnGoal * 100f / points.size).roundToInt()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Days on goal (≥ ${metric.goal.toInt()} ${metric.unit})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$daysOnGoal / ${points.size}  ($pct%)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = primaryColor)
                            }
                        }

                        if (metric == Metric.WEIGHT && change < 0f) {
                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total lost this period", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${"%.1f".format(-change)} kg", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                }
            }

            // Data table (last 7 entries)
            item {
                Text("Recent Entries", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        val fmt = DateTimeFormatter.ofPattern("EEE, d MMM")
                        points.takeLast(7).reversed().forEachIndexed { i, pt ->
                            if (i > 0) HorizontalDivider()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(pt.date.format(fmt), style = MaterialTheme.typography.bodyMedium)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (metric.goal != null) {
                                        val met = pt.value >= metric.goal
                                        Text(
                                            if (met) "✓ Goal" else "✗ Goal",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (met) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        "%.${metric.decimals}f ${metric.unit}".format(pt.value),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = primaryColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Chart drawing helpers ────────────────────────────────────────────────────

private fun DrawScope.drawLineChart(
    points: List<DataPoint>,
    lineColor: Color,
    fillColor: Color,
    trackColor: Color,
    goalValue: Float?,
    goalColor: Color,
) {
    if (points.isEmpty()) return
    val pad = Offset(12.dp.toPx(), 12.dp.toPx())
    val chartW = size.width - pad.x * 2
    val chartH = size.height - pad.y * 2

    val values = points.map { it.value }
    val minV = values.min()
    val maxV = values.max()
    val range = (maxV - minV).takeIf { it > 0f } ?: 1f

    fun xOf(i: Int) = pad.x + if (points.size == 1) chartW / 2 else (i.toFloat() / (points.size - 1)) * chartW
    fun yOf(v: Float) = pad.y + chartH - ((v - minV) / range) * chartH

    // Horizontal goal line
    if (goalValue != null && goalValue in minV..maxV) {
        val gy = yOf(goalValue)
        drawLine(goalColor.copy(alpha = 0.5f), Offset(pad.x, gy), Offset(pad.x + chartW, gy), strokeWidth = 1.5.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))
    }

    val coords = points.indices.map { Offset(xOf(it), yOf(values[it])) }

    // Fill
    val fill = Path().apply {
        moveTo(coords.first().x, size.height - pad.y)
        coords.forEach { lineTo(it.x, it.y) }
        lineTo(coords.last().x, size.height - pad.y)
        close()
    }
    drawPath(fill, fillColor)

    // Line
    val line = Path().apply {
        moveTo(coords.first().x, coords.first().y)
        coords.drop(1).forEach { lineTo(it.x, it.y) }
    }
    drawPath(line, lineColor, style = Stroke(width = 2.5.dp.toPx()))

    // Dots (only if few points)
    if (points.size <= 30) {
        coords.forEach { pt ->
            drawCircle(lineColor, radius = 4.dp.toPx(), center = pt)
            drawCircle(Color.White, radius = 2.dp.toPx(), center = pt)
        }
    }
}

private fun DrawScope.drawBarChart(
    points: List<DataPoint>,
    barColor: Color,
    goalBarColor: Color,
    goalValue: Float?,
    surfaceVariant: Color,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    labelColor: Color,
) {
    if (points.isEmpty()) return
    val padTop = 12.dp.toPx()
    val padBottom = 24.dp.toPx()
    val padSide = 4.dp.toPx()
    val chartH = size.height - padTop - padBottom
    val chartW = size.width - padSide * 2

    val values = points.map { it.value }
    val maxV = (goalValue?.coerceAtLeast(values.max()) ?: values.max()) * 1.05f

    val totalBars = points.size
    val barW = (chartW / totalBars) * 0.65f
    val gap = (chartW / totalBars) * 0.35f

    // Horizontal goal line
    if (goalValue != null) {
        val gy = padTop + chartH - (goalValue / maxV) * chartH
        drawLine(goalBarColor.copy(alpha = 0.6f), Offset(padSide, gy), Offset(padSide + chartW, gy), strokeWidth = 1.5.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))
    }

    points.forEachIndexed { i, pt ->
        val barH = (pt.value / maxV) * chartH
        val x = padSide + i * (barW + gap) + gap / 2
        val y = padTop + chartH - barH
        val color = if (goalValue != null && pt.value >= goalValue) goalBarColor else barColor
        drawRoundRect(
            color = color.copy(alpha = 0.8f),
            topLeft = Offset(x, y),
            size = Size(barW, barH),
            cornerRadius = CornerRadius(3.dp.toPx())
        )
    }

    // Y-axis label for goal
    if (goalValue != null && points.size <= 30) {
        val gy = padTop + chartH - (goalValue / maxV) * chartH
        val label = "Goal: ${goalValue.toInt()}"
        val result = textMeasurer.measure(label, TextStyle(fontSize = 9.sp, color = goalBarColor))
        drawText(result, topLeft = Offset(size.width - result.size.width - 4.dp.toPx(), gy - result.size.height - 2.dp.toPx()))
    }
}

@Composable
private fun SummaryStatItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            if (unit.isNotEmpty()) {
                Spacer(Modifier.width(2.dp))
                Text(unit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 1.dp))
            }
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}