package com.theinheritance.accounting.model

import java.time.LocalDate

data class IncomeStatementLine(val label: String, val amountCents: Long, val indent: Int = 0, val isTotal: Boolean = false)

data class IncomeStatement(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val revenueCents: Long,
    val expenseCents: Long,
    val cogsCents: Long,
    val lines: List<IncomeStatementLine>
) {
    val netIncomeCents: Long get() = revenueCents - expenseCents - cogsCents
}
