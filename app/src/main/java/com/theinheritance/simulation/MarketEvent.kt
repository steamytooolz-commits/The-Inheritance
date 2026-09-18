package com.theinheritance.simulation

import kotlinx.serialization.Serializable

@Serializable
data class MarketEvent(
    val day: Int,
    val eventType: String,
    val severity: Float,
    val narrative: String,
    val cashDeltaCents: Long = 0L
)
