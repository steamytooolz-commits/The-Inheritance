package com.theinheritance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val memo: String,
    val isPosted: Boolean = false,
    val isVoided: Boolean = false,
    val createdAt: Long,
    val postedBy: String = "system",
    val fraudFlag: String? = null
)
