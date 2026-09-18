package com.theinheritance.gm.actions

import com.theinheritance.gm.GmAction
import com.theinheritance.gm.Season
import com.theinheritance.gm.Weather
import com.theinheritance.simulation.NpcAgent

sealed interface WorldAction : GmAction {
    data class AdvanceDay(val description: String) : WorldAction
    data class SkipADay(val whatsDifferent: String) : WorldAction
    data class RewindADay(val reason: String) : WorldAction
    data class HoldADay(val reason: String) : WorldAction
    data class ChangeWeather(val weather: Weather) : WorldAction
    data class BringASeason(val season: Season) : WorldAction
    data class EndASeason(val reason: String) : WorldAction
    data class ShiftDemand(val productId: Long, val multiplier: Float) : WorldAction
    data class ShiftSupply(val productId: Long, val multiplier: Float) : WorldAction
    data class IntroduceCompetitor(val competitor: NpcAgent) : WorldAction
    data class CloseCompetitor(val competitorId: Long) : WorldAction
    data class ChangeInterestRates(val newRate: Float) : WorldAction
    data class ChangeTaxLaw(val change: String) : WorldAction
    data class SpawnSubsidy(val description: String, val amountCents: Long) : WorldAction
    data class WithdrawSubsidy(val reason: String) : WorldAction
    data class CauseARecession(val severity: Float) : WorldAction
    data class CauseABoom(val severity: Float) : WorldAction
    data class BreakEquipment(val assetId: Long, val repairCostCents: Long) : WorldAction
    data class FloodTheBasement(val inventoryLostCents: Long) : WorldAction
    data class FireInTheKitchen(val damageCents: Long, val insurancePayoutCents: Long) : WorldAction
    data class VandalizeTheStorefront(val repairCostCents: Long, val reputationHit: Float) : WorldAction
    data class LoseAShipment(val inventoryLostCents: Long) : WorldAction
    data class FindLostGoods(val description: String) : WorldAction
    data class MoveTheStreet(val trafficChange: Float) : WorldAction
    data class SendLetterFromBank(val content: String) : WorldAction
    data class SendLetterFromTaxOffice(val content: String) : WorldAction
    data class SendLetterFromLawyer(val content: String) : WorldAction
    data class CallFromOldFriend(val content: String) : WorldAction
    data class KnockAtTheDoor(val visitorName: String, val reason: String) : WorldAction
    data class PhoneRingsAt3am(val content: String) : WorldAction
}
