package com.example.nutrahelp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private data class Silhouette(
    val icon: ImageVector,
    val xFraction: Float,
    val yFraction: Float,
    val size: Dp,
    val rotation: Float,
    val tint: Color,
)

@Composable
fun FoodSilhouetteBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
    val secondary = MaterialTheme.colorScheme.secondary.copy(alpha = 0.07f)
    val tertiary = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.07f)

    Box(modifier = modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val w = maxWidth
            val h = maxHeight

            val silhouettes = listOf(
                Silhouette(Icons.Default.Restaurant,          0.72f,  0.02f, 150.dp,  20f, primary),
                Silhouette(Icons.Default.FitnessCenter,      -0.08f,  0.10f, 130.dp, -15f, secondary),
                Silhouette(Icons.Default.Spa,                 0.78f,  0.28f, 110.dp,  30f, tertiary),
                Silhouette(Icons.Default.LocalDrink,         -0.06f,  0.46f, 120.dp, -20f, primary),
                Silhouette(Icons.AutoMirrored.Filled.DirectionsRun,       0.66f,  0.56f, 160.dp,   5f, secondary),
                Silhouette(Icons.Default.Favorite,            0.07f,  0.72f, 100.dp,  -8f, tertiary),
                Silhouette(Icons.Default.Eco,                 0.74f,  0.80f, 120.dp,  25f, primary),
                Silhouette(Icons.Default.LocalFireDepartment, 0.28f,  0.04f,  90.dp,   0f, secondary),
            )

            silhouettes.forEach { s ->
                Icon(
                    imageVector = s.icon,
                    contentDescription = null,
                    tint = s.tint,
                    modifier = Modifier
                        .size(s.size)
                        .offset(x = w * s.xFraction, y = h * s.yFraction)
                        .rotate(s.rotation),
                )
            }
        }
        content()
    }
}