package com.theinheritance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.theinheritance.data.local.entity.GmMemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GmMemoryDao {
    @Query("SELECT * FROM gm_memories ORDER BY day DESC")
    fun observeAll(): Flow<List<GmMemoryEntity>>

    @Query("SELECT * FROM gm_memories WHERE isRevealed = 0 ORDER BY day DESC LIMIT :limit")
    suspend fun getUnrevealed(limit: Int): List<GmMemoryEntity>

    @Insert
    suspend fun insert(memory: GmMemoryEntity)

    @Query("UPDATE gm_memories SET isRevealed = 1 WHERE id = :id")
    suspend fun markRevealed(id: Long)
}
