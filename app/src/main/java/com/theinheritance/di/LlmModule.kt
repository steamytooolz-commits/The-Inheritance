package com.theinheritance.di

import com.theinheritance.data.llm.LlmEngine
import com.theinheritance.data.llm.RuleBasedEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LlmModule {
    @Provides @Singleton
    fun provideEngine(ruleBased: RuleBasedEngine): LlmEngine = ruleBased
}
