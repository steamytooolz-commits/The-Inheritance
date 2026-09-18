package com.theinheritance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ClayColors.LightPrimary,
    onPrimary = ClayColors.LightOnPrimary,
    primaryContainer = ClayColors.LightPrimaryContainer,
    onPrimaryContainer = ClayColors.LightOnPrimaryContainer,
    secondary = ClayColors.LightSecondary,
    onSecondary = ClayColors.LightOnSecondary,
    secondaryContainer = ClayColors.LightSecondaryContainer,
    onSecondaryContainer = ClayColors.LightOnSecondaryContainer,
    tertiary = ClayColors.LightWarning,
    onTertiary = ClayColors.LightOnPrimary,
    background = ClayColors.LightBackground,
    onBackground = ClayColors.LightForeground,
    surface = ClayColors.LightSurface,
    onSurface = ClayColors.LightForeground,
    surfaceVariant = ClayColors.LightSurfaceVariant,
    onSurfaceVariant = ClayColors.LightForegroundMuted,
    outline = ClayColors.LightBorderStrong,
    outlineVariant = ClayColors.LightBorder,
    error = ClayColors.LightDanger,
    onError = ClayColors.LightOnPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = ClayColors.DarkPrimary,
    onPrimary = ClayColors.DarkOnPrimary,
    primaryContainer = ClayColors.DarkPrimaryContainer,
    onPrimaryContainer = ClayColors.DarkOnPrimaryContainer,
    secondary = ClayColors.DarkSecondary,
    onSecondary = ClayColors.DarkOnSecondary,
    secondaryContainer = ClayColors.DarkSecondaryContainer,
    onSecondaryContainer = ClayColors.DarkOnSecondaryContainer,
    tertiary = ClayColors.DarkWarning,
    onTertiary = ClayColors.DarkOnPrimary,
    background = ClayColors.DarkBackground,
    onBackground = ClayColors.DarkForeground,
    surface = ClayColors.DarkSurface,
    onSurface = ClayColors.DarkForeground,
    surfaceVariant = ClayColors.DarkSurfaceVariant,
    onSurfaceVariant = ClayColors.DarkForegroundMuted,
    outline = ClayColors.DarkBorderStrong,
    outlineVariant = ClayColors.DarkBorder,
    error = ClayColors.DarkDanger,
    onError = ClayColors.DarkOnPrimary
)

@Composable
fun TheInheritanceTheme(
    dark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (dark) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = ClayTypography,
        content = content
    )
}

