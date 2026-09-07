package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VibrantLightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = PureWhite,
    primaryContainer = IndigoContainer,
    onPrimaryContainer = IndigoDeepNavy,
    secondary = VibrantOrange,
    onSecondary = PureWhite,
    secondaryContainer = VibrantOrangeLight,
    onSecondaryContainer = VibrantOrangeDark,
    tertiary = EmeraldSuccess,
    onTertiary = PureWhite,
    background = IndigoLightBg,
    onBackground = Slate900,
    surface = PureWhite,
    onSurface = Slate900,
    surfaceVariant = Slate50,
    onSurfaceVariant = Slate700,
    outline = Slate200,
    outlineVariant = Slate100
)

private val VibrantDarkColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = PureWhite,
    primaryContainer = IndigoDeepNavy,
    onPrimaryContainer = IndigoContainer,
    secondary = VibrantOrange,
    onSecondary = PureWhite,
    background = Color(0xFF0F172A),
    onBackground = PureWhite,
    surface = Color(0xFF1E1B4B),
    onSurface = PureWhite,
    surfaceVariant = Color(0xFF282468),
    onSurfaceVariant = Slate200,
    outline = Color(0xFF334155)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) VibrantDarkColorScheme else VibrantLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
