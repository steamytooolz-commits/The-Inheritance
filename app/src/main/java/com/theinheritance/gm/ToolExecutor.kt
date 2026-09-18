package com.theinheritance.gm

import com.theinheritance.data.local.dao.AccountDao
import com.theinheritance.data.local.dao.GameStateDao
import com.theinheritance.data.local.dao.JournalDao
import com.theinheritance.data.local.dao.NpcDao
import com.theinheritance.data.local.entity.JournalEntryEntity
import com.theinheritance.data.local.entity.JournalLineEntity
import com.theinheritance.gm.actions.BookAction
import com.theinheritance.gm.actions.CharacterAction
import com.theinheritance.gm.actions.MetaAction
import com.theinheritance.gm.actions.NarrativeAction
import com.theinheritance.gm.actions.WorldAction
import com.theinheritance.simulation.BusinessState
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

sealed class ActionResult {
    data class Applied(val summary: String) : ActionResult()
    data class Rejected(val reason: String) : ActionResult()
}

@Singleton
class ToolExecutor @Inject constructor(
    private val journalDao: JournalDao,
    private val accountDao: AccountDao,
    private val npcDao: NpcDao,
    private val gameStateDao: GameStateDao
) {
    suspend fun execute(action: GmAction, state: BusinessState): ActionResult {
        return when (action) {
            is BookAction.PostTransaction -> {
                if (action.amountCents <= 0) {
                    ActionResult.Rejected("Amount must be positive")
                } else if (action.debitAccountId == action.creditAccountId) {
                    ActionResult.Rejected("Debit == credit")
                } else {
                    val entity = JournalEntryEntity(
                        date = LocalDate.now().toString(),
                        memo = action.memo,
                        isPosted = true,
                        createdAt = System.currentTimeMillis(),
                        postedBy = "Game Master"
                    )
                    val lines = listOf(
                        JournalLineEntity(journalEntryId = 0, accountId = action.debitAccountId, debitCents = action.amountCents),
                        JournalLineEntity(journalEntryId = 0, accountId = action.creditAccountId, creditCents = action.amountCents)
                    )
                    journalDao.insertWithLines(entity, lines)

                    // Adjust live game state cash
                    val stateEntity = gameStateDao.get()
                    if (stateEntity != null) {
                        var cash = stateEntity.cashCents
                        if (action.debitAccountId == 1000L || action.debitAccountId == 1010L) {
                            cash += action.amountCents
                        }
                        if (action.creditAccountId == 1000L || action.creditAccountId == 1010L) {
                            cash -= action.amountCents
                        }
                        gameStateDao.upsert(stateEntity.copy(cashCents = cash))
                    }

                    ActionResult.Applied("Posted transaction of R${String.format("%.2f", action.amountCents / 100.0)} (DR ${action.debitAccountId}, CR ${action.creditAccountId}): ${action.memo}")
                }
            }

            is BookAction.VoidEntry -> {
                val entry = journalDao.getById(action.entryId)
                if (entry == null) {
                    ActionResult.Rejected("Journal entry with ID ${action.entryId} not found")
                } else if (entry.isVoided) {
                    ActionResult.Rejected("Journal entry with ID ${action.entryId} is already voided")
                } else {
                    journalDao.voidEntry(action.entryId)
                    val lines = journalDao.getLinesForEntry(action.entryId)
                    val stateEntity = gameStateDao.get()
                    if (stateEntity != null) {
                        var cash = stateEntity.cashCents
                        for (line in lines) {
                            if (line.accountId == 1000L || line.accountId == 1010L) {
                                cash -= line.debitCents
                                cash += line.creditCents
                            }
                        }
                        gameStateDao.upsert(stateEntity.copy(cashCents = cash))
                    }
                    ActionResult.Applied("Voided transaction ID ${action.entryId}: ${action.reason}")
                }
            }

            is BookAction.PlantDiscrepancy -> {
                val entity = JournalEntryEntity(
                    date = LocalDate.now().toString(),
                    memo = action.memo,
                    isPosted = true,
                    createdAt = System.currentTimeMillis(),
                    postedBy = "Game Master (Discrepancy)"
                )
                val lines = listOf(
                    JournalLineEntity(journalEntryId = 0, accountId = action.accountId, debitCents = action.amountCents),
                    JournalLineEntity(journalEntryId = 0, accountId = 9999L, creditCents = action.amountCents)
                )
                journalDao.insertWithLines(entity, lines)

                if (action.accountId == 1000L || action.accountId == 1010L) {
                    val stateEntity = gameStateDao.get()
                    if (stateEntity != null) {
                        gameStateDao.upsert(stateEntity.copy(cashCents = stateEntity.cashCents + action.amountCents))
                    }
                }
                ActionResult.Applied("Planted discrepancy of R${String.format("%.2f", action.amountCents / 100.0)} in account ${action.accountId}: ${action.memo}")
            }

            is BookAction.FabricateInvoice -> {
                val entity = JournalEntryEntity(
                    date = LocalDate.now().toString(),
                    memo = "Fabricated Invoice: ${action.vendorName}",
                    isPosted = true,
                    createdAt = System.currentTimeMillis(),
                    postedBy = "Game Master"
                )
                val lines = listOf(
                    JournalLineEntity(journalEntryId = 0, accountId = 1200L, debitCents = action.amountCents),
                    JournalLineEntity(journalEntryId = 0, accountId = 2000L, creditCents = action.amountCents)
                )
                journalDao.insertWithLines(entity, lines)
                ActionResult.Applied("Fabricated invoice from ${action.vendorName} for R${String.format("%.2f", action.amountCents / 100.0)}")
            }

            is BookAction.LockAccount -> {
                accountDao.setLocked(action.accountId, true)
                ActionResult.Applied("Locked account ${action.accountId}: ${action.reason}")
            }

            is BookAction.UnlockAccount -> {
                accountDao.setLocked(action.accountId, false)
                ActionResult.Applied("Unlocked account ${action.accountId}: ${action.reason}")
            }

            is CharacterAction.ShiftTrust -> {
                val npc = npcDao.getById(action.npcId)
                if (npc != null) {
                    val newTrust = (npc.trustLevel + action.delta).coerceIn(0, 100)
                    npcDao.setTrust(action.npcId, newTrust)
                    ActionResult.Applied("Shifted NPC ${action.npcId} (${npc.name}) trust by ${action.delta} to $newTrust%")
                } else {
                    ActionResult.Rejected("NPC ${action.npcId} not found")
                }
            }

            is CharacterAction.SpeakInCharacter -> {
                ActionResult.Applied("NPC ${action.npcId} says: \"${action.dialogue}\"")
            }

            is CharacterAction.KillCharacter -> {
                val npc = npcDao.getById(action.npcId)
                if (npc != null) {
                    npcDao.upsert(npc.copy(isAlive = false))
                    ActionResult.Applied("NPC ${action.npcId} (${npc.name}) has been deceased due to: ${action.cause}")
                } else {
                    ActionResult.Rejected("NPC ${action.npcId} not found")
                }
            }

            is CharacterAction.ShowTheirBooks -> {
                val npc = npcDao.getById(action.npcId)
                if (npc != null) {
                    npcDao.upsert(npc.copy(hasBooksOpen = true))
                    ActionResult.Applied("NPC ${action.npcId} (${npc.name}) showed their financial books")
                } else {
                    ActionResult.Rejected("NPC ${action.npcId} not found")
                }
            }

            is CharacterAction.HideTheirBooks -> {
                val npc = npcDao.getById(action.npcId)
                if (npc != null) {
                    npcDao.upsert(npc.copy(hasBooksOpen = false))
                    ActionResult.Applied("NPC ${action.npcId} (${npc.name}) hid their financial books: ${action.reason}")
                } else {
                    ActionResult.Rejected("NPC ${action.npcId} not found")
                }
            }

            is NarrativeAction.ReadFromTheLedger -> {
                ActionResult.Applied("Ledger note: ${action.content}")
            }

            is WorldAction.ChangeWeather -> {
                ActionResult.Applied("Weather changed to ${action.weather}")
            }

            is WorldAction.AdvanceDay -> {
                val stateEntity = gameStateDao.get()
                if (stateEntity != null) {
                    val nextDay = (stateEntity.currentDay + 1).coerceAtMost(stateEntity.maxDays)
                    gameStateDao.setCurrentDay(nextDay)
                    ActionResult.Applied("Advanced current day to Day $nextDay: ${action.description}")
                } else {
                    ActionResult.Rejected("Game state not found")
                }
            }

            is MetaAction.StartTheEpilogue -> {
                ActionResult.Applied("Triggered epilogue ending: ${action.endingId}")
            }

            else -> ActionResult.Applied(action.javaClass.simpleName)
        }
    }
}

typealias GmActionExecutor = ToolExecutor
