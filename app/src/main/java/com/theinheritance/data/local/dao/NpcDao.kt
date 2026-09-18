package com.theinheritance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.theinheritance.data.local.entity.NpcEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NpcDao {
    @Query("SELECT * FROM npcs")
    fun observeAll(): Flow<List<NpcEntity>>

    @Query("SELECT * FROM npcs")
    suspend fun getAll(): List<NpcEntity>

    @Query("SELECT * FROM npcs WHERE id = :id")
    suspend fun getById(id: Long): NpcEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(npc: NpcEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(npcs: List<NpcEntity>)

    @Query("UPDATE npcs SET trustLevel = :trust WHERE id = :id")
    suspend fun setTrust(id: Long, trust: Int)

    @Query("DELETE FROM npcs WHERE id = :id")
    suspend fun delete(id: Long)
}
