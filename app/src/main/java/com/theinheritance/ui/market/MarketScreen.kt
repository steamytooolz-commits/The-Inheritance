package com.theinheritance.ui.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.ui.theme.ClayCard

@Composable
fun MarketScreen(vm: MarketViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    LaunchedEffect(Unit) { vm.loadWeek() }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("market_screen"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Market & Street", style = MaterialTheme.typography.headlineMedium) }
        if (s.events.isEmpty()) {
            item {
                ClayCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Quiet week. Too quiet — the uncle is suspicious.")
                }
            }
        }
        items(s.events) { e ->
            ClayCard(modifier = Modifier.fillMaxWidth().testTag("market_event_${e.day}")) {
                Text("Day ${e.day}: ${e.eventType}")
                Text(e.narrative)
            }
        }
    }
}
