package com.theinheritance.data.llm

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiteRtLlmEngine @Inject constructor() : LlmEngine {
    private var ready = false

    override suspend fun initialize(modelPath: String): Result<Unit> {
        ready = false
        return Result.failure(UnsupportedOperationException("LiteRT path not bundled; use MediaPipe"))
    }

    override suspend fun generate(systemPrompt: String, userPrompt: String): String =
        throw IllegalStateException("LiteRT not initialized")

    override fun generateStreaming(
        systemPrompt: String,
        userPrompt: String,
        onToken: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        onError(IllegalStateException("LiteRT not initialized"))
    }

    override fun shutdown() { ready = false }

    override val isInitialized: Boolean get() = ready
}
