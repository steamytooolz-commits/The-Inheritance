package com.theinheritance.accounting.money

/** ALL money is Long cents. NEVER Double. */
@JvmInline
value class Money(val cents: Long) {
    operator fun plus(other: Money) = Money(cents + other.cents)
    operator fun minus(other: Money) = Money(cents - other.cents)
    operator fun times(factor: Int) = Money(cents * factor)
    operator fun times(factor: Long) = Money(cents * factor)
    operator fun div(divisor: Int): Money {
        require(divisor != 0) { "Division by zero" }
        return Money(cents / divisor)
    }
    operator fun unaryMinus() = Money(-cents)
    operator fun compareTo(other: Money): Int = cents.compareTo(other.cents)
    fun isZero() = cents == 0L
    fun isNegative() = cents < 0L
    fun isPositive() = cents > 0L
    fun abs() = Money(kotlin.math.abs(cents))

    companion object {
        val ZERO = Money(0L)
        fun ofRands(rands: Long) = Money(rands * 100)
        fun ofCents(cents: Long) = Money(cents)
    }
}
