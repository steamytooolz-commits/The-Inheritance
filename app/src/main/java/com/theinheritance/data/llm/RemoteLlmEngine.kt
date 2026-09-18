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
