package com.theinheritance.gm

import javax.inject.Inject
import javax.inject.Singleton

data class GmMemory(
    val day: Int,
    val actionType: String,
    val content: String,
    val isRevealed: Boolean = false
)

@Singleton
class GmMemoryStore @Inject constructor() {
    private val items = mutableListOf<GmMemory>()
    private val rejections = mutableListOf<String>()

    fun record(day: Int, action: GmAction, result: ActionResult.Applied) {
        items.add(GmMemory(day, action.javaClass.simpleName, action.toString()))
    }

    fun recordRejection(action: GmAction, reason: String) {
        rejections.add("$action -> $reason")
    }

    fun all(): List<GmMemory> = items.toList()
    fun rejections(): List<String> = rejections.toList()
}
