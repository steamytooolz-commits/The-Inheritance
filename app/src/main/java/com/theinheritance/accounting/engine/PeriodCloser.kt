package com.theinheritance.accounting.engine

import java.time.LocalDate

class PeriodCloser(private val engine: AccountingEngine) {
    fun closeMonth(year: Int, month: Int): Result<Unit> {
        val end = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1)
        val tb = engine.trialBalance(end)
        if (!tb.isBalanced) {
            return Result.failure(IllegalStateException("Cannot close unbalanced period: ${tb.totalDebits.cents} vs ${tb.totalCredits.cents}"))
        }
        engine.closePeriod(year, month)
        return Result.success(Unit)
    }
}
