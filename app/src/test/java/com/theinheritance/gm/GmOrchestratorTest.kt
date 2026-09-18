package com.theinheritance.gm

import com.theinheritance.data.llm.LlmEngine
import com.theinheritance.gm.actions.BookAction
import com.theinheritance.simulation.BusinessState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GmOrchestratorTest {
    private class FakeEngine(var ready: Boolean, var reply: String = "The uncle nods.") : LlmEngine {
        override suspend fun initialize(modelPath: String): Result<Unit> {
            ready = true
            return Result.success(Unit)
        }
        override suspend fun generate(systemPrompt: String, userPrompt: String): String = reply
        override fun generateStreaming(systemPrompt: String, userPrompt: String, onToken: (String) -> Unit, onComplete: () -> Unit, onError: (Throwable) -> Unit) {
            onToken(reply); onComplete()
        }
        override fun shutdown() { ready = false }
        override val isInitialized: Boolean get() = ready
    }

    private fun orchestrator(engine: LlmEngine): GmOrchestrator =
        GmOrchestrator(engine, ToolExecutor(), PromptBuilder(), GmMemoryStore())

    @Test fun `offline engine falls back to rule-based voice`() = runBlocking {
        val result = orchestrator(FakeEngine(ready = false))
            .processTurn(PlayerAction("hello"), BusinessState(day = 1))
        val text = (result as GmTurnResult.Narrative).text
        assertTrue(text.contains("prepaid"))
        assertTrue(result.executed.isEmpty())
    }

    @Test fun `initialized engine returns its narrative`() = runBlocking {
        val result = orchestrator(FakeEngine(ready = true, reply = "Kid. Line one.\nLine two."))
            .processTurn(PlayerAction("hello"), BusinessState(day = 5))
        assertEquals("Kid. Line one.", (result as GmTurnResult.Narrative).text)
    }

    @Test fun `action budget defaults to 15`() {
        assertEquals(15, GmActionBudget().maxPerTurn)
    }

    @Test fun `invalid post is rejected and rejection is recorded`() = runBlocking {
        val memory = GmMemoryStore()
        val executor = ToolExecutor()
        val bad = BookAction.PostTransaction(1010, 1010, 0, "bad")
        val result = executor.execute(bad, BusinessState())
        assertTrue(result is ActionResult.Rejected)
        memory.recordRejection(bad, (result as ActionResult.Rejected).reason)
        assertEquals(1, memory.rejections().size)
        assertTrue(memory.all().isEmpty())
    }

    @Test fun `valid post is applied and remembered`() = runBlocking {
        val memory = GmMemoryStore()
        val good = BookAction.PostTransaction(1010, 4000, 10_000, "sale")
        val result = ToolExecutor().execute(good, BusinessState())
        assertTrue(result is ActionResult.Applied)
        memory.record(good, result as ActionResult.Applied)
        assertEquals(1, memory.all().size)
    }
}
