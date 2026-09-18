package com.theinheritance.gm

import com.theinheritance.gm.actions.BookAction
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
            else -> ActionResult.Applied(action.javaClass.simpleName)
        }
    }
}

/** Spec name for the executor injected into [GmOrchestrator]. */
typealias GmActionExecutor = ToolExecutor
