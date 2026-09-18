package com.theinheritance.accounting.engine

import com.theinheritance.accounting.model.Account
import com.theinheritance.accounting.model.JournalEntry
import com.theinheritance.accounting.money.Money
import java.time.LocalDate

sealed class AccountingException(message: String) : Exception(message) {
    class Unbalanced(val debit: Money, val credit: Money) :
        AccountingException("Entry unbalanced: debit=${debit.cents} credit=${credit.cents}")
    object EmptyEntry : AccountingException("Entry has no lines")
    class InvalidLine(val reason: String) : AccountingException(reason)
    class AccountLocked(val accountId: Long) : AccountingException("Account $accountId is locked")
    class PeriodClosed(val date: LocalDate) : AccountingException("Period containing $date is closed")
}

class PostingValidator {
    fun validate(entry: JournalEntry, accounts: Map<Long, Account>): Result<Unit> {
        if (entry.lines.isEmpty()) return Result.failure(AccountingException.EmptyEntry)
        for (line in entry.lines) {
            val account = accounts[line.accountId]
                ?: return Result.failure(AccountingException.InvalidLine("Unknown account ${line.accountId}"))
            if (account.isLocked) return Result.failure(AccountingException.AccountLocked(account.id))
            if (line.debitCents == 0L && line.creditCents == 0L) {
                return Result.failure(AccountingException.InvalidLine("Zero-amount line for ${line.accountId}"))
            }
        }
        val totalDebit = entry.lines.sumOf { it.debitCents }
        val totalCredit = entry.lines.sumOf { it.creditCents }
        if (totalDebit != totalCredit) {
            return Result.failure(AccountingException.Unbalanced(Money(totalDebit), Money(totalCredit)))
        }
        if (totalDebit == 0L) return Result.failure(AccountingException.EmptyEntry)
        return Result.success(Unit)
    }
}
