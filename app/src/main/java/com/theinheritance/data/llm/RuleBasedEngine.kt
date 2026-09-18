package com.theinheritance.data.llm

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline fallback: no model, scripted uncle voice. This is the default Hilt
 * binding, so the app works fully offline (constraint 7) before any model
 * download completes. Additive file beyond the spec's eight.
 */
@Singleton
class RuleBasedEngine @Inject constructor() : LlmEngine {
    override suspend fun initialize(modelPath: String): Result<Unit> = Result.success(Unit)

    override suspend fun generate(systemPrompt: String, userPrompt: String): String =
        "The uncle clears his throat from inside the ledger. \"$userPrompt\" — careful, kid. Check the prepaid account first."

    override fun generateStreaming(
        systemPrompt: String,
        userPrompt: String,
        onToken: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        onToken("The uncle clears his throat. ")
        onComplete()
    }

    override fun shutdown() {}

    override val isInitialized: Boolean get() = true
}
