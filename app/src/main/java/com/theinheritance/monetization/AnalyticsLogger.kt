package com.theinheritance.monetization

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsLogger @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val EVENT_RUN_STARTED = "run_started"
        const val EVENT_DAY_ADVANCED = "day_advanced"
        const val EVENT_FRAUD_FOUND = "fraud_found"
        const val EVENT_RUN_ENDED = "run_ended"
        const val EVENT_PRO_UNLOCKED = "pro_unlocked"
    }

    private fun log(name: String, block: Bundle.() -> Unit = {}) {
        try {
            FirebaseAnalytics.getInstance(context).logEvent(name, Bundle().apply(block))
        } catch (_: Throwable) {
            // Analytics is optional; never crash the game for telemetry.
        }
    }

    fun runStarted(seed: Long) = log(EVENT_RUN_STARTED) { putLong("seed", seed) }
    fun dayAdvanced(day: Int) = log(EVENT_DAY_ADVANCED) { putInt("day", day) }
    fun fraudFound(scheme: String) = log(EVENT_FRAUD_FOUND) { putString("scheme", scheme) }
    fun runEnded(endingId: String, days: Int) = log(EVENT_RUN_ENDED) {
        putString("ending", endingId)
        putInt("days", days)
    }
    fun proUnlocked() = log(EVENT_PRO_UNLOCKED)
}
