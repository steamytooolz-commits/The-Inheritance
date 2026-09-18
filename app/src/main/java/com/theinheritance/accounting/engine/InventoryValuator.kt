package com.theinheritance.accounting.engine

import kotlin.math.roundToLong

object InventoryValuator {
    fun fifoCogs(layers: List<Pair<Long, Long>>, unitsSold: Long): Long {
        // layers: (units, costPerUnitCents)
        var remaining = unitsSold
        var cogs = 0L
        for ((units, cost) in layers) {
            if (remaining <= 0) break
            val take = minOf(remaining, units)
            cogs += take * cost
            remaining -= take
        }
        return cogs
    }
    fun weightedAverage(units: List<Long>, costsCents: List<Long>): Long {
        require(units.size == costsCents.size && units.isNotEmpty())
        val totalUnits = units.sum()
        if (totalUnits == 0L) return 0L
        val totalCost = units.zip(costsCents).sumOf { (u, c) -> u * c }
        return (totalCost.toDouble() / totalUnits).roundToLong()
    }
}
