package com.theinheritance.gm

import com.theinheritance.simulation.BusinessState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GmActionExecutor @Inject constructor(
    private val toolExecutor: ToolExecutor
) {
    suspend fun execute(action: GmAction, state: BusinessState): ActionResult {
        return toolExecutor.execute(action, state)
    }
}
