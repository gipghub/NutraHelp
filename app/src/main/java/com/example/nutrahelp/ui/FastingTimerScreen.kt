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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val fastingGoals = listOf(12, 16, 18, 20, 24)

private data class FastingSession(
    val id: Long = System.nanoTime(),
    val date: String,
    val durationSeconds: Long,
    val goalHours: Int
)

private fun formatElapsed(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastingTimerScreen(onBack: () -> Unit) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    var goalHours by remember { mutableStateOf(16) }
    var sessions by remember { mutableStateOf(listOf<FastingSession>()) }

    val goalSeconds = goalHours * 3600L
    val progress = if (goalSeconds > 0) (elapsedSeconds.toFloat() / goalSeconds).coerceAtMost(1f) else 0f
    val goalReached = elapsedSeconds >= goalSeconds && isRunning

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (true) {
                delay(1000L)
                elapsedSeconds++
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fasting Timer") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = formatElapsed(elapsedSeconds),
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (goalReached) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                        )

                        if (goalReached) {
                            Text(
                                "Goal reached!",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                "Goal: ${goalHours}h fast",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            "${(progress * 100).toInt()}% of ${goalHours}h goal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (!isRunning) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Fast duration goal", style = MaterialTheme.typography.labelMedium)
                                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                    fastingGoals.forEachIndexed { index, hours ->
                                        SegmentedButton(
                                            selected = goalHours == hours,
                                            onClick = { goalHours = hours },
                                            shape = SegmentedButtonDefaults.itemShape(index = index, count = fastingGoals.size),
                                            label = { Text("${hours}h") }
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isRunning) {
                                Button(
                                    onClick = {
                                        isRunning = false
                                        if (elapsedSeconds > 0) {
                                            val date = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
                                            sessions = listOf(FastingSession(date = date, durationSeconds = elapsedSeconds, goalHours = goalHours)) + sessions
                                        }
                                        elapsedSeconds = 0L
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Stop & Save")
                                }
                            } else {
                                if (elapsedSeconds > 0) {
                                    OutlinedButton(
                                        onClick = { elapsedSeconds = 0L },
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Reset") }
                                }
                                Button(
                                    onClick = { isRunning = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(if (elapsedSeconds > 0) "Resume" else "Start Fast")
                                }
                            }
                        }
                    }
                }
            }

            if (sessions.isNotEmpty()) {
                item {
                    Text("Past Sessions", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(sessions, key = { it.id }) { session ->
                    val pct = ((session.durationSeconds.toFloat() / (session.goalHours * 3600f)) * 100).toInt().coerceAtMost(100)
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(session.date, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "${session.goalHours}h goal · $pct% complete",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                formatElapsed(session.durationSeconds),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
