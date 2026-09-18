package com.theinheritance.data.llm

data class ToolCall(
    val name: String,
    val parameters: Map<String, String> = emptyMap()
)

data class LlmResponse(
    val text: String,
    val toolCalls: List<ToolCall> = emptyList()
)

interface LlmEngine {
    suspend fun initialize(modelPath: String): Result<Unit>
    suspend fun generate(systemPrompt: String, userPrompt: String): String
    suspend fun generateStructured(systemPrompt: String, userPrompt: String): LlmResponse {
        val text = generate(systemPrompt, userPrompt)
        val toolCalls = parseToolCalls(text)
        return LlmResponse(text = text, toolCalls = toolCalls)
    }
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

fun parseToolCalls(text: String): List<ToolCall> {
    val calls = mutableListOf<ToolCall>()
    // Match JSON blocks e.g. {"action": "PostTransaction", "debitAccountId": "1000", ...}
    val jsonRegex = Regex("""\{\s*"action"\s*:\s*"([^"]+)"([^}]+)\}""", RegexOption.DOT_MATCHES_ALL)
    for (match in jsonRegex.findAll(text)) {
        val actionName = match.groupValues[1]
        val rest = match.groupValues[2]
        val paramRegex = Regex("""\s*"([^"]+)"\s*:\s*(?:"([^"]*)"|(\d+)|(true|false))""")
        val params = mutableMapOf<String, String>()
        for (pMatch in paramRegex.findAll(rest)) {
            val key = pMatch.groupValues[1]
            val value = pMatch.groupValues[2].ifEmpty { pMatch.groupValues[3].ifEmpty { pMatch.groupValues[4] } }
            params[key] = value
        }
        calls.add(ToolCall(name = actionName, parameters = params))
    }
    // Match [[ACTION: Name(param1=val1, param2=val2)]]
    val inlineRegex = Regex("""\[\[ACTION:\s*([A-Za-z0-9_.]+)\((.*?)\)\]\]""")
    for (match in inlineRegex.findAll(text)) {
        val actionName = match.groupValues[1]
        val paramsRaw = match.groupValues[2]
        val params = mutableMapOf<String, String>()
        if (paramsRaw.isNotBlank()) {
            paramsRaw.split(",").forEach { pair ->
                val parts = pair.split("=")
                if (parts.size == 2) {
                    params[parts[0].trim()] = parts[1].trim().removeSurrounding("\"")
                }
            }
        }
        calls.add(ToolCall(name = actionName, parameters = params))
    }
    return calls
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
