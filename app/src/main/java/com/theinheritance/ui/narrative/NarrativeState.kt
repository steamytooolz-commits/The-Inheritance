package com.theinheritance.ui.narrative

data class ChatMsg(val fromGm: Boolean, val text: String)

data class NarrativeState(
    val messages: List<ChatMsg> = listOf(
        ChatMsg(true, "Kid. I'm dead, you're broke, and the ledger remembers everything. What do you want to know first?")
    ),
    val busy: Boolean = false
)
