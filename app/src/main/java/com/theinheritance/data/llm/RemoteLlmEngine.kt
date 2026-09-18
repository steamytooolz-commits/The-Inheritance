package com.theinheritance.data.llm

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteLlmEngine @Inject constructor() : LlmEngine {
    private var endpoint: String? = null

    fun configure(
        baseUrl: String,
        @Suppress("UNUSED_PARAMETER") apiKey: String,
        @Suppress("UNUSED_PARAMETER") model: String
    ) {
        endpoint = baseUrl
    }

    override suspend fun initialize(modelPath: String): Result<Unit> {
        endpoint = modelPath
        return Result.success(Unit)
    }

    override suspend fun generate(systemPrompt: String, userPrompt: String): String {
        // Offline-first stub: real HTTP wired when user provides endpoint + key.
        return "The ledger hums. (Remote endpoint ${endpoint ?: "unset"} — configure in Settings to enable.)"
    }

    override fun generateStreaming(
        systemPrompt: String,
        userPrompt: String,
        onToken: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            onToken("The ledger hums. ")
            onComplete()
        } catch (e: Exception) {
            onError(e)
        }
    }

    override fun shutdown() { endpoint = null }

    override val isInitialized: Boolean get() = endpoint != null
}
