package com.theinheritance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "npc_memories")
data class NpcMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val npcId: Long,
    val day: Int,
    val content: String,
    val emotionalWeight: Float = 0.5f
)
