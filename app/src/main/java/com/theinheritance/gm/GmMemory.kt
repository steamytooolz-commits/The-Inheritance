package com.theinheritance.gm

import com.theinheritance.data.local.dao.GmMemoryDao
import com.theinheritance.data.local.entity.GmMemoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class GmMemory(
    val day: Int,
    val actionType: String,
    val content: String,
    val isRevealed: Boolean = false
)

@Singleton
class GmMemoryStore @Inject constructor(
    private val gmMemoryDao: GmMemoryDao
) {
    private val items = mutableListOf<GmMemory>()
    private val rejections = mutableListOf<String>()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun record(day: Int, action: GmAction, result: ActionResult.Applied) {
        val actionName = action.javaClass.simpleName
        val details = result.summary
        items.add(GmMemory(day, actionName, details))
        
        scope.launch {
            try {
                gmMemoryDao.insert(
                    GmMemoryEntity(
                        day = day,
                        actionType = actionName,
                        content = details,
                        isRevealed = false
                    )
                )
            } catch (_: Exception) {}
        }
    }

    fun recordRejection(action: GmAction, reason: String) {
        rejections.add("$action -> $reason")
    }

    fun all(): List<GmMemory> = items.toList()
    fun rejections(): List<String> = rejections.toList()
}
