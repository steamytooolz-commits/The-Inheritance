package com.theinheritance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.theinheritance.data.local.entity.JournalEntryEntity
import com.theinheritance.data.local.entity.JournalLineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY date DESC, id DESC")
    fun observeAll(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries ORDER BY date ASC, id ASC")
    suspend fun getAllEntries(): List<JournalEntryEntity>

    @Query("SELECT * FROM journal_lines")
    suspend fun getAllLines(): List<JournalLineEntity>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getById(id: Long): JournalEntryEntity?

    @Query("SELECT * FROM journal_lines WHERE journalEntryId = :entryId")
    suspend fun getLinesForEntry(entryId: Long): List<JournalLineEntity>

    @Insert
    suspend fun insertEntry(entry: JournalEntryEntity): Long

    @Insert
    suspend fun insertLines(lines: List<JournalLineEntity>)

    @Transaction
    suspend fun insertWithLines(entry: JournalEntryEntity, lines: List<JournalLineEntity>): Long {
        val id = insertEntry(entry)
        insertLines(lines.map { it.copy(journalEntryId = id) })
        return id
    }

    @Query("UPDATE journal_entries SET isVoided = 1 WHERE id = :id")
    suspend fun voidEntry(id: Long)

    @Query("SELECT * FROM journal_lines WHERE accountId = :accountId")
    suspend fun getLinesForAccount(accountId: Long): List<JournalLineEntity>
}
