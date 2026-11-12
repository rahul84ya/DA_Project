package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SaffronPrimary,
    onPrimary = OnSaffron,
    primaryContainer = SaffronContainer,
    onPrimaryContainer = SaffronDark,
    secondary = IndiaGreen,
    onSecondary = OnIndiaGreen,
    secondaryContainer = IndiaGreenLight,
    onSecondaryContainer = IndiaGreenDark,
    tertiary = TextNavyDark,
    background = BackgroundNeutral,
    onBackground = TextNavyDark,
    surface = SurfaceWhite,
    onSurface = TextNavyDark,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextNavyMedium,
    outline = BorderSubtle,
    outlineVariant = BorderMedium
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF9E66),
    onPrimary = Color(0xFF4A1A00),
    primaryContainer = Color(0xFF6B2600),
    onPrimaryContainer = Color(0xFFFFECE0),
    secondary = Color(0xFF66BB6A),
    onSecondary = Color(0xFF003914),
    secondaryContainer = Color(0xFF135028),
    onSecondaryContainer = Color(0xFFE8F5E9),
    tertiary = Color(0xFFE2E8F0),
    background = Color(0xFF0B1220),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF131F33),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve our trustworthy prop-tech tricolour brand
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
