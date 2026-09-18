package com.theinheritance.tutorial

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tutorialStore by preferencesDataStore("tutorial")

@Singleton
class TutorialManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val SEEN_KEY = booleanPreferencesKey("seen")
        val PAGES = listOf(
            "Your uncle died." to "You inherit The Book Nook — and its books, which are lying. You have 30 days.",
            "Read the books." to "Post journals, open the ledger, tap any statement line and the uncle will explain it.",
            "Find the fraud." to "Every run hides a procedurally generated fraud. Follow the prepaid account."
        )
    }

    val hasSeen: Flow<Boolean> = context.tutorialStore.data.map { it[SEEN_KEY] == true }

    suspend fun markSeen() {
        context.tutorialStore.edit { it[SEEN_KEY] = true }
    }
}
