package com.theinheritance.simulation

class MarketEventGenerator(private val seed: Long) {
    private val catalog = listOf(
        "Supplier raised prices" to -120_00L,
        "Tourist weekend rush" to 210_00L,
        "Storm kept foot traffic low" to -90_00L,
        "Local review went viral" to 160_00L,
        "Equipment breakdown" to -140_00L,
        "Quiet day, regulars only" to 20_00L
    )

    fun forDay(day: Int): MarketEvent? {
        val r = kotlin.random.Random(seed + day * 131)
        if (r.nextFloat() > 0.55f) return null
        val (label, delta) = catalog.random(r)
        return MarketEvent(day, label, r.nextFloat(), label, (delta * (0.5 + r.nextFloat())).toLong())
    }
}
