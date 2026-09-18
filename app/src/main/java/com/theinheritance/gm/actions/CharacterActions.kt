package com.theinheritance.gm.actions

import com.theinheritance.gm.GmAction
import com.theinheritance.simulation.NpcAgent

sealed interface CharacterAction : GmAction {
    data class IntroduceCharacter(val npc: NpcAgent) : CharacterAction
    data class KillCharacter(val npcId: Long, val cause: String) : CharacterAction
    data class SendCharacterAway(val npcId: Long, val days: Int) : CharacterAction
    data class BringSomeoneBack(val npcId: Long, val changedHow: String) : CharacterAction
    data class RevealHiddenRelationship(val npcIdA: Long, val npcIdB: Long, val relationship: String) : CharacterAction
    data class CreateOffspringCharacter(val npcIdA: Long, val npcIdB: Long) : CharacterAction
    data class ShiftTrust(val npcId: Long, val delta: Int) : CharacterAction
    data class TradeFavor(val npcId: Long, val favor: String, val owed: String) : CharacterAction
    data class CallInFavor(val npcId: Long, val favor: String) : CharacterAction
    data class Betray(val npcId: Long, val betrayal: String) : CharacterAction
    data class Defend(val npcId: Long, val costToSelf: String) : CharacterAction
    data class ConfessToPlayer(val npcId: Long, val confession: String) : CharacterAction
    data class ConfessToOther(val npcId: Long, val toNpcId: Long, val confession: String) : CharacterAction
    data class SpreadRumor(val sourceNpcId: Long, val targetNpcId: Long, val rumor: String) : CharacterAction
    data class ForgeAlliance(val npcIdA: Long, val npcIdB: Long, val terms: String) : CharacterAction
    data class BreakAlliance(val npcIdA: Long, val npcIdB: Long, val reason: String) : CharacterAction
    data class SpeakInCharacter(val npcId: Long, val dialogue: String) : CharacterAction
    data class RefuseToSpeak(val npcId: Long, val reason: String) : CharacterAction
    data class DemandEvidence(val npcId: Long, val whatEvidence: String) : CharacterAction
    data class OfferADeal(val npcId: Long, val terms: String) : CharacterAction
    data class WithdrawADeal(val npcId: Long, val reason: String) : CharacterAction
    data class ChangeTheirMind(val npcId: Long, val from: String, val to: String) : CharacterAction
    data class ShowTheirBooks(val npcId: Long) : CharacterAction
    data class HideTheirBooks(val npcId: Long, val reason: String) : CharacterAction
    data class AskThePlayerAQuestion(val npcId: Long, val question: String) : CharacterAction
    data class LieToThePlayer(val npcId: Long, val lie: String, val truth: String) : CharacterAction
    data class PromoteCharacter(val npcId: Long, val newRole: String) : CharacterAction
    data class DemoteCharacter(val npcId: Long, val newRole: String) : CharacterAction
    data class BankruptCharacter(val npcId: Long) : CharacterAction
    data class MarryCharacters(val npcIdA: Long, val npcIdB: Long) : CharacterAction
    data class SeparateCharacters(val npcIdA: Long, val npcIdB: Long) : CharacterAction
    data class AgeCharacter(val npcId: Long, val years: Int) : CharacterAction
}
