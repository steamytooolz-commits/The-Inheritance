package com.theinheritance.ui.dashboard.components

import androidx.compose.runtime.Composable
import com.theinheritance.ui.theme.ClayProgressRing

@Composable
fun ProfitRing(netIncomeCents: Long, revenueCents: Long) {
    val margin = if (revenueCents > 0) (netIncomeCents.toFloat() / revenueCents).coerceIn(0f, 1f) else 0f
    ClayProgressRing(progress = margin, label = "Profit margin")
}
