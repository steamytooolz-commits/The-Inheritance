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
                        memory.record(gameState.day, action, result)
                    }
                }
                steps++
            }
            GmTurnResult.Narrative(firstLine(responseText), executedActions)
        } catch (e: Exception) {
            android.util.Log.e("GmOrchestrator", "Error processing Game Master turn", e)
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
            val action: GmAction = when (name) {
                "PostTransaction" -> {
                    val dr = p["debitAccountId"]?.toLongOrNull()
                    val cr = p["creditAccountId"]?.toLongOrNull()
                    val amt = p["amountCents"]?.toLongOrNull()
                    val memo = p["memo"]
                    if (dr == null || cr == null || amt == null || memo == null) {
                        MalformedAction("PostTransaction", "Missing/invalid params (debit=$dr, credit=$cr, amt=$amt, memo=$memo)")
                    } else {
                        BookAction.PostTransaction(dr, cr, amt, memo)
                    }
                }
                "VoidEntry" -> {
                    val entryId = p["entryId"]?.toLongOrNull()
                    val reason = p["reason"]
                    if (entryId == null || reason == null) {
                        MalformedAction("VoidEntry", "Missing/invalid params (entryId=$entryId, reason=$reason)")
                    } else {
                        BookAction.VoidEntry(entryId, reason)
                    }
                }
                "PlantDiscrepancy" -> {
                    val acc = p["accountId"]?.toLongOrNull()
                    val amt = p["amountCents"]?.toLongOrNull()
                    val memo = p["memo"]
                    if (acc == null || amt == null || memo == null) {
                        MalformedAction("PlantDiscrepancy", "Missing/invalid params (acc=$acc, amt=$amt, memo=$memo)")
                    } else {
                        BookAction.PlantDiscrepancy(acc, amt, memo)
                    }
                }
                "FabricateInvoice" -> {
                    val vendor = p["vendorName"]
                    val amt = p["amountCents"]?.toLongOrNull()
                    if (vendor == null || amt == null) {
                        MalformedAction("FabricateInvoice", "Missing/invalid params (vendor=$vendor, amt=$amt)")
                    } else {
                        BookAction.FabricateInvoice(vendor, amt)
                    }
                }
                "ClosePeriod" -> {
                    val date = p["endDate"]
                    if (date == null) {
                        MalformedAction("ClosePeriod", "Missing endDate")
                    } else {
                        BookAction.ClosePeriod(date)
                    }
                }
                "LockAccount" -> {
                    val acc = p["accountId"]?.toLongOrNull()
                    val reason = p["reason"]
                    if (acc == null || reason == null) {
                        MalformedAction("LockAccount", "Missing/invalid params (acc=$acc, reason=$reason)")
                    } else {
                        BookAction.LockAccount(acc, reason)
                    }
                }
                "UnlockAccount" -> {
                    val acc = p["accountId"]?.toLongOrNull()
                    val reason = p["reason"]
                    if (acc == null || reason == null) {
                        MalformedAction("UnlockAccount", "Missing/invalid params (acc=$acc, reason=$reason)")
                    } else {
                        BookAction.UnlockAccount(acc, reason)
                    }
                }
                "ShiftTrust" -> {
                    val npc = p["npcId"]?.toLongOrNull()
                    val delta = p["delta"]?.toIntOrNull()
                    if (npc == null || delta == null) {
                        MalformedAction("ShiftTrust", "Missing/invalid params (npc=$npc, delta=$delta)")
                    } else {
                        CharacterAction.ShiftTrust(npc, delta)
                    }
                }
                "SpeakInCharacter" -> {
                    val npc = p["npcId"]?.toLongOrNull()
                    val dial = p["dialogue"] ?: p["text"] ?: response
                    if (npc == null) {
                        MalformedAction("SpeakInCharacter", "Missing npcId")
                    } else {
                        CharacterAction.SpeakInCharacter(npc, dial)
                    }
                }
                "ReadFromTheLedger" -> {
                    val content = p["content"] ?: response
                    NarrativeAction.ReadFromTheLedger(content)
                }
                "ChangeWeather" -> {
                    val wStr = p["weather"]
                    val weather = try { Weather.valueOf(wStr?.uppercase() ?: "RAIN") } catch (_: Exception) { Weather.RAIN }
                    WorldAction.ChangeWeather(weather)
                }
                "AdvanceDay" -> {
                    val desc = p["description"]
                    if (desc == null) {
                        MalformedAction("AdvanceDay", "Missing description")
                    } else {
                        WorldAction.AdvanceDay(desc)
                    }
                }
                else -> instantiateActionReflectively(name, p) ?: MalformedAction(name, "Unsupported GM action surface")
            }
            list.add(action)
        }
        return list
    }

    private fun findActionClass(name: String): Class<*>? {
        val packages = listOf(
            "com.theinheritance.gm.actions.BookAction",
            "com.theinheritance.gm.actions.CharacterAction",
            "com.theinheritance.gm.actions.MetaAction",
            "com.theinheritance.gm.actions.NarrativeAction",
            "com.theinheritance.gm.actions.WorldAction",
            "com.theinheritance.gm.actions.PlayerAction_"
        )
        for (pkg in packages) {
            try {
                return Class.forName("$pkg\$$name")
            } catch (_: ClassNotFoundException) {}
        }
        return null
    }

    private fun instantiateActionReflectively(name: String, p: Map<String, String>): GmAction? {
        val clazz = findActionClass(name) ?: return null
        try {
            val constructor = clazz.declaredConstructors.firstOrNull() ?: return MalformedAction(name, "No constructor found")
            val fields = clazz.declaredFields.filter { !it.isSynthetic && !java.lang.reflect.Modifier.isStatic(it.modifiers) }
            val params = constructor.parameters
            val args = arrayOfNulls<Any>(params.size)
            
            for (i in params.indices) {
                val param = params[i]
                val fieldName = if (i < fields.size) fields[i].name else param.name
                val rawValue = p[fieldName] ?: p[fieldName.lowercase()] ?: p[param.name]
                
                val type = param.type
                if (rawValue == null) {
                    if (type == String::class.java) {
                        args[i] = ""
                    } else if (type == List::class.java) {
                        args[i] = emptyList<Any>()
                    } else {
                        return MalformedAction(name, "Missing parameter '$fieldName'")
                    }
                    continue
                }
                
                args[i] = when (type) {
                    String::class.java -> rawValue
                    Long::class.java, java.lang.Long::class.java -> {
                        rawValue.toLongOrNull() ?: return MalformedAction(name, "Invalid Long for '$fieldName': $rawValue")
                    }
                    Int::class.java, java.lang.Integer::class.java -> {
                        rawValue.toIntOrNull() ?: return MalformedAction(name, "Invalid Int for '$fieldName': $rawValue")
                    }
                    Float::class.java, java.lang.Float::class.java -> {
                        rawValue.toFloatOrNull() ?: return MalformedAction(name, "Invalid Float for '$fieldName': $rawValue")
                    }
                    Double::class.java, java.lang.Double::class.java -> {
                        rawValue.toDoubleOrNull() ?: return MalformedAction(name, "Invalid Double for '$fieldName': $rawValue")
                    }
                    Boolean::class.java, java.lang.Boolean::class.java -> {
                        rawValue.toBoolean()
                    }
                    Tone::class.java -> {
                        try { Tone.valueOf(rawValue.uppercase()) } catch (_: Exception) { Tone.WARM }
                    }
                    Weather::class.java -> {
                        try { Weather.valueOf(rawValue.uppercase()) } catch (_: Exception) { Weather.RAIN }
                    }
                    Season::class.java -> {
                        try { Season.valueOf(rawValue.uppercase()) } catch (_: Exception) { Season.SPRING }
                    }
                    com.theinheritance.simulation.NpcAgent::class.java -> {
                        val npcId = p["npcId"]?.toLongOrNull() ?: p["id"]?.toLongOrNull() ?: 1L
                        val npcName = p["npcName"] ?: p["name"] ?: "Unknown NPC"
                        val npcRole = p["role"] ?: "Trader"
                        val npcPersonality = p["personality"] ?: "Mysterious"
                        com.theinheritance.simulation.NpcAgent(npcId, npcName, npcRole, npcPersonality)
                    }
                    else -> return MalformedAction(name, "Unsupported type ${type.simpleName} for '$fieldName'")
                }
            }
            constructor.isAccessible = true
            return constructor.newInstance(*args) as GmAction
        } catch (e: Exception) {
            return MalformedAction(name, "Failed to instantiate action: ${e.message}")
        }
    }

    private fun firstLine(response: String): String =
        response.lineSequence().firstOrNull()?.take(600).orEmpty().ifBlank { response.take(600) }

    private fun ruleBasedLine(action: PlayerAction, state: BusinessState): String {
        return if (state.day <= 3) "Kid. Day ${state.day} of 30. The books don't add up because I made sure they wouldn't. Start with the prepaid account — nobody prepays that much for nothing."
        else "Day ${state.day}. Cash is thin and the ledger is nervous. '${action.rawText.take(60)}' — cute. Check the margins I left you."
    }
}
