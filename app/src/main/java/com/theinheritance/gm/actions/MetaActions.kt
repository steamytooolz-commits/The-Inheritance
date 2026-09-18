package com.theinheritance.gm.actions

import com.theinheritance.gm.GmAction
import com.theinheritance.gm.Tone

sealed interface MetaAction : GmAction {
    data class ShiftTone(val from: Tone, val to: Tone) : MetaAction
    data class UseTheUnclesVoice(val content: String) : MetaAction
    data class UseTheLedgerVoice(val content: String) : MetaAction
    data class DropAllPersona(val content: String) : MetaAction
    data class BreakIntoSong(val lyrics: String) : MetaAction
    data class Stutter(val content: String) : MetaAction
    data class RevertToVoicemail(val voicemailAssetId: String) : MetaAction
    data class GoSilent(val reason: String) : MetaAction
    data class RememberThis(val memoryId: String, val content: String) : MetaAction
    data class ForgetThis(val memoryId: String) : MetaAction
    data class MisrememberThis(val memoryId: String, val wrongContent: String) : MetaAction
    data class PrioritizeThread(val threadId: String) : MetaAction
    data class DeprioritizeThread(val threadId: String) : MetaAction
    data class OpenALedgerOfItsOwn(val content: String) : MetaAction
    data class ShowItsNotes(val notes: String) : MetaAction
    data class DeleteItsNotes(val reason: String) : MetaAction
    data class WarmToPlayer(val reason: String) : MetaAction
    data class CoolToPlayer(val reason: String) : MetaAction
    data class TestThePlayer(val test: String) : MetaAction
    data class RewardThePlayer(val reward: String, val motive: String) : MetaAction
    data class PunishThePlayer(val punishment: String) : MetaAction
    data class ConfideInPlayer(val secret: String) : MetaAction
    data class LieToPlayer(val lie: String, val truth: String) : MetaAction
    data class ConfessToPlayer(val confession: String) : MetaAction
    data class AskForHelp(val help: String) : MetaAction
    data class IntroduceNewMechanic(val mechanicId: String, val description: String) : MetaAction
    data class EscalateStakes(val how: String) : MetaAction
    data class LowerStakes(val why: String) : MetaAction
    data class RevealHiddenLayer(val layerId: String, val description: String) : MetaAction
    data class EndTheChapter(val chapterId: String) : MetaAction
    data class StartTheEpilogue(val endingId: String) : MetaAction
}
