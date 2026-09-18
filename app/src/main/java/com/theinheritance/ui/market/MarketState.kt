package com.theinheritance.ui.market

import com.theinheritance.simulation.MarketEvent

data class MarketState(
    val events: List<MarketEvent> = emptyList(),
    val seed: Long = 7L
)
