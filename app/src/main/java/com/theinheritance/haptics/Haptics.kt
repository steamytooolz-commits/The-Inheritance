package com.theinheritance.haptics

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Haptics via the Compose haptic channel: no permissions, no system-service wiring.
 * Every helper is a no-op on devices without haptic hardware.
 */
object Haptics {
    @Composable
    fun click(): () -> Unit {
        val haptics = LocalHapticFeedback.current
        return { haptics.performHapticFeedback(HapticFeedbackType.LongPress) }
    }

    @Composable
    fun tick(): () -> Unit {
        val haptics = LocalHapticFeedback.current
        return { haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
    }
}
