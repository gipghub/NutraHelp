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
    primary             = CarolinaBlue80,
    onPrimary           = CreamNeutral20,
    primaryContainer    = CarolinaContainer30,
    onPrimaryContainer  = CarolinaContainer90,
    secondary           = UNCNavy80,
    onSecondary         = CreamNeutral20,
    secondaryContainer  = NavyContainer30,
    onSecondaryContainer = NavyContainer90,
    tertiary            = Slate80,
    onTertiary          = CreamNeutral20,
    tertiaryContainer   = SlateContainer30,
    onTertiaryContainer = SlateContainer90,
    background          = CreamNeutral10,
    onBackground        = CreamNeutral90,
    surface             = CreamNeutral20,
    onSurface           = CreamNeutral90,
    surfaceVariant      = Color(0xFF1F3347),
    onSurfaceVariant    = CoolVariant80,
)

private val LightColorScheme = lightColorScheme(
    primary             = CarolinaBlue40,
    onPrimary           = Color.White,
    primaryContainer    = CarolinaContainer90,
    onPrimaryContainer  = CarolinaContainer30,
    secondary           = UNCNavy40,
    onSecondary         = Color.White,
    secondaryContainer  = NavyContainer90,
    onSecondaryContainer = NavyContainer30,
    tertiary            = Slate40,
    onTertiary          = Color.White,
    tertiaryContainer   = SlateContainer90,
    onTertiaryContainer = SlateContainer30,
    background          = CreamNeutral95,
    onBackground        = CreamNeutral10,
    surface             = CreamNeutral95,
    onSurface           = CreamNeutral10,
    surfaceVariant      = CoolVariant90,
    onSurfaceVariant    = CoolVariant30,
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