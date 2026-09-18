package com.theinheritance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_history")
data class RunHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val runSeed: Long,
    val endingId: String,
    val netIncomeCents: Long,
    val daysSurvived: Int,
    val fraudDiscovered: Boolean,
    val relationshipEndState: String,
    val completedAt: Long
)
