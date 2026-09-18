package com.theinheritance.accounting.engine

import kotlin.math.roundToLong

object DepreciationCalculator {
    fun straightLine(costCents: Long, salvageCents: Long, usefulLifeMonths: Int, monthsElapsed: Int): Long {
        require(usefulLifeMonths > 0)
        val depreciable = (costCents - salvageCents).coerceAtLeast(0)
        val perMonth = depreciable.toDouble() / usefulLifeMonths
        val elapsed = monthsElapsed.coerceIn(0, usefulLifeMonths)
        return (perMonth * elapsed).roundToLong().coerceAtMost(depreciable)
    }
    fun monthlyExpense(costCents: Long, salvageCents: Long, usefulLifeMonths: Int): Long {
        require(usefulLifeMonths > 0)
        return ((costCents - salvageCents).coerceAtLeast(0).toDouble() / usefulLifeMonths).roundToLong()
    }
}
