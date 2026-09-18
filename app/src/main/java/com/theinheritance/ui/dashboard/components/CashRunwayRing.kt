package com.theinheritance.ui.dashboard.components

import androidx.compose.runtime.Composable
import com.theinheritance.ui.theme.ClayProgressRing

@Composable
fun CashRunwayRing(cashCents: Long) {
    val months = (cashCents / 100.0 / 6200.0).toFloat().coerceIn(0f, 1f)
    ClayProgressRing(progress = months, label = "Cash runway")
}
