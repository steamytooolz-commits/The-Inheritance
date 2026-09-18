package com.theinheritance.data.llm

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmBackendResolver @Inject constructor(
    private val mediaPipe: MediaPipeLlmEngine,
    private val remote: RemoteLlmEngine,
    private val ruleBased: RuleBasedEngine
) {
    fun resolve(preferRemote: Boolean, remoteConfigured: Boolean): LlmEngine = when {
        preferRemote && remoteConfigured && remote.isInitialized -> remote
        mediaPipe.isInitialized -> mediaPipe
        else -> ruleBased
    }
}
