package com.theinheritance.simulation

class EconomicSimulator {
    fun nextDay(
        state: BusinessState,
        events: List<MarketEvent>,
        baseRevenueCents: Long = 85_00,
        baseCostCents: Long = 62_00
    ): BusinessState {
        val eventDelta = events.filter { it.day == state.day }.sumOf { it.cashDeltaCents }
        val noise = ((state.runSeed + state.day * 7919) % 2000) - 1000
        val cash = state.cashCents + (baseRevenueCents - baseCostCents) + eventDelta + noise
        return state.copy(day = state.day + 1, cashCents = cash, isAlive = cash > -500_00)
    }
}
