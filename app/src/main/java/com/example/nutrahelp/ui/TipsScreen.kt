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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private data class GlpTip(val icon: ImageVector, val title: String, val body: String)

private val tips = listOf(
    GlpTip(Icons.Default.Timer, "Eat Slowly", "GLP-1 slows digestion. Take at least 20 minutes per meal to avoid nausea and discomfort."),
    GlpTip(Icons.Default.FitnessCenter, "Prioritize Protein", "Aim for 1g per pound of goal body weight daily to preserve muscle mass during weight loss."),
    GlpTip(Icons.Default.WaterDrop, "Stay Hydrated", "Drink at least 8 glasses of water daily. Dehydration is common on GLP-1 medications."),
    GlpTip(Icons.Default.Restaurant, "Small Portions", "Your appetite is suppressed — don't force yourself to finish a plate. Small, frequent meals work best."),
    GlpTip(Icons.Default.Warning, "Limit High-Fat Foods", "Fatty foods can worsen nausea. Choose lean proteins, vegetables, and complex carbohydrates."),
    GlpTip(Icons.Default.Alarm, "Don't Skip Meals", "Even with reduced appetite, eating regularly prevents muscle loss and stabilizes blood sugar."),
)

@Composable
fun TipsScreen() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                "GLP-1 Nutrition Tips",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(tips) { tip ->
            TipCard(tip)
        }
    }
}

@Composable
private fun TipCard(tip: GlpTip) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                tip.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(tip.title, style = MaterialTheme.typography.titleSmall)
                Text(
                    tip.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
