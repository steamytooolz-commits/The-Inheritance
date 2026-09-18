package com.theinheritance.gm.actions

import com.theinheritance.gm.Deadline
import com.theinheritance.gm.GmAction

sealed interface PlayerAction_ : GmAction {
    data class RaiseTheTempo(val multiplier: Float) : PlayerAction_
    data class LowerTheTempo(val reason: String) : PlayerAction_
    data class WhisperDeadline(val daysRemaining: Int) : PlayerAction_
    data class TickAClock(val seconds: Int) : PlayerAction_
    data class OpenANewFront(val description: String) : PlayerAction_
    data class StackTheCalendar(val deadlines: List<Deadline>) : PlayerAction_
    data class MakeItPersonal(val npcId: Long, val problem: String) : PlayerAction_
    data class ThreatenTheDog(val what: String) : PlayerAction_
    data class MentionTheChildren(val npcId: Long) : PlayerAction_
    data class GiveASmallWin(val description: String, val amountCents: Long) : PlayerAction_
    data class DropAHint(val hint: String, val targetEntryId: Long) : PlayerAction_
    data class ForgiveAMistake(val penaltyId: Long) : PlayerAction_
    data class ExtendTheDeadline(val days: Int, val costCents: Long) : PlayerAction_
    data class SendAStranger(val npcName: String, val help: String) : PlayerAction_
    data class QuietTheWorld(val description: String) : PlayerAction_
    data class RememberYourBirthday(val gift: String) : PlayerAction_
    data class Flatter(val aboutWhat: String) : PlayerAction_
    data class Gaslight(val falseStatement: String) : PlayerAction_
    data class ChangeTheSubject(val newTopic: String) : PlayerAction_
    data class AnswerADifferentQuestion(val question: String) : PlayerAction_
    data class LaughItOff(val concern: String) : PlayerAction_
    data class GoQuiet(val reason: String) : PlayerAction_
    data class ApologizeTooMuch(val forWhat: String) : PlayerAction_
    data class WeaponizeAMemory(val memoryId: String) : PlayerAction_
}
