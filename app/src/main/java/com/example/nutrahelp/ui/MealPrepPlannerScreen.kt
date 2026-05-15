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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val defaultPrepItems = listOf(
    "Cook chicken breast (batch)",
    "Prepare brown rice",
    "Chop vegetables",
    "Hard boil eggs",
    "Portion protein shakes",
    "Wash and prep salad greens",
    "Cook sweet potatoes",
    "Prepare overnight oats"
)

private data class PrepPlan(
    val id: Long = System.nanoTime(),
    val weekLabel: String,
    val items: List<PrepItem>
)

private data class PrepItem(
    val id: Long = System.nanoTime(),
    val name: String,
    var done: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPrepPlannerScreen(onBack: () -> Unit) {
    var currentItems by remember {
        mutableStateOf(defaultPrepItems.map { PrepItem(name = it) })
    }
    var newItemText by remember { mutableStateOf("") }
    var savedPlans by remember { mutableStateOf(listOf<PrepPlan>()) }
    var weekLabel by remember { mutableStateOf("This Week") }
    var formError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Prep Planner") },
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
                        Text("Current Prep List", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = weekLabel,
                            onValueChange = { weekLabel = it },
                            label = { Text("Week label") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        currentItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    item.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                    color = if (item.done) MaterialTheme.colorScheme.onSurfaceVariant
                                            else MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(onClick = {
                                    currentItems = currentItems.toMutableList().also {
                                        it[index] = it[index].copy(done = !it[index].done)
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (item.done) Icons.Default.CheckCircle
                                                      else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = if (item.done) "Mark undone" else "Mark done",
                                        tint = if (item.done) MaterialTheme.colorScheme.primary
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newItemText,
                                onValueChange = { newItemText = it; formError = false },
                                label = { Text("Add item") },
                                singleLine = true,
                                isError = formError,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                if (newItemText.isBlank()) {
                                    formError = true
                                } else {
                                    currentItems = currentItems + PrepItem(name = newItemText.trim())
                                    newItemText = ""
                                    formError = false
                                }
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Add item")
                            }
                        }

                        val doneCount = currentItems.count { it.done }
                        Text(
                            "$doneCount / ${currentItems.size} tasks done",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                savedPlans = (listOf(
                                    PrepPlan(
                                        weekLabel = weekLabel.ifBlank { "This Week" },
                                        items = currentItems.map { it.copy() }
                                    )
                                ) + savedPlans).sortedByDescending { it.id }
                                currentItems = defaultPrepItems.map { PrepItem(name = it) }
                                weekLabel = "This Week"
                                formError = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save & Start New")
                        }
                    }
                }
            }

            if (savedPlans.isNotEmpty()) {
                item {
                    Text("Saved Plans", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
                items(savedPlans, key = { it.id }) { plan ->
                    val done = plan.items.count { it.done }
                    val total = plan.items.size
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(plan.weekLabel, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "$done/$total done",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (done == total) MaterialTheme.colorScheme.tertiary
                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            plan.items.filter { it.done }.take(3).forEach { item ->
                                Text(
                                    "✓ ${item.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            val remaining = plan.items.filter { !it.done }
                            remaining.take(3).forEach { item ->
                                Text(
                                    "· ${item.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (remaining.size > 3) {
                                Text(
                                    "+${remaining.size - 3} more items",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
