package com.theinheritance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.theinheritance.data.local.entity.MarketEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketEventDao {
    @Query("SELECT * FROM market_events ORDER BY day DESC")
    fun observeAll(): Flow<List<MarketEventEntity>>

    @Insert
    suspend fun insert(event: MarketEventEntity): Long

    @Query("UPDATE market_events SET isResolved = 1 WHERE id = :id")
    suspend fun markResolved(id: Long)
}
