package com.theinheritance.accounting.engine

import com.theinheritance.accounting.model.Account
import com.theinheritance.accounting.model.JournalEntry
import com.theinheritance.accounting.model.JournalLine
import com.theinheritance.accounting.model.Ledger
import com.theinheritance.accounting.model.LedgerEntry
import com.theinheritance.accounting.model.TrialBalance
import com.theinheritance.accounting.model.TrialBalanceLine
import com.theinheritance.accounting.money.Money
import java.time.LocalDate

/**
 * Pure-Kotlin double-entry engine. NO Android imports.
 * Owns posting, ledgers, trial balance and statement generation.
 */
class AccountingEngine(
    accounts: List<Account>,
    private val validator: PostingValidator = PostingValidator(),
    private val closedPeriods: MutableSet<YearMonthKey> = mutableSetOf()
) {
    private val accountMap: MutableMap<Long, Account> = accounts.associateBy { it.id }.toMutableMap()
    private val entries: MutableList<JournalEntry> = mutableListOf()
    private var nextId = 1L

    data class YearMonthKey(val year: Int, val month: Int)

    fun accounts(): Map<Long, Account> = accountMap.toMap()
    fun postedEntries(): List<JournalEntry> = entries.filter { it.isPosted && !it.isVoided }.toList()
    fun allEntries(): List<JournalEntry> = entries.toList()

    fun isPeriodClosed(date: LocalDate): Boolean =
        closedPeriods.contains(YearMonthKey(date.year, date.monthValue))

    fun closePeriod(year: Int, month: Int) { closedPeriods.add(YearMonthKey(year, month)) }
    fun openPeriod(year: Int, month: Int) { closedPeriods.remove(YearMonthKey(year, month)) }

    fun lockAccount(id: Long) { accountMap[id]?.let { accountMap[id] = it.copy(isLocked = true) } }
    fun unlockAccount(id: Long) { accountMap[id]?.let { accountMap[id] = it.copy(isLocked = false) } }

    fun post(entry: JournalEntry): Result<JournalEntry> {
        if (isPeriodClosed(entry.date)) {
            return Result.failure(AccountingException.PeriodClosed(entry.date))
        }
        validator.validate(entry, accountMap).onFailure { return Result.failure(it) }
        val stored = entry.copy(id = nextId++, isPosted = true)
        entries.add(stored)
        return Result.success(stored)
    }

    fun void(entryId: Long): Result<Unit> {
        val idx = entries.indexOfFirst { it.id == entryId }
        if (idx < 0) return Result.failure(IllegalArgumentException("Unknown entry $entryId"))
        entries[idx] = entries[idx].copy(isVoided = true)
        return Result.success(Unit)
    }

    fun backdate(entryId: Long, newDate: LocalDate): Result<JournalEntry> {
        val idx = entries.indexOfFirst { it.id == entryId }
        if (idx < 0) return Result.failure(IllegalArgumentException("Unknown entry $entryId"))
        if (isPeriodClosed(newDate)) return Result.failure(AccountingException.PeriodClosed(newDate))
        val updated = entries[idx].copy(date = newDate)
        entries[idx] = updated
        return Result.success(updated)
    }

    fun balanceFor(accountId: Long, upTo: LocalDate? = null): Money {
        val account = accountMap[accountId] ?: return Money.ZERO
        var debitSum = 0L
        var creditSum = 0L
        for (e in postedEntries()) {
            if (upTo != null && e.date.isAfter(upTo)) continue
            for (l in e.lines.filter { it.accountId == accountId }) {
                debitSum += l.debitCents
                creditSum += l.creditCents
            }
        }
        // Debit-normal accounts: balance = debits - credits, else credits - debits.
        val net = if (account.normalDebit) debitSum - creditSum else creditSum - debitSum
        return Money(net)
    }

    fun ledgerFor(accountId: Long): Ledger? {
        val account = accountMap[accountId] ?: return null
        val sorted = postedEntries().sortedWith(compareBy({ it.date }, { it.id }))
        var running = 0L
        val legs = mutableListOf<LedgerEntry>()
        for (e in sorted) {
            for (l in e.lines.filter { it.accountId == accountId }) {
                val delta = if (account.normalDebit) l.debitCents - l.creditCents else l.creditCents - l.debitCents
                running += delta
                legs.add(
                    LedgerEntry(
                        journalEntryId = e.id, date = e.date, memo = e.memo.ifBlank { l.memo },
                        debit = Money(l.debitCents), credit = Money(l.creditCents), balance = Money(running)
                    )
                )
            }
        }
        return Ledger(accountId, account, legs, Money(running))
    }

    fun trialBalance(asOf: LocalDate): TrialBalance {
        val lines = accountMap.values.sortedBy { it.code }.map { acc ->
            val bal = balanceFor(acc.id, asOf)
            val (d, c) = if (acc.normalDebit) {
                if (bal.cents >= 0) Money(bal.cents) to Money.ZERO else Money.ZERO to Money(-bal.cents)
            } else {
                if (bal.cents >= 0) Money.ZERO to Money(bal.cents) else Money(-bal.cents) to Money.ZERO
            }
            TrialBalanceLine(acc.id, acc.code, acc.name, d, c)
        }.filter { !it.debit.isZero() || !it.credit.isZero() }
        return TrialBalance(asOf, lines)
    }

    fun postSimple(date: LocalDate, memo: String, debitId: Long, creditId: Long, amountCents: Long, by: String = "system"): Result<JournalEntry> {
        require(amountCents > 0) { "Amount must be positive" }
        return post(
            JournalEntry(
                date = date, memo = memo,
                lines = listOf(
                    JournalLine(debitId, debitCents = amountCents),
                    JournalLine(creditId, creditCents = amountCents)
                ),
                postedBy = by
            )
        )
    }
}
