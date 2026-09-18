package com.theinheritance.data.llm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteLlmEngine @Inject constructor() : LlmEngine {
    private var endpoint: String? = null
    private var apiKey: String? = null
    private var modelName: String = "gpt-4o-mini"
    
    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun configure(
        baseUrl: String,
        apiKey: String,
        model: String
    ) {
        this.endpoint = baseUrl.ifBlank { null }
        this.apiKey = apiKey.ifBlank { null }
        if (model.isNotBlank()) this.modelName = model
    }

    override suspend fun initialize(modelPath: String): Result<Unit> {
        if (modelPath.isNotBlank() && modelPath.startsWith("http")) {
            endpoint = modelPath
        }
        return Result.success(Unit)
    }

    override suspend fun generate(systemPrompt: String, userPrompt: String): String = withContext(Dispatchers.IO) {
        val currentEndpoint = endpoint
        if (currentEndpoint.isNullOrBlank()) {
            return@withContext "The ledger hums. (Remote endpoint unset — configure in Settings to enable.)"
        }

        try {
            val jsonPayload = JSONObject().apply {
                put("model", modelName)
                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userPrompt)
                    })
                }
                put("messages", messages)
                put("temperature", 0.7)
            }

            val requestBuilder = Request.Builder()
                .url(if (currentEndpoint.endsWith("/")) "${currentEndpoint}chat/completions" else "$currentEndpoint/chat/completions")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))

            apiKey?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }

            val response = client.newCall(requestBuilder.build()).execute()
            val bodyString = response.body?.string() ?: ""

            if (response.isSuccessful && bodyString.isNotBlank()) {
                val jsonRes = JSONObject(bodyString)
                val choices = jsonRes.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val choice = choices.getJSONObject(0)
                    val message = choice.optJSONObject("message")
                    return@withContext message?.optString("content") ?: bodyString
                }
            }
            "The ledger hums. (Response: HTTP ${response.code})"
        } catch (e: Exception) {
            "The ledger hums. (Remote connection error: ${e.localizedMessage ?: "Unknown error"})"
        }
    }

    override suspend fun generateStructured(systemPrompt: String, userPrompt: String): LlmResponse = withContext(Dispatchers.IO) {
        val currentEndpoint = endpoint
        if (currentEndpoint.isNullOrBlank()) {
            return@withContext LlmResponse("The ledger hums. (Remote endpoint unset — configure in Settings to enable.)")
        }

        try {
            val jsonPayload = JSONObject().apply {
                put("model", modelName)
                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userPrompt)
                    })
                }
                put("messages", messages)
                put("temperature", 0.7)
                put("tools", buildOpenAiToolsJson())
                put("tool_choice", "auto")
            }

            val requestBuilder = Request.Builder()
                .url(if (currentEndpoint.endsWith("/")) "${currentEndpoint}chat/completions" else "$currentEndpoint/chat/completions")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))

            apiKey?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }

            val response = client.newCall(requestBuilder.build()).execute()
            val bodyString = response.body?.string() ?: ""

            if (response.isSuccessful && bodyString.isNotBlank()) {
                val jsonRes = JSONObject(bodyString)
                val choices = jsonRes.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val choice = choices.getJSONObject(0)
                    val message = choice.optJSONObject("message")
                    if (message != null) {
                        val text = message.optString("content") ?: ""
                        val toolCallsList = mutableListOf<ToolCall>()

                        val toolCallsJson = message.optJSONArray("tool_calls")
                        if (toolCallsJson != null) {
                            for (i in 0 until toolCallsJson.length()) {
                                val tc = toolCallsJson.getJSONObject(i)
                                val fn = tc.optJSONObject("function")
                                if (fn != null) {
                                    val name = fn.getString("name")
                                    val argsStr = fn.optString("arguments")
                                    val params = mutableMapOf<String, String>()
                                    try {
                                        val argsObj = JSONObject(argsStr)
                                        val argsKeys = argsObj.keys()
                                        while (argsKeys.hasNext()) {
                                            val ak = argsKeys.next()
                                            params[ak] = argsObj.opt(ak)?.toString() ?: ""
                                        }
                                    } catch (_: Exception) {}
                                    toolCallsList.add(ToolCall(name, params))
                                }
                            }
                        }

                        val fallbackCalls = parseToolCalls(text)
                        for (fallback in fallbackCalls) {
                            if (toolCallsList.none { it.name == fallback.name }) {
                                toolCallsList.add(fallback)
                            }
                        }

                        return@withContext LlmResponse(text = text, toolCalls = toolCallsList)
                    }
                }
            }
            LlmResponse("The ledger hums. (Response: HTTP ${response.code})")
        } catch (e: Exception) {
            LlmResponse("The ledger hums. (Remote connection error: ${e.localizedMessage ?: "Unknown error"})")
        }
    }

    private fun buildOpenAiToolsJson(): JSONArray {
        val tools = JSONArray()

        tools.put(JSONObject().apply {
            put("type", "function")
            put("function", JSONObject().apply {
                put("name", "PostTransaction")
                put("description", "Post a real double-entry financial transaction to the bookkeeping ledger.")
                put("parameters", JSONObject().apply {
                    put("type", "object")
                    put("properties", JSONObject().apply {
                        put("debitAccountId", JSONObject().apply { put("type", "string"); put("description", "The asset or expense account code being debited (e.g. 1000, 1010, 5100)") })
                        put("creditAccountId", JSONObject().apply { put("type", "string"); put("description", "The liability, equity, or revenue account code being credited (e.g. 2000, 3000, 4000)") })
                        put("amountCents", JSONObject().apply { put("type", "string"); put("description", "The monetary amount in cents (e.g. 10000 for R100.00)") })
                        put("memo", JSONObject().apply { put("type", "string"); put("description", "A descriptive memo or audit trail note for the transaction") })
                    })
                    put("required", JSONArray().apply { put("debitAccountId"); put("creditAccountId"); put("amountCents"); put("memo") })
                })
            })
        })

        tools.put(JSONObject().apply {
            put("type", "function")
            put("function", JSONObject().apply {
                put("name", "VoidEntry")
                put("description", "Voids an existing journal entry in the ledger by ID.")
                put("parameters", JSONObject().apply {
                    put("type", "object")
                    put("properties", JSONObject().apply {
                        put("entryId", JSONObject().apply { put("type", "string"); put("description", "The ID of the journal entry to void") })
                        put("reason", JSONObject().apply { put("type", "string"); put("description", "The reason for voiding the entry") })
                    })
                    put("required", JSONArray().apply { put("entryId"); put("reason") })
                })
            })
        })

        tools.put(JSONObject().apply {
            put("type", "function")
            put("function", JSONObject().apply {
                put("name", "PlantDiscrepancy")
                put("description", "Plants a dynamic discrepancy or unreconciled item in a specific account.")
                put("parameters", JSONObject().apply {
                    put("type", "object")
                    put("properties", JSONObject().apply {
                        put("accountId", JSONObject().apply { put("type", "string"); put("description", "The account ID where the discrepancy will be planted") })
                        put("amountCents", JSONObject().apply { put("type", "string"); put("description", "The discrepancy amount in cents") })
                        put("memo", JSONObject().apply { put("type", "string"); put("description", "Memo detailing the nature of the discrepancy") })
                    })
                    put("required", JSONArray().apply { put("accountId"); put("amountCents"); put("memo") })
                })
            })
        })

        tools.put(JSONObject().apply {
            put("type", "function")
            put("function", JSONObject().apply {
                put("name", "ShiftTrust")
                put("description", "Changes the trust level of an NPC agent.")
                put("parameters", JSONObject().apply {
                    put("type", "object")
                    put("properties", JSONObject().apply {
                        put("npcId", JSONObject().apply { put("type", "string"); put("description", "The ID of the NPC character") })
                        put("delta", JSONObject().apply { put("type", "string"); put("description", "The shift in trust (e.g. -5, 10)") })
                    })
                    put("required", JSONArray().apply { put("npcId"); put("delta") })
                })
            })
        })

        tools.put(JSONObject().apply {
            put("type", "function")
            put("function", JSONObject().apply {
                put("name", "SpeakInCharacter")
                put("description", "Forces an NPC agent to speak dialogue in-character to the player.")
                put("parameters", JSONObject().apply {
                    put("type", "object")
                    put("properties", JSONObject().apply {
                        put("npcId", JSONObject().apply { put("type", "string"); put("description", "The ID of the NPC character") })
                        put("dialogue", JSONObject().apply { put("type", "string"); put("description", "The dialogue spoken by the character") })
                    })
                    put("required", JSONArray().apply { put("npcId"); put("dialogue") })
                })
            })
        })

        tools.put(JSONObject().apply {
            put("type", "function")
            put("function", JSONObject().apply {
                put("name", "AdvanceDay")
                put("description", "Advances the active timeline/day of the simulation.")
                put("parameters", JSONObject().apply {
                    put("type", "object")
                    put("properties", JSONObject().apply {
                        put("description", JSONObject().apply { put("type", "string"); put("description", "Narrative explanation for advancing the day") })
                    })
                    put("required", JSONArray().apply { put("description") })
                })
            })
        })

        return tools
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

    override fun shutdown() { 
        endpoint = null 
        apiKey = null
    }

    override val isInitialized: Boolean get() = !endpoint.isNullOrBlank()
}
