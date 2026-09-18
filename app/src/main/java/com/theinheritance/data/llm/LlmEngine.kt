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

    // 1. Scan for standard JSON objects starting with { and ending with } using balanced braces
    val jsonObjects = findJsonObjects(text)
    for (jsonStr in jsonObjects) {
        try {
            val obj = org.json.JSONObject(jsonStr)
            if (obj.has("action")) {
                val actionName = obj.getString("action")
                val params = mutableMapOf<String, String>()
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    if (key != "action") {
                        params[key] = obj.opt(key)?.toString() ?: ""
                    }
                }
                calls.add(ToolCall(name = actionName, parameters = params))
            } else if (obj.has("tool_calls")) {
                val toolCallsArr = obj.getJSONArray("tool_calls")
                for (i in 0 until toolCallsArr.length()) {
                    val tc = toolCallsArr.getJSONObject(i)
                    val fn = tc.optJSONObject("function")
                    if (fn != null) {
                        val name = fn.getString("name")
                        val argsStr = fn.optString("arguments")
                        val params = mutableMapOf<String, String>()
                        try {
                            val argsObj = org.json.JSONObject(argsStr)
                            val argsKeys = argsObj.keys()
                            while (argsKeys.hasNext()) {
                                val ak = argsKeys.next()
                                params[ak] = argsObj.opt(ak)?.toString() ?: ""
                            }
                        } catch (_: Exception) {
                            // If arguments is not a JSON string, try reading as raw object or fallback
                        }
                        calls.add(ToolCall(name = name, parameters = params))
                    }
                }
            }
        } catch (_: Exception) {}
    }

    // 2. Scan for inline action annotations [[ACTION: Name(param1=val1, param2=val2)]]
    val inlineRegex = Regex("""\[\[ACTION:\s*([A-Za-z0-9_.]+)\((.*?)\)\]\]""")
    for (match in inlineRegex.findAll(text)) {
        val actionName = match.groupValues[1]
        val paramsRaw = match.groupValues[2]
        val params = mutableMapOf<String, String>()
        if (paramsRaw.isNotBlank()) {
            paramsRaw.split(",").forEach { pair ->
                val parts = pair.split("=")
                if (parts.size == 2) {
                    params[parts[0].trim()] = parts[1].trim().removeSurrounding("\"").removeSurrounding("'")
                }
            }
        }
        if (calls.none { it.name == actionName }) {
            calls.add(ToolCall(name = actionName, parameters = params))
        }
    }
    return calls
}

private fun findJsonObjects(text: String): List<String> {
    val results = mutableListOf<String>()
    var openBrackets = 0
    var startIndex = -1
    for (i in text.indices) {
        if (text[i] == '{') {
            if (openBrackets == 0) {
                startIndex = i
            }
            openBrackets++
        } else if (text[i] == '}') {
            if (openBrackets > 0) {
                openBrackets--
                if (openBrackets == 0 && startIndex != -1) {
                    results.add(text.substring(startIndex, i + 1))
                }
            }
        }
    }
    return results
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
