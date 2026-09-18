package com.theinheritance.data.llm

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlamaCppEngine @Inject constructor() : LlmEngine {
    override suspend fun initialize(modelPath: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("llama.cpp JNI optional — not bundled"))

    override suspend fun generate(systemPrompt: String, userPrompt: String): String =
        throw IllegalStateException("llama.cpp not available")

    override fun generateStreaming(
        systemPrompt: String,
        userPrompt: String,
        onToken: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        onError(IllegalStateException("llama.cpp not available"))
    }

    override fun shutdown() {}

    override val isInitialized: Boolean get() = false
}
