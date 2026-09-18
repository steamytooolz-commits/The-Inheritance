package com.theinheritance.gm.actions

import com.theinheritance.gm.GmAction
import java.time.LocalDate

sealed interface NarrativeAction : GmAction {
    data class Foreshadow(val hint: String, val payoffDay: Int) : NarrativeAction
    data class Callback(val foreshadowId: String) : NarrativeAction
    data class Misdirect(val toward: String, val awayFrom: String) : NarrativeAction
    data class RevealPartialTruth(val truth: String, val percentage: Float) : NarrativeAction
    data class RevealFullTruth(val truth: String) : NarrativeAction
    data class WithholdTruth(val what: String, val reason: String) : NarrativeAction
    data class BuryTheLede(val important: String, val buriedIn: String) : NarrativeAction
    data class CutScene(val toWhere: String, val toWhom: String) : NarrativeAction
    data class HoldOnImage(val description: String) : NarrativeAction
    data class Montage(val description: String) : NarrativeAction
    data class SlowMotion(val moment: String) : NarrativeAction
    data class Intercut(val sceneA: String, val sceneB: String) : NarrativeAction
    data class Flashback(val toWhen: String, val content: String) : NarrativeAction
    data class Flashforward(val toWhen: String, val content: String) : NarrativeAction
    data class ReadFromTheLedger(val content: String) : NarrativeAction
    data class ReadFromALetter(val content: String) : NarrativeAction
    data class ReadFromAVoicemail(val content: String) : NarrativeAction
    data class ReadFromAReceipt(val content: String) : NarrativeAction
    data class ReadFromAReview(val content: String) : NarrativeAction
    data class ReadFromTheNews(val content: String) : NarrativeAction
    data class ReadFromSilence(val content: String) : NarrativeAction
}
