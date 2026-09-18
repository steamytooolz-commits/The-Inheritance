package com.theinheritance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gm_memories")
data class GmMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val day: Int,
    val actionType: String,
    val content: String,
    val isRevealed: Boolean = false,
    val isMisremembered: Boolean = false
)
