package com.theinheritance.accounting.model

import java.time.LocalDate

data class BalanceSheetLine(val label: String, val amountCents: Long, val indent: Int = 0, val isTotal: Boolean = false)

data class BalanceSheet(
    val asOfDate: LocalDate,
    val assetCents: Long,
    val liabilityCents: Long,
    val equityCents: Long,
    val lines: List<BalanceSheetLine>
) {
    val isBalanced: Boolean get() = assetCents == liabilityCents + equityCents
}
