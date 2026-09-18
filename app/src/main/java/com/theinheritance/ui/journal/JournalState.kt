package com.theinheritance.ui.journal

import com.theinheritance.data.local.entity.JournalEntryEntity

data class JournalState(
    val memo: String = "",
    val debitId: Long = 1010,
    val creditId: Long = 4000,
    val amountRands: String = "",
    val message: String = "",
    val postedEntries: List<JournalEntryEntity> = emptyList()
)
