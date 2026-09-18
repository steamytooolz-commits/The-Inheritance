package com.theinheritance.accounting.money

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

object MoneyFormatter {
    private fun formatter(locale: Locale = Locale("en", "ZA")): NumberFormat =
        NumberFormat.getCurrencyInstance(locale).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }

    fun format(money: Money, locale: Locale = Locale("en", "ZA")): String {
        val rands = BigDecimal(money.cents).divide(BigDecimal(100), 2, RoundingMode.HALF_EVEN)
        return try {
            formatter(locale).format(rands)
        } catch (_: Exception) {
            "R ${rands.toPlainString()}"
        }
    }

    fun formatShort(money: Money): String {
        val abs = kotlin.math.abs(money.cents)
        val sign = if (money.cents < 0) "-" else ""
        fun scaled(divisor: Long, suffix: String): String {
            val v = BigDecimal(abs).divide(BigDecimal(divisor), 1, RoundingMode.HALF_EVEN)
            return "$sign R${v.toPlainString()}$suffix"
        }
        return when {
            abs >= 100_000_000 -> scaled(100_000_000, "M")
            abs >= 100_000 -> scaled(100_000, "k")
            else -> format(money)
        }
    }
}

object MoneyMath {
    fun allocate(totalCents: Long, ratios: List<Int>): List<Long> {
        require(ratios.isNotEmpty())
        val sum = ratios.sum()
        require(sum > 0)
        var remainder = totalCents
        return ratios.mapIndexed { i, r ->
            if (i == ratios.lastIndex) remainder
            else {
                val share = totalCents * r / sum
                remainder -= share
                share
            }
        }
    }
}
