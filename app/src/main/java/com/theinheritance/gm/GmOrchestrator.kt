package com.theinheritance.gm

import com.theinheritance.data.llm.LlmEngine
import com.theinheritance.simulation.BusinessState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GmOrchestrator @Inject constructor(
    private val engine: LlmEngine,
    private val actionExecutor: GmActionExecutor,
    private val promptBuilder: PromptBuilder,
    private val memory: GmMemoryStore
) {
    private var actionBudget = GmActionBudget(maxPerTurn = 15)

    suspend fun processTurn(
        playerAction: PlayerAction,
        gameState: GameState
    ): GmTurnResult {
        val systemPrompt = promptBuilder.buildSystemPrompt(gameState, memory.all())
        val userPrompt = promptBuilder.buildTurnPrompt(playerAction, gameState)

        if (!engine.isInitialized) {
            return GmTurnResult.Narrative(ruleBasedLine(playerAction, gameState), emptyList())
        }

        return try {
            val response = engine.generate(systemPrompt, userPrompt)

            val actions = parseActions(response)
            if (actions.isEmpty()) {
                return GmTurnResult.Narrative(firstLine(response), emptyList())
            }

            val executedActions = mutableListOf<GmAction>()
            var steps = 0
            for (action in actions) {
                if (steps >= actionBudget.maxPerTurn) break
                when (val result = actionExecutor.execute(action, gameState)) {
                    is ActionResult.Rejected -> {
                        // Engine rejected the action — feed back to GM
                        memory.recordRejection(action, result.reason)
                    }
                    is ActionResult.Applied -> {
                        executedActions.add(action)
                        memory.record(action, result)
                    }
                }
                steps++
            }
            GmTurnResult.Narrative(firstLine(response), executedActions)
        } catch (e: Exception) {
            GmTurnResult.Narrative(ruleBasedLine(playerAction, gameState), emptyList())
        }
    }

    private fun parseActions(response: String): List<GmAction> = emptyList()

    private fun firstLine(response: String): String =
        response.lineSequence().firstOrNull()?.take(600).orEmpty().ifBlank { response.take(600) }

    private fun ruleBasedLine(action: PlayerAction, state: BusinessState): String {
        return if (state.day <= 3) "Kid. Day ${state.day} of 30. The books don't add up because I made sure they wouldn't. Start with the prepaid account — nobody prepays that much for nothing."
        else "Day ${state.day}. Cash is thin and the ledger is nervous. '${action.rawText.take(60)}' — cute. Check the margins I left you."
    }
}
