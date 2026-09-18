package com.theinheritance.accounting.model

import com.theinheritance.accounting.money.Money
import java.time.LocalDate

data class LedgerEntry(
    val journalEntryId: Long,
    val date: LocalDate,
    val memo: String,
    val debit: Money,
    val credit: Money,
    val balance: Money
)

data class Ledger(
    val accountId: Long,
    val account: Account,
    val entries: List<LedgerEntry>,
    val runningBalance: Money
)
