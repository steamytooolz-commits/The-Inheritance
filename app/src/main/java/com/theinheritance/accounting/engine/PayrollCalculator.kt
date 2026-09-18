package com.theinheritance.accounting.engine

import kotlin.math.roundToLong

object PayrollCalculator {
    data class PayStub(val grossCents: Long, val taxCents: Long, val netCents: Long)
    fun stub(grossCents: Long, taxRate: Double = 0.25): PayStub {
        val tax = (grossCents * taxRate).roundToLong()
        return PayStub(grossCents, tax, grossCents - tax)
    }
}
