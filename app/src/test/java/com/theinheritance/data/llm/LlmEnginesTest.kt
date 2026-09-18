package com.theinheritance.data.llm

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LlmEnginesTest {
    @Test fun `catalog holds four models with bundled default`() {
        assertEquals(4, AvailableModels.all.size)
        assertTrue(AvailableModels.FunctionGemma.bundled)
        assertTrue(AvailableModels.all.filterNot { it.bundled }.all { it.downloadUrl.isNotBlank() })
    }

    @Test fun `rule-based engine is always ready and speaks`() = runBlocking {
        val engine = RuleBasedEngine()
        assertTrue(engine.isInitialized)
        val text = engine.generate("sys", "hello")
        assertTrue(text.contains("prepaid"))
    }

    @Test fun `remote engine starts offline until initialized`() = runBlocking {
        val engine = RemoteLlmEngine()
        assertFalse(engine.isInitialized)
        engine.initialize("https://example.com/v1")
        assertTrue(engine.isInitialized)
        assertTrue(engine.generate("sys", "hi").contains("ledger"))
        engine.shutdown()
        assertFalse(engine.isInitialized)
    }

    @Test fun `litert and llama engines report unbundled`() = runBlocking {
        assertTrue(LiteRtLlmEngine().initialize("x").isFailure)
        assertTrue(LlamaCppEngine().initialize("x").isFailure)
        assertFalse(LiteRtLlmEngine().isInitialized)
        assertFalse(LlamaCppEngine().isInitialized)
    }
}
