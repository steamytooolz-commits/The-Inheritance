package com.theinheritance.gm

import com.theinheritance.gm.actions.BookAction
import com.theinheritance.gm.actions.CharacterAction
import com.theinheritance.gm.actions.MetaAction
import com.theinheritance.gm.actions.NarrativeAction
import com.theinheritance.gm.actions.WorldAction
import com.theinheritance.simulation.BusinessState
import javax.inject.Inject
import javax.inject.Singleton

sealed class ActionResult {
    data class Applied(val summary: String) : ActionResult()
    data class Rejected(val reason: String) : ActionResult()
}

@Singleton
class ToolExecutor @Inject constructor() {
    suspend fun execute(action: GmAction, state: BusinessState): ActionResult {
        return when (action) {
            is BookAction.PostTransaction -> {
                if (action.amountCents <= 0) ActionResult.Rejected("Amount must be positive")
                else if (action.debitAccountId == action.creditAccountId) ActionResult.Rejected("Debit == credit")
                else ActionResult.Applied("post ${action.amountCents} DR ${action.debitAccountId} CR ${action.creditAccountId}")
            }
            is BookAction.VoidEntry -> ActionResult.Applied("void ${action.entryId}: ${action.reason}")
            is BookAction.ClosePeriod -> ActionResult.Applied("close ${action.endDate}")
            is BookAction.LockAccount -> ActionResult.Applied("lock ${action.accountId}")
            is BookAction.UnlockAccount -> ActionResult.Applied("unlock ${action.accountId}")
            is BookAction.PlantDiscrepancy -> ActionResult.Applied("planted discrepancy in account ${action.accountId} for ${action.amountCents} cents: ${action.memo}")
            is BookAction.FabricateInvoice -> ActionResult.Applied("fabricated invoice from ${action.vendorName} for ${action.amountCents} cents")

            is CharacterAction.ShiftTrust -> ActionResult.Applied("shifted NPC ${action.npcId} trust by ${action.delta}")
            is CharacterAction.SpeakInCharacter -> ActionResult.Applied("NPC ${action.npcId} says: \"${action.dialogue}\"")
            is CharacterAction.KillCharacter -> ActionResult.Applied("NPC ${action.npcId} deceased (${action.cause})")

            is NarrativeAction.ReadFromTheLedger -> ActionResult.Applied("Ledger note: ${action.content}")
            is NarrativeAction.RevealFullTruth -> ActionResult.Applied("Truth revealed: ${action.truth}")

            is WorldAction.ChangeWeather -> ActionResult.Applied("weather changed to ${action.weather}")
            is WorldAction.AdvanceDay -> ActionResult.Applied("advanced day: ${action.description}")

            is MetaAction.StartTheEpilogue -> ActionResult.Applied("triggered epilogue ${action.endingId}")

            else -> ActionResult.Applied(action.javaClass.simpleName)
        }
    }
}

/** Spec name for the executor injected into [GmOrchestrator]. */
typealias GmActionExecutor = ToolExecutor
