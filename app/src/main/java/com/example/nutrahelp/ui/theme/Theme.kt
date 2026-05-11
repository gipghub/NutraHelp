package com.example.nutrahelp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Amber80,
    onPrimary = WarmNeutral20,
    primaryContainer = AmberContainer30,
    onPrimaryContainer = AmberContainer90,
    secondary = HerbGreen80,
    onSecondary = WarmNeutral20,
    secondaryContainer = HerbContainer30,
    onSecondaryContainer = HerbContainer90,
    tertiary = Turmeric80,
    onTertiary = WarmNeutral20,
    tertiaryContainer = TurmericContainer30,
    onTertiaryContainer = TurmericContainer90,
    background = WarmNeutral10,
    onBackground = WarmNeutral90,
    surface = WarmNeutral20,
    onSurface = WarmNeutral90,
    surfaceVariant = WarmVariant30,
    onSurfaceVariant = WarmVariant80,
)

private val LightColorScheme = lightColorScheme(
    primary = Amber40,
    onPrimary = Color.White,
    primaryContainer = AmberContainer90,
    onPrimaryContainer = AmberContainer30,
    secondary = HerbGreen40,
    onSecondary = Color.White,
    secondaryContainer = HerbContainer90,
    onSecondaryContainer = HerbContainer30,
    tertiary = Turmeric40,
    onTertiary = Color.White,
    tertiaryContainer = TurmericContainer90,
    onTertiaryContainer = TurmericContainer30,
    background = WarmNeutral95,
    onBackground = WarmNeutral10,
    surface = WarmNeutral95,
    onSurface = WarmNeutral10,
    surfaceVariant = WarmVariant90,
    onSurfaceVariant = WarmVariant30,
)

@Composable
fun NutraHelpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}