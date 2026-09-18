package com.theinheritance.ui.npc

import com.theinheritance.simulation.NpcAgent

data class DialogueExchange(
    val playerPrompt: String,
    val npcResponse: String,
    val trustDelta: Int = 0
)

data class NpcHubState(
    val npcs: List<NpcAgent> = emptyList(),
    val activeNpc: NpcAgent? = null,
    val currentDialogue: DialogueExchange? = null
)
