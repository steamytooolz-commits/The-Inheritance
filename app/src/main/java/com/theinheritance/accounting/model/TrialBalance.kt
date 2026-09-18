package com.theinheritance.accounting.model

import com.theinheritance.accounting.money.Money
import java.time.LocalDate

data class TrialBalanceLine(
    val accountId: Long,
    val accountCode: String,
    val accountName: String,
    val debit: Money,
    val credit: Money
)

data class TrialBalance(val asOfDate: LocalDate, val lines: List<TrialBalanceLine>) {
    val totalDebits: Money get() = Money(lines.sumOf { it.debit.cents })
    val totalCredits: Money get() = Money(lines.sumOf { it.credit.cents })
    val isBalanced: Boolean get() = totalDebits == totalCredits
}
