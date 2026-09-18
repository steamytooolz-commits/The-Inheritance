package com.theinheritance.accounting.model

import java.time.Instant
import java.time.LocalDate

data class JournalEntry(
    val id: Long = 0L,
    val date: LocalDate,
    val memo: String,
    val lines: List<JournalLine>,
    val isPosted: Boolean = false,
    val isVoided: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val postedBy: String = "system",
    val fraudFlag: FraudFlag? = null
) {
    val totalDebit: Long get() = lines.sumOf { it.debitCents }
    val totalCredit: Long get() = lines.sumOf { it.creditCents }
    val isBalanced: Boolean get() = totalDebit == totalCredit && totalDebit > 0
}

enum class FraudFlag { PLANTED, SUSPICIOUS, CLEARED }
