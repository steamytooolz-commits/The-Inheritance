package com.theinheritance.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Zero-asset sound effects (no res files, no permissions). All calls are
 * best-effort: audio must never crash the game.
 */
@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @Volatile private var enabled: Boolean = true

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun click() = beep(ToneGenerator.TONE_PROP_BEEP)
    fun post() = beep(ToneGenerator.TONE_PROP_ACK)
    fun error() = beep(ToneGenerator.TONE_CDMA_ABBR_ALERT)
    fun discovery() = beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD)

    private fun beep(tone: Int) {
        if (!enabled) return
        try {
            @Suppress("DEPRECATION")
            val stream = AudioManager.STREAM_MUSIC
            val generator = ToneGenerator(stream, 60)
            try {
                generator.startTone(tone, 120)
            } finally {
                generator.release()
            }
        } catch (_: Exception) {
            // Silence is acceptable. The uncle approves of silence.
        }
    }
}
