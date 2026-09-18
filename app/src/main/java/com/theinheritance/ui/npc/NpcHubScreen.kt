package com.theinheritance.ui.npc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.ui.npc.components.NpcPortraitCard
import kotlinx.coroutines.launch

@Composable
fun NpcHubScreen(vm: NpcHubViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { vm.load() }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("npc_hub_screen"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("People", style = MaterialTheme.typography.headlineMedium) }
        items(s.npcs) { npc ->
            NpcPortraitCard(npc, onBeKind = { scope.launch { vm.befriend(npc.id) } })
        }
    }
}
