package com.theinheritance.simulation

object RunScorer {
    fun score(daysSurvived: Int, netIncomeCents: Long, fraudFound: Boolean): Int {
        var s = daysSurvived * 10
        s += (netIncomeCents / 1000).toInt().coerceIn(-200, 500)
        if (fraudFound) s += 150
        return s
    }
}
