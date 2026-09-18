package com.theinheritance.ui.theme

import androidx.compose.ui.graphics.Color

object ClayColors {
    // Light Mode Palette (Ultra-clean, high-contrast, professional)
    val LightBackground = Color(0xFFF8FAFC)       // Slate 50
    val LightSurface = Color(0xFFFFFFFF)          // Crisp White
    val LightSurfaceVariant = Color(0xFFF1F5F9)   // Slate 100
    val LightPrimary = Color(0xFF4F46E5)          // Indigo 600
    val LightOnPrimary = Color(0xFFFFFFFF)
    val LightPrimaryContainer = Color(0xFFEEF2FF) // Indigo 50
    val LightOnPrimaryContainer = Color(0xFF312E81)// Indigo 900
    val LightSecondary = Color(0xFF0284C7)        // Sky 600
    val LightOnSecondary = Color(0xFFFFFFFF)
    val LightSecondaryContainer = Color(0xFFE0F2FE)// Sky 100
    val LightOnSecondaryContainer = Color(0xFF0369A1)
    val LightForeground = Color(0xFF0F172A)       // Slate 900 (High contrast)
    val LightForegroundMuted = Color(0xFF475569)  // Slate 600
    val LightBorder = Color(0xFFE2E8F0)           // Slate 200
    val LightBorderStrong = Color(0xFFCBD5E1)     // Slate 300
    val LightSuccess = Color(0xFF16A34A)          // Emerald 600
    val LightDanger = Color(0xFFDC2626)           // Red 600
    val LightWarning = Color(0xFFD97706)          // Amber 600

    // Dark Mode Palette (Deep rich obsidian & indigo, vivid high-contrast text)
    val DarkBackground = Color(0xFF0F1117)        // Deep Charcoal
    val DarkSurface = Color(0xFF181C26)           // Elevated Slate
    val DarkSurfaceVariant = Color(0xFF232838)    // Card Highlight
    val DarkPrimary = Color(0xFF818CF8)           // Indigo 400
    val DarkOnPrimary = Color(0xFF0F172A)
    val DarkPrimaryContainer = Color(0xFF312E81)  // Indigo 900
    val DarkOnPrimaryContainer = Color(0xFFE0E7FF)
    val DarkSecondary = Color(0xFF38BDF8)         // Sky 400
    val DarkOnSecondary = Color(0xFF082F49)
    val DarkSecondaryContainer = Color(0xFF075985)
    val DarkOnSecondaryContainer = Color(0xFFE0F2FE)
    val DarkForeground = Color(0xFFF8FAFC)        // Slate 50 (Ultra crisp)
    val DarkForegroundMuted = Color(0xFF94A3B8)   // Slate 400
    val DarkBorder = Color(0xFF334155)            // Slate 700
    val DarkBorderStrong = Color(0xFF475569)      // Slate 600
    val DarkSuccess = Color(0xFF4ADE80)           // Green 400
    val DarkDanger = Color(0xFFF87171)            // Red 400
    val DarkWarning = Color(0xFFFBBF24)           // Amber 400

    // Common Fallbacks for backwards compatibility
    val Background = LightBackground
    val Surface = LightSurface
    val Primary = LightPrimary
    val Secondary = LightSecondary
    val AccentWarm = LightWarning
    val Foreground = LightForeground
    val Muted = LightForegroundMuted
    val Success = LightSuccess
    val Danger = LightDanger
    val Border = LightBorder
}

