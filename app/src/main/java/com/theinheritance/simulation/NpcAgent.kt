package com.theinheritance.simulation

import kotlinx.serialization.Serializable

@Serializable
data class NpcAgent(
    val id: Long,
    val name: String,
    val role: String,
    val personality: String,
    val trustLevel: Int = 50,
    val isAlive: Boolean = true
)
