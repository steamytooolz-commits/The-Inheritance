package com.theinheritance.navigation

import kotlinx.serialization.Serializable

@Serializable object DashboardRoute
@Serializable object NarrativeRoute
@Serializable object JournalRoute
@Serializable object LedgerRoute
@Serializable data class LedgerDetailRoute(val accountId: Long)
@Serializable object StatementsRoute
@Serializable object NpcHubRoute
@Serializable object MarketRoute
@Serializable object ModelManagerRoute
@Serializable object SettingsRoute
@Serializable object TutorialRoute
