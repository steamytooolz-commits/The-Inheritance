package com.theinheritance.data.llm

interface LlmEngine {
    suspend fun initialize(modelPath: String): Result<Unit>
    suspend fun generate(systemPrompt: String, userPrompt: String): String
    fun generateStreaming(
        systemPrompt: String,
        userPrompt: String,
        onToken: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    )
    fun shutdown()
    val isInitialized: Boolean
}

data class LlmModel(
    val id: String,
    val label: String,
    val sizeBytes: Long,
    val downloadUrl: String,
    val bundled: Boolean = false
)

object AvailableModels {
    val FunctionGemma = LlmModel("function-gemma-270m", "FunctionGemma 270M (bundled)", 200L * 1024 * 1024, "", true)
    val Gemma3_1B = LlmModel("gemma-3-1b", "Gemma 3 1B", 600L * 1024 * 1024, "https://huggingface.co/litert-community/Gemma3-1B", false)
    val Lfm25 = LlmModel("lfm-2.5", "LFM 2.5 2.6B", 1500L * 1024 * 1024, "https://huggingface.co/", false)
    val Qwen35 = LlmModel("qwen-3.5", "Qwen 3.5 4B", 2200L * 1024 * 1024, "https://huggingface.co/", false)
    val all = listOf(FunctionGemma, Gemma3_1B, Lfm25, Qwen35)
}
