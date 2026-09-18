package com.theinheritance.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.accounting.money.Money
import com.theinheritance.accounting.money.MoneyFormatter
import com.theinheritance.ui.dashboard.components.CashRunwayRing
import com.theinheritance.ui.dashboard.components.DailyReportDialog
import com.theinheritance.ui.dashboard.components.DebtToEquityRing
import com.theinheritance.ui.dashboard.components.EndingDialog
import com.theinheritance.ui.dashboard.components.GmFeedCard
import com.theinheritance.ui.dashboard.components.ProfitRing
import com.theinheritance.ui.dashboard.components.ShoeboxDialog
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard

@Composable
fun DashboardScreen(
    onOpenNarrative: () -> Unit,
    onOpenJournal: () -> Unit,
    onOpenStatements: () -> Unit,
    onOpenMarket: () -> Unit,
    onOpenModels: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNpcHub: () -> Unit = {},
    vm: DashboardViewModel = hiltViewModel()
) {
    val repoState by vm.state.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
    ) {
        val isWideTablet = maxWidth >= 800.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Business Header & Financial Rings
            item {
                ClayCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = repoState.businessName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Day ${repoState.day} of ${repoState.maxDays}  •  ${MoneyFormatter.formatShort(Money(repoState.cashCents))} cash",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    ) {
                        CashRunwayRing(repoState.cashCents)
                        ProfitRing(repoState.netIncomeCents, repoState.revenueCents)
                        DebtToEquityRing(repoState.debtCents, repoState.equityCents)
                    }
                }
            }

            // Uncle George's GM Feed Card
            item {
                GmFeedCard(repoState.gmLine)
            }

            if (isWideTablet) {
                // Wide Tablet 2-Column Grid
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Left Column: Survival Loop & Actions
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ClayCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Daily Survival Loop",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Review your books, interrogate suspects, then advance to tomorrow to process daily trading revenue and expenses.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ClayButton(
                                        text = "End Day ${repoState.day} ➜ Advance",
                                        onClick = vm::endDay,
                                        modifier = Modifier.fillMaxWidth(),
                                        testTag = "btn_advance_day"
                                    )
                                    ClayButton(
                                        text = "Evidence Shoebox 📦",
                                        onClick = { vm.toggleShoebox(true) },
                                        modifier = Modifier.fillMaxWidth(),
                                        isPrimary = false,
                                        testTag = "btn_open_shoebox"
                                    )
                                }
                            }
                        }

                        // Right Column: Operations Hub Navigation
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Operations & Ledgers",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            ClayButton("Talk to Uncle George (GM)", onOpenNarrative, modifier = Modifier.fillMaxWidth(), testTag = "btn_open_narrative")
                            ClayButton("Financial Statements (Tappable)", onOpenStatements, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_statements")
                            ClayButton("Post Journal Entry", onOpenJournal, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_journal")
                            ClayButton("NPC People & Suspects", onOpenNpcHub, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_npcs")
                            ClayButton("Market & Street Economy", onOpenMarket, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_market")
                            ClayButton("AI Model Settings", onOpenModels, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_models")
                            ClayButton("Settings & History", onOpenSettings, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_settings")
                        }
                    }
                }
            } else {
                // Mobile Layout
                item {
                    ClayCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Daily Survival Loop",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Review your books, interrogate suspects, then advance to tomorrow to process daily trading revenue and expenses.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ClayButton(
                                text = "End Day ${repoState.day} ➜ Advance",
                                onClick = vm::endDay,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_advance_day"
                            )
                            ClayButton(
                                text = "Evidence Shoebox 📦",
                                onClick = { vm.toggleShoebox(true) },
                                modifier = Modifier.weight(1f),
                                isPrimary = false,
                                testTag = "btn_open_shoebox"
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Operations & Ledgers",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ClayButton("Talk to Uncle George (GM)", onOpenNarrative, modifier = Modifier.fillMaxWidth(), testTag = "btn_open_narrative")
                        ClayButton("Financial Statements (Tappable)", onOpenStatements, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_statements")
                        ClayButton("Post Journal Entry", onOpenJournal, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_journal")
                        ClayButton("NPC People & Suspects", onOpenNpcHub, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_npcs")
                        ClayButton("Market & Street Economy", onOpenMarket, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_market")
                        ClayButton("AI Model Settings", onOpenModels, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_models")
                        ClayButton("Settings & History", onOpenSettings, modifier = Modifier.fillMaxWidth(), isPrimary = false, testTag = "btn_open_settings")
                    }
                }
            }
        }

        // End of Day Dialog
        repoState.endOfDayReport?.let { report ->
            DailyReportDialog(
                report = report,
                onDismiss = vm::dismissDailyReport
            )
        }

        // Evidence Shoebox Sheet
        if (repoState.showShoebox) {
            ShoeboxDialog(
                clues = repoState.clues,
                outcomeMessage = repoState.accusationOutcome,
                onSolveClue = vm::solveClue,
                onDismiss = { vm.toggleShoebox(false) }
            )
        }

        // Ending / Victory / Game Over Dialog
        if (repoState.isGameOver || repoState.isVictorious) {
            EndingDialog(
                isVictorious = repoState.isVictorious,
                daysSurvived = repoState.day,
                endingCashCents = repoState.cashCents,
                score = repoState.finalScore,
                onRestart = vm::restartRun
            )
        }
    }
}
