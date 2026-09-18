package com.theinheritance.simulation

class RelationshipTracker {
    private val trust = mutableMapOf<Long, Int>()

    fun shift(npcId: Long, delta: Int): Int {
        val v = ((trust[npcId] ?: 50) + delta).coerceIn(0, 100)
        trust[npcId] = v
        return v
    }

    fun get(npcId: Long) = trust[npcId] ?: 50
}
