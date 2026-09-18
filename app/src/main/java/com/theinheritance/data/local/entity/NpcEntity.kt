package com.theinheritance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "npcs")
data class NpcEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val role: String,
    val personality: String,
    val trustLevel: Int = 50,
    val isAlive: Boolean = true,
    val hasBooksOpen: Boolean = false,
    val isHidingSomething: Boolean = true
)
