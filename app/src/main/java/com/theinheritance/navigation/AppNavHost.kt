package com.theinheritance.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.theinheritance.tutorial.TutorialScreen
import com.theinheritance.ui.dashboard.DashboardScreen
import com.theinheritance.ui.journal.JournalScreen
import com.theinheritance.ui.ledger.LedgerScreen
import com.theinheritance.ui.market.MarketScreen
import com.theinheritance.ui.modelmanager.ModelManagerScreen
import com.theinheritance.ui.narrative.NarrativeScreen
import com.theinheritance.ui.npc.NpcHubScreen
import com.theinheritance.ui.pocketbase.PocketBaseScreen
import com.theinheritance.ui.settings.SettingsScreen
import com.theinheritance.ui.statements.StatementsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navItems = listOf(
        Triple(DashboardRoute, "Dashboard & Runway", Icons.Default.Dashboard),
        Triple(NarrativeRoute, "Talk to Uncle George", Icons.Default.AutoStories),
        Triple(StatementsRoute, "Financial Statements", Icons.Default.AccountBalance),
        Triple(LedgerRoute, "General Ledger", Icons.AutoMirrored.Filled.MenuBook),
        Triple(JournalRoute, "Post Journal Entry", Icons.Default.Receipt),
        Triple(NpcHubRoute, "People & Suspects", Icons.Default.Group),
        Triple(MarketRoute, "Market & Economy", Icons.Default.Storefront),
    )

    val toolItems = listOf(
        Triple(ModelManagerRoute, "AI Engine & Local LLMs", Icons.Default.Psychology),
        Triple(PocketBaseRoute, "PocketBase & Trailbase", Icons.Default.CloudSync),
        Triple(SettingsRoute, "Settings & Audio", Icons.Default.Settings),
        Triple(TutorialRoute, "Tutorial & Guide", Icons.AutoMirrored.Filled.HelpOutline),
    )

    @Composable
    fun DrawerContent(onItemClick: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "The Inheritance",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Your uncle died. The books are lying. 30 days to survive.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "CORE OPERATIONS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            navItems.forEach { (route, label, icon) ->
                val selected = backStack?.destination?.hasRoute(route::class) == true
                NavigationDrawerItem(
                    label = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(icon, contentDescription = label) },
                    selected = selected,
                    onClick = {
                        onItemClick()
                        nav.navigate(route) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "INTELLIGENCE & CLOUD",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            toolItems.forEach { (route, label, icon) ->
                val selected = backStack?.destination?.hasRoute(route::class) == true
                NavigationDrawerItem(
                    label = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(icon, contentDescription = label) },
                    selected = selected,
                    onClick = {
                        onItemClick()
                        nav.navigate(route) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTabletExpanded = maxWidth >= 840.dp

        if (isTabletExpanded) {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier.width(300.dp).testTag("app_permanent_drawer")
                    ) {
                        DrawerContent(onItemClick = {})
                    }
                }
            ) {
                MainScaffold(
                    nav = nav,
                    backStack = backStack,
                    onOpenDrawer = {},
                    showDrawerButton = false,
                    showBottomBar = false
                )
            }
        } else {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.width(310.dp).testTag("app_drawer_sheet")
                    ) {
                        DrawerContent(onItemClick = { scope.launch { drawerState.close() } })
                    }
                }
            ) {
                MainScaffold(
                    nav = nav,
                    backStack = backStack,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    showDrawerButton = true,
                    showBottomBar = true
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold(
    nav: androidx.navigation.NavHostController,
    backStack: androidx.navigation.NavBackStackEntry?,
    onOpenDrawer: () -> Unit,
    showDrawerButton: Boolean,
    showBottomBar: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "The Inheritance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (showDrawerButton) {
                        IconButton(
                            onClick = onOpenDrawer,
                            modifier = Modifier.testTag("btn_open_drawer")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val bottomItems = listOf(
                        Triple(DashboardRoute, "Board", Icons.Default.Dashboard),
                        Triple(NarrativeRoute, "Uncle", Icons.Default.AutoStories),
                        Triple(StatementsRoute, "Books", Icons.Default.AccountBalance),
                        Triple(JournalRoute, "Journal", Icons.Default.Receipt),
                        Triple(NpcHubRoute, "People", Icons.Default.Group),
                    )
                    bottomItems.forEach { (route, label, icon) ->
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
                    onOpenNpcHub = { nav.navigate(NpcHubRoute) }
                )
            }
            composable<NarrativeRoute> { NarrativeScreen() }
            composable<JournalRoute> { JournalScreen() }
            composable<LedgerRoute> { LedgerScreen(accountId = null) }
            composable<LedgerDetailRoute> { e -> LedgerScreen(accountId = e.toRoute<LedgerDetailRoute>().accountId) }
            composable<StatementsRoute> {
                StatementsScreen(
                    onAccountClick = { id -> nav.navigate(LedgerDetailRoute(id)) },
                    onOpenNarrative = { nav.navigate(NarrativeRoute) }
                )
            }
            composable<NpcHubRoute> { NpcHubScreen() }
            composable<MarketRoute> { MarketScreen() }
            composable<ModelManagerRoute> { ModelManagerScreen() }
            composable<PocketBaseRoute> { PocketBaseScreen() }
            composable<SettingsRoute> {
                SettingsScreen(
                    onOpenTutorial = { nav.navigate(TutorialRoute) }
                )
            }
            composable<TutorialRoute> { TutorialScreen(onDone = { nav.popBackStack() }) }
        }
    }
}
