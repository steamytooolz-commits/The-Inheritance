package com.theinheritance.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theinheritance.accounting.model.JournalEntry
import com.theinheritance.accounting.model.JournalLine
import com.theinheritance.data.local.entity.JournalLineEntity
import com.theinheritance.data.repository.AccountingRepository
import com.theinheritance.data.repository.GameStateRepository
import com.theinheritance.data.repository.GmMemoryRepository
import com.theinheritance.data.repository.RunRepository
import com.theinheritance.simulation.BusinessState
import com.theinheritance.simulation.MarketEvent
import com.theinheritance.simulation.RunScorer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val gameStateRepo: GameStateRepository,
    private val accountingRepo: AccountingRepository,
    private val gmMemoryRepo: GmMemoryRepository,
    private val runRepo: RunRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            gameStateRepo.observe().collectLatest { gs ->
                refreshFinancialMetrics(gs)
            }
        }
    }

    private suspend fun refreshFinancialMetrics(gs: BusinessState) {
        val now = LocalDate.now()
        val startOfMonth = now.withDayOfMonth(1)
        val is_ = accountingRepo.getIncomeStatement(startOfMonth, now)
        val bs = accountingRepo.getBalanceSheet(now)

        val gmLine = when {
            !gs.isAlive -> "The shop is bankrupt. The locks have been changed. The uncle sighs from the void."
            gs.day >= 30 -> "Day 30 reached! The audit is complete. Let's see if the books hold up in probate."
            gs.day == 1 -> "Welcome to the shop. Check the ledger, count the cash till, and don't trust the payroll."
            gs.day == 5 -> "Noor delivered fresh books today. Make sure Mara actually posted the accounts payable."
            gs.day == 15 -> "Midpoint of the month. Silas Vane will be looking for his interest payment."
            gs.day == 25 -> "Payroll day. Keep an eye on any strange consultant disbursements."
            gs.cashCents < 100_000_00 -> "Cash is getting tight! We need to make more sales or collect receivables."
            else -> "Day ${gs.day}: The ledger is waiting. Every number tells a secret if you listen."
        }

        _state.value = _state.value.copy(
            day = gs.day,
            maxDays = gs.maxDays,
            cashCents = gs.cashCents,
            businessName = gs.businessName,
            isAlive = gs.isAlive,
            isGameOver = !gs.isAlive,
            isVictorious = gs.day >= 30 && gs.isAlive,
            gmLine = gmLine,
            revenueCents = is_.revenueCents,
            expenseCents = is_.expenseCents + is_.cogsCents,
            netIncomeCents = is_.netIncomeCents,
            debtCents = bs.liabilityCents,
            equityCents = bs.equityCents
        )
    }

    fun endDay() {
        viewModelScope.launch {
            val cur = gameStateRepo.get()
            if (!cur.isAlive || cur.day >= cur.maxDays) return@launch

            val nextDay = cur.day + 1
            val random = Random(cur.runSeed + cur.day * 1337L)

            val dailyRevenue = (3_500 + random.nextInt(0, 4_500)) * 100L
            val dailyCost = (2_200 + random.nextInt(0, 2_000)) * 100L
            val dailyNet = dailyRevenue - dailyCost

            val todayDate = LocalDate.now().minusDays((30 - nextDay).coerceAtLeast(0).toLong())
            accountingRepo.post(
                JournalEntry(
                    date = todayDate,
                    memo = "Day ${cur.day} Trading Revenue & Register Reconciliation",
                    lines = listOf(
                        JournalLine(1000, debitCents = dailyRevenue),
                        JournalLine(4000, creditCents = dailyRevenue)
                    ),
                    postedBy = "Daily Batch Process"
                ),
                listOf(
                    JournalLineEntity(journalEntryId = 0, accountId = 1000, debitCents = dailyRevenue, creditCents = 0),
                    JournalLineEntity(journalEntryId = 0, accountId = 4000, debitCents = 0, creditCents = dailyRevenue)
                )
            )

            accountingRepo.post(
                JournalEntry(
                    date = todayDate,
                    memo = "Day ${cur.day} Cost of Sales and Daily Operations",
                    lines = listOf(
                        JournalLine(5000, debitCents = dailyCost),
                        JournalLine(1000, creditCents = dailyCost)
                    ),
                    postedBy = "Daily Batch Process"
                ),
                listOf(
                    JournalLineEntity(journalEntryId = 0, accountId = 5000, debitCents = dailyCost, creditCents = 0),
                    JournalLineEntity(journalEntryId = 0, accountId = 1000, debitCents = 0, creditCents = dailyCost)
                )
            )

            val newCash = cur.cashCents + dailyNet
            val isAlive = newCash > 0

            val updatedState = cur.copy(
                day = nextDay,
                cashCents = newCash,
                isAlive = isAlive
            )
            gameStateRepo.upsert(updatedState)

            var eventTitle: String? = null
            if (nextDay % 5 == 0) {
                eventTitle = "Market Shift — Day $nextDay"
                runRepo.recordEvent(
                    MarketEvent(
                        day = nextDay,
                        eventType = "Local Festival",
                        severity = 0.5f,
                        narrative = "High street footfall increased textbook sales by 25% today.",
                        cashDeltaCents = dailyRevenue / 4
                    )
                )
            }

            val gmVerdict = when {
                !isAlive -> "The till is empty. Silas and Piet are knocking on the front glass. We are done."
                nextDay >= 30 -> "You made it to the final audit! 30 days completed. Let's see your final score."
                dailyNet > 0 -> "Solid day. Cash grew by ${(dailyNet / 100)} Rands. The Uncle nods approvingly."
                else -> "Bleeding money today. We lost ${(-dailyNet / 100)} Rands. Tighten up those expenses."
            }

            gmMemoryRepo.remember(nextDay, "daily_turn", gmVerdict)

            val report = DailyReport(
                dayResolved = cur.day,
                revenueCents = dailyRevenue,
                costCents = dailyCost,
                netCents = dailyNet,
                cashEndingCents = newCash,
                gmVerdict = gmVerdict,
                eventTitle = eventTitle
            )

            val finalScore = if (nextDay >= 30 || !isAlive) {
                RunScorer.score(nextDay, newCash, _state.value.clues.any { it.isSolved })
            } else 0

            _state.value = _state.value.copy(
                endOfDayReport = report,
                finalScore = finalScore
            )
        }
    }

    fun dismissDailyReport() {
        _state.value = _state.value.copy(endOfDayReport = null)
    }

    fun toggleShoebox(open: Boolean) {
        _state.value = _state.value.copy(showShoebox = open, accusationOutcome = null)
    }

    fun solveClue(clueId: String) {
        val updatedClues = _state.value.clues.map {
            if (it.id == clueId) it.copy(isSolved = true) else it
        }
        _state.value = _state.value.copy(
            clues = updatedClues,
            accusationOutcome = "Evidence verified! Uncle George whispers: 'You caught them red-handed in the journal margins.'"
        )
    }

    fun restartRun() {
        viewModelScope.launch {
            val newSeed = System.currentTimeMillis()
            gameStateRepo.newRun(newSeed, "The Book Nook")
            _state.value = DashboardState(
                businessName = "The Book Nook",
                cashCents = 420_000_00,
                day = 1,
                maxDays = 30,
                isAlive = true
            )
        }
    }
}
