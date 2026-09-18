package com.theinheritance.gm

import kotlinx.serialization.Serializable

/** Persisted snapshot of one GM turn (log / NG+ recall). */
@Serializable
data class GmTurn(val day: Int, val narrative: String, val entryIds: List<Long> = emptyList())

/** Free-text input from the player each turn. */
data class PlayerAction(val rawText: String, val kind: String = "free_text")

sealed class GmTurnResult {
    data class Narrative(val text: String, val executed: List<GmAction>) : GmTurnResult()
}
