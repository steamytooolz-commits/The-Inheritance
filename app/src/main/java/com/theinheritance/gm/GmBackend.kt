package com.theinheritance.gm

import com.theinheritance.data.llm.LlmModel

sealed class GmBackend {
    data class OnDevice(val model: LlmModel) : GmBackend()
    data class Remote(val baseUrl: String, val apiKey: String, val model: String) : GmBackend()
    object RuleBased : GmBackend() // Fallback: no LLM, scripted responses
}

enum class GmBackendKind { ON_DEVICE, REMOTE, RULE_BASED }
