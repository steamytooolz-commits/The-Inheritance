package com.theinheritance.simulation

class DailyTurnResolver(private val sim: EconomicSimulator = EconomicSimulator()) {
    fun resolve(state: BusinessState, events: List<MarketEvent>): BusinessState =
        sim.nextDay(state, events)
}
