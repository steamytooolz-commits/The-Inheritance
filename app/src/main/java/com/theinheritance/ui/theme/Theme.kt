package com.theinheritance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val Light = lightColorScheme(
    primary = ClayColors.Primary, secondary = ClayColors.Secondary, tertiary = ClayColors.AccentWarm,
    background = ClayColors.Background, surface = ClayColors.Surface,
    onBackground = ClayColors.Foreground, onSurface = ClayColors.Foreground,
    error = ClayColors.Danger
)
private val Dark = darkColorScheme(
    primary = ClayColors.DarkPrimary, secondary = ClayColors.Secondary,
    background = ClayColors.DarkBackground, surface = ClayColors.DarkSurface,
    onBackground = ClayColors.DarkForeground, onSurface = ClayColors.DarkForeground
)

@Composable
fun TheInheritanceTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, typography = ClayTypography, content = content)
}
