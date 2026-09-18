package com.theinheritance.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.theinheritance.ui.dashboard.DashboardScreen
import com.theinheritance.ui.journal.JournalScreen
import com.theinheritance.ui.ledger.LedgerScreen
import com.theinheritance.ui.market.MarketScreen
import com.theinheritance.ui.modelmanager.ModelManagerScreen
import com.theinheritance.ui.narrative.NarrativeScreen
import com.theinheritance.ui.npc.NpcHubScreen
import com.theinheritance.ui.settings.SettingsScreen
import com.theinheritance.ui.statements.StatementsScreen
import com.theinheritance.tutorial.TutorialScreen

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    Triple(DashboardRoute, "Board", Icons.Default.Dashboard),
                    Triple(NarrativeRoute, "Uncle", Icons.Default.AutoStories),
                    Triple(JournalRoute, "Journal", Icons.Default.Receipt),
                    Triple(StatementsRoute, "Books", Icons.Default.AccountBalance),
                    Triple(NpcHubRoute, "People", Icons.Default.Group),
                )
                items.forEach { (route, label, icon) ->
                    val selected = backStack?.destination?.hasRoute(route::class) == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            nav.navigate(route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(nav, startDestination = DashboardRoute, Modifier.padding(padding)) {
            composable<DashboardRoute> {
                DashboardScreen(
                    onOpenNarrative = { nav.navigate(NarrativeRoute) },
                    onOpenJournal = { nav.navigate(JournalRoute) },
                    onOpenStatements = { nav.navigate(StatementsRoute) },
                    onOpenMarket = { nav.navigate(MarketRoute) },
                    onOpenModels = { nav.navigate(ModelManagerRoute) },
                    onOpenSettings = { nav.navigate(SettingsRoute) },
                )
            }
            composable<NarrativeRoute> { NarrativeScreen() }
            composable<JournalRoute> { JournalScreen() }
            composable<LedgerRoute> { LedgerScreen(accountId = null) }
            composable<LedgerDetailRoute> { e -> LedgerScreen(accountId = e.toRoute<LedgerDetailRoute>().accountId) }
            composable<StatementsRoute> { StatementsScreen(onAccountClick = { id -> nav.navigate(LedgerDetailRoute(id)) }) }
            composable<NpcHubRoute> { NpcHubScreen() }
            composable<MarketRoute> { MarketScreen() }
            composable<ModelManagerRoute> { ModelManagerScreen() }
            composable<SettingsRoute> { SettingsScreen(onOpenTutorial = { nav.navigate(TutorialRoute) }) }
            composable<TutorialRoute> { TutorialScreen(onDone = { nav.popBackStack() }) }
        }
    }
}
