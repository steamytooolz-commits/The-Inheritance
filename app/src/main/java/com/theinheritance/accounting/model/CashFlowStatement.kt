package com.theinheritance.accounting.model

import java.time.LocalDate

data class CashFlowStatement(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val operatingCents: Long,
    val investingCents: Long,
    val financingCents: Long
) {
    val netChangeCents: Long get() = operatingCents + investingCents + financingCents
}
