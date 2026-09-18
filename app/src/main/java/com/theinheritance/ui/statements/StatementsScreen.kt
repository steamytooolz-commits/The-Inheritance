package com.theinheritance.ui.statements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.ui.statements.components.BalanceSheetView
import com.theinheritance.ui.statements.components.CashFlowView
import com.theinheritance.ui.statements.components.IncomeStatementView
import com.theinheritance.ui.statements.components.UncleExplanationSheet
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard

@Composable
fun StatementsScreen(
    onAccountClick: (Long) -> Unit,
    onOpenNarrative: () -> Unit = {},
    vm: StatementsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Overview", "Income Statement", "Balance Sheet", "Cash Flow")

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("statements_screen")
    ) {
        val isWideTablet = maxWidth >= 800.dp

        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            if (isWideTablet) {
                // Wide Tablet Split View: Left pane = Financial Statements, Right pane = Uncle George Analysis & Ledger Drilldown
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(0.55f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            ClayCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "The Ledger Never Lies",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Tap any financial statement line below. Uncle George will analyze the entry side-by-side on tablet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        renderStatementContent(
                            tab = selectedTab,
                            state = state,
                            onLineClick = { title, amount, accId -> vm.explainLine(title, amount, accId) }
                        )
                    }

                    // Right Pane: Forensic Analysis Anchor
                    Column(
                        modifier = Modifier.weight(0.45f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ClayCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Uncle George's Audit Notebook",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                state.selectedExplanation?.let { exp ->
                                    Text(
                                        text = "Line: ${exp.title} (${exp.amountFormatted})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = exp.uncleComment,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (exp.relatedAccountId != null) {
                                        ClayButton(
                                            text = "Drill into Account Ledger 🔍",
                                            onClick = { onAccountClick(exp.relatedAccountId) },
                                            modifier = Modifier.fillMaxWidth(),
                                            isPrimary = true
                                        )
                                    }

                                    ClayButton(
                                        text = "Ask Uncle George in Chat 💬",
                                        onClick = onOpenNarrative,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } ?: run {
                                    Text(
                                        text = "Select any line item on the left statement to inspect forensic accounting clues and drill down into T-accounts.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Mobile single column view with bottom sheet
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        ClayCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "The Ledger Never Lies",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Tap any line item below. Uncle George will step in to explain the numbers and point out discrepancies.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    renderStatementContent(
                        tab = selectedTab,
                        state = state,
                        onLineClick = { title, amount, accId -> vm.explainLine(title, amount, accId) }
                    )
                }
            }
        }

        // Mobile Sheet Fallback
        if (maxWidth < 800.dp) {
            state.selectedExplanation?.let { exp ->
                UncleExplanationSheet(
                    explanation = exp,
                    onDismiss = vm::dismissExplanation,
                    onDrillToLedger = { accId -> onAccountClick(accId) },
                    onAskUncleInChat = onOpenNarrative
                )
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.renderStatementContent(
    tab: Int,
    state: StatementsState,
    onLineClick: (String, Long, Long?) -> Unit
) {
    when (tab) {
        0 -> {
            item {
                IncomeStatementView(statement = state.incomeStatement, onLineClick = onLineClick)
            }
            item {
                BalanceSheetView(balanceSheet = state.balanceSheet, onLineClick = onLineClick)
            }
            item {
                CashFlowView(cashFlow = state.cashFlow, onLineClick = onLineClick)
            }
        }
        1 -> item { IncomeStatementView(statement = state.incomeStatement, onLineClick = onLineClick) }
        2 -> item { BalanceSheetView(balanceSheet = state.balanceSheet, onLineClick = onLineClick) }
        3 -> item { CashFlowView(cashFlow = state.cashFlow, onLineClick = onLineClick) }
    }
}
