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
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.SelfImprovement
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
    // Eating habits
    GlpTip(Icons.Default.Timer, "Eat Slowly", "GLP-1 slows digestion. Take at least 20 minutes per meal to avoid nausea and discomfort."),
    GlpTip(Icons.Default.Restaurant, "Small Portions", "Your appetite is suppressed — don't force yourself to finish a plate. Small, frequent meals work best."),
    GlpTip(Icons.Default.Alarm, "Don't Skip Meals", "Even with reduced appetite, eating regularly prevents muscle loss and stabilizes blood sugar."),
    GlpTip(Icons.Default.NoFood, "Stop When Full", "GLP-1 enhances satiety signals. Stop eating at the first sign of fullness to avoid nausea."),
    GlpTip(Icons.Default.Warning, "Limit High-Fat Foods", "Fatty foods can worsen nausea. Choose lean proteins, vegetables, and complex carbohydrates."),
    GlpTip(Icons.Default.MoodBad, "Reduce Alcohol", "Alcohol can cause low blood sugar and worsen GLP-1 side effects. Limit to occasional, small amounts."),

    // Nutrition
    GlpTip(Icons.Default.FitnessCenter, "Prioritize Protein", "Aim for 1g per pound of goal body weight daily to preserve muscle mass during weight loss."),
    GlpTip(Icons.Default.WaterDrop, "Stay Hydrated", "Drink at least 8 glasses of water daily. Dehydration is common on GLP-1 medications."),
    GlpTip(Icons.Default.BatterySaver, "Choose Fiber-Rich Foods", "Vegetables, legumes, and whole grains slow digestion further and keep blood sugar steady."),
    GlpTip(Icons.Default.Scale, "Track Calories Loosely", "Even with a suppressed appetite, aim for at least 1,200 kcal/day to meet micronutrient needs."),

    // Lifestyle
    GlpTip(Icons.Default.DirectionsWalk, "Stay Active", "Pair GLP-1 medication with 150 minutes of moderate exercise per week to maximize fat loss and muscle retention."),
    GlpTip(Icons.Default.SelfImprovement, "Manage Stress", "High cortisol can stall weight loss. Try walking, meditation, or deep breathing to keep stress in check."),
    GlpTip(Icons.Default.Bedtime, "Prioritize Sleep", "Poor sleep raises hunger hormones and undermines the effectiveness of GLP-1 therapy. Aim for 7–9 hours."),

    // Medication
    GlpTip(Icons.Default.LocalPharmacy, "Take It Consistently", "Inject on the same day each week and rotate injection sites to prevent skin changes and ensure steady absorption."),
    GlpTip(Icons.Default.Warning, "Watch for Side Effects", "Nausea, constipation, and fatigue are common early on. Most improve after a few weeks — tell your doctor if they persist."),
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
