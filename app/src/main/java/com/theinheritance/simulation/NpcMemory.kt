package com.theinheritance.simulation

import kotlinx.serialization.Serializable

@Serializable
data class NpcMemory(
    val npcId: Long,
    val day: Int,
    val content: String,
    val emotionalWeight: Float = 0.5f
)
