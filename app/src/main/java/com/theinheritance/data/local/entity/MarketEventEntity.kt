package com.theinheritance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "market_events")
data class MarketEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val day: Int,
    val eventType: String,
    val severity: Float,
    val narrative: String,
    val isResolved: Boolean = false
)
