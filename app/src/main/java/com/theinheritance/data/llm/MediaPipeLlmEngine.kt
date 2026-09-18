package com.theinheritance.data.llm

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPipeLlmEngine @Inject constructor(
    @ApplicationContext private val context: Context
) : LlmEngine {

    private var llmInference: LlmInference? = null

    override suspend fun initialize(modelPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTopK(64)
                .build()

            llmInference = LlmInference.createFromOptions(context, options)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generate(systemPrompt: String, userPrompt: String): String =
        withContext(Dispatchers.Default) {
            val fullPrompt = "$systemPrompt\n\n$userPrompt"
            llmInference?.generateResponse(fullPrompt) ?: ""
        }

    override fun generateStreaming(
        systemPrompt: String,
        userPrompt: String,
        onToken: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val fullPrompt = "$systemPrompt\n\n$userPrompt"
        val engine = llmInference ?: run {
            onError(IllegalStateException("LLM not initialized"))
            return
        }
        try {
            engine.generateResponseAsync(fullPrompt) { partialResult, done ->
                if (done) onComplete() else onToken(partialResult)
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    override fun shutdown() {
        llmInference?.close()
        llmInference = null
    }

    override val isInitialized: Boolean get() = llmInference != null
}
