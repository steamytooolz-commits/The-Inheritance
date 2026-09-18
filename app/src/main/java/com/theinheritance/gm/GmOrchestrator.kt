package com.theinheritance.gm

import com.theinheritance.data.llm.LlmEngine
import com.theinheritance.data.llm.parseToolCalls
import com.theinheritance.gm.actions.BookAction
import com.theinheritance.gm.actions.CharacterAction
import com.theinheritance.gm.actions.NarrativeAction
import com.theinheritance.gm.actions.WorldAction
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
            val structuredResponse = engine.generateStructured(systemPrompt, userPrompt)
            val responseText = structuredResponse.text

            val actions = parseActions(responseText)
            if (actions.isEmpty()) {
                return GmTurnResult.Narrative(firstLine(responseText), emptyList())
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
            GmTurnResult.Narrative(firstLine(responseText), executedActions)
        } catch (e: Exception) {
            GmTurnResult.Narrative(ruleBasedLine(playerAction, gameState), emptyList())
        }
    }

    private fun parseActions(response: String): List<GmAction> {
        val toolCalls = parseToolCalls(response)
        if (toolCalls.isEmpty()) return emptyList()

        val list = mutableListOf<GmAction>()
        for (call in toolCalls) {
            val name = call.name.substringAfterLast(".")
            val p = call.parameters
            val action: GmAction? = when (name) {
                "PostTransaction" -> BookAction.PostTransaction(
                    debitAccountId = p["debitAccountId"]?.toLongOrNull() ?: 1000L,
                    creditAccountId = p["creditAccountId"]?.toLongOrNull() ?: 4000L,
                    amountCents = p["amountCents"]?.toLongOrNull() ?: 10000L,
                    memo = p["memo"] ?: "GM Entry"
                )
                "VoidEntry" -> BookAction.VoidEntry(
                    entryId = p["entryId"]?.toLongOrNull() ?: 1L,
                    reason = p["reason"] ?: "GM Void"
                )
                "PlantDiscrepancy" -> BookAction.PlantDiscrepancy(
                    accountId = p["accountId"]?.toLongOrNull() ?: 1000L,
                    amountCents = p["amountCents"]?.toLongOrNull() ?: 5000L,
                    memo = p["memo"] ?: "Unreconciled discrepancy"
                )
                "FabricateInvoice" -> BookAction.FabricateInvoice(
                    vendorName = p["vendorName"] ?: "Ghost Supplier",
                    amountCents = p["amountCents"]?.toLongOrNull() ?: 120000L
                )
                "ClosePeriod" -> BookAction.ClosePeriod(endDate = p["endDate"] ?: "2026-09-30")
                "LockAccount" -> BookAction.LockAccount(
                    accountId = p["accountId"]?.toLongOrNull() ?: 1000L,
                    reason = p["reason"] ?: "Audit Lock"
                )
                "UnlockAccount" -> BookAction.UnlockAccount(
                    accountId = p["accountId"]?.toLongOrNull() ?: 1000L,
                    reason = p["reason"] ?: "Audit Release"
                )
                "ShiftTrust" -> CharacterAction.ShiftTrust(
                    npcId = p["npcId"]?.toLongOrNull() ?: 1L,
                    delta = p["delta"]?.toIntOrNull() ?: -1
                )
                "SpeakInCharacter" -> CharacterAction.SpeakInCharacter(
                    npcId = p["npcId"]?.toLongOrNull() ?: 1L,
                    dialogue = p["dialogue"] ?: p["text"] ?: response
                )
                "ReadFromTheLedger" -> NarrativeAction.ReadFromTheLedger(
                    content = p["content"] ?: response
                )
                "ChangeWeather" -> WorldAction.ChangeWeather(weather = Weather.RAIN)
                "AdvanceDay" -> WorldAction.AdvanceDay(description = p["description"] ?: "Day passes")
                else -> null
            }
            if (action != null) {
                list.add(action)
            }
        }
        return list
    }

    private fun firstLine(response: String): String =
        response.lineSequence().firstOrNull()?.take(600).orEmpty().ifBlank { response.take(600) }

    private fun ruleBasedLine(action: PlayerAction, state: BusinessState): String {
        return if (state.day <= 3) "Kid. Day ${state.day} of 30. The books don't add up because I made sure they wouldn't. Start with the prepaid account — nobody prepays that much for nothing."
        else "Day ${state.day}. Cash is thin and the ledger is nervous. '${action.rawText.take(60)}' — cute. Check the margins I left you."
    }
}
