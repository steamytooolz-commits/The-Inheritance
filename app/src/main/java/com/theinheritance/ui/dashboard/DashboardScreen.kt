package com.theinheritance.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
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
import com.theinheritance.ui.dashboard.components.GmFeedCard
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
    vm: DashboardViewModel = hiltViewModel()
) {
    val repoState by vm.state.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Text(repoState.businessName, style = MaterialTheme.typography.headlineMedium)
                Text("Day ${repoState.day} of ${repoState.maxDays} — ${MoneyFormatter.formatShort(Money(repoState.cashCents))} cash")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
                    CashRunwayRing(repoState.cashCents)
                }
            }
        }
        item { GmFeedCard(repoState.gmLine) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ClayButton("Talk to the Uncle", onOpenNarrative, testTag = "btn_open_narrative")
                ClayButton("Post in Journal", onOpenJournal, isPrimary = false, testTag = "btn_open_journal")
                ClayButton("Read Statements", onOpenStatements, isPrimary = false, testTag = "btn_open_statements")
                ClayButton("Market", onOpenMarket, isPrimary = false, testTag = "btn_open_market")
                ClayButton("AI Models", onOpenModels, isPrimary = false, testTag = "btn_open_models")
                ClayButton("Settings", onOpenSettings, isPrimary = false, testTag = "btn_open_settings")
            }
        }
    }
}
