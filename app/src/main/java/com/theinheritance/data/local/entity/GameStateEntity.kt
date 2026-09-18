package com.theinheritance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey val id: Int = 1,
    val currentDay: Int = 1,
    val maxDays: Int = 30,
    val cashCents: Long = 420000,
    val businessName: String = "The Book Nook",
    val isAlive: Boolean = true,
    val epilogueId: String? = null,
    val runSeed: Long = 0
)
