package com.theinheritance.ui.dashboard.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.theinheritance.ui.theme.ClayProgressRing

@Composable
fun DebtToEquityRing(debtCents: Long, equityCents: Long, modifier: Modifier = Modifier) {
    val ratio = if (equityCents > 0) (debtCents.toFloat() / equityCents).coerceIn(0f, 1f) else 1f
    ClayProgressRing(progress = 1f - ratio, label = "Health", modifier = modifier)
}
