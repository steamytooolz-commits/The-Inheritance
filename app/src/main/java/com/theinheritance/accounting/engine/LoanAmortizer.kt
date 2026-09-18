package com.theinheritance.accounting.engine

import kotlin.math.roundToLong

object LoanAmortizer {
    data class Payment(val number: Int, val interestCents: Long, val principalCents: Long, val balanceCents: Long)
    fun schedule(principalCents: Long, annualRate: Double, months: Int): List<Payment> {
        require(months > 0)
        val r = annualRate / 12.0
        val payment = if (r == 0.0) principalCents.toDouble() / months
        else principalCents * r / (1 - Math.pow(1 + r, -months.toDouble()))
        var balance = principalCents.toDouble()
        return (1..months).map { n ->
            val interest = (balance * r).roundToLong()
            val principal = (payment.roundToLong() - interest).coerceAtMost(balance.roundToLong())
            balance -= principal
            Payment(n, interest, principal, balance.roundToLong().coerceAtLeast(0))
        }
    }
}
