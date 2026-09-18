package com.theinheritance.gm

import com.theinheritance.simulation.BusinessState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameMaster @Inject constructor(private val orchestrator: GmOrchestrator) {
    suspend fun turn(input: String, state: BusinessState): GmTurnResult =
        orchestrator.processTurn(PlayerAction(input), state)
}
