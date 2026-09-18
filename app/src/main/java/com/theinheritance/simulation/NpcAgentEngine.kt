package com.theinheritance.simulation

class NpcAgentEngine {
    fun reactToTrust(npc: NpcAgent, playerWasKind: Boolean): NpcAgent {
        val delta = if (playerWasKind) 4 else -5
        return npc.copy(trustLevel = (npc.trustLevel + delta).coerceIn(0, 100))
    }
}
