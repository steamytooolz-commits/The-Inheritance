package com.theinheritance.simulation

import kotlinx.serialization.Serializable

@Serializable
data class BusinessState(
    val day: Int = 1,
    val maxDays: Int = 30,
    val cashCents: Long = 420000,
    val businessName: String = "The Book Nook",
    val isAlive: Boolean = true,
    val epilogueId: String? = null,
    val runSeed: Long = 0L,
    val reputation: Float = 0.6f,
    val staffMorale: Float = 0.6f
)
