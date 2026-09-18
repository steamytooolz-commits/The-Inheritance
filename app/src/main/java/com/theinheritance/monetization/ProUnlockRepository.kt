package com.theinheritance.monetization

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.proStore by preferencesDataStore("pro_unlock")

@Singleton
class ProUnlockRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val PRO_KEY = booleanPreferencesKey("pro_unlocked")
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val isPro: Flow<Boolean> = context.proStore.data.map { it[PRO_KEY] == true }

    fun setProUnlocked(unlocked: Boolean) {
        scope.launch { context.proStore.edit { it[PRO_KEY] = unlocked } }
    }

    /** Synchronous hook for billing callbacks that cannot suspend. */
    fun setProUnlockedSync(unlocked: Boolean) {
        runBlocking(Dispatchers.IO) { context.proStore.edit { it[PRO_KEY] = unlocked } }
    }
}
