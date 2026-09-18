package com.theinheritance.ui.npc.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.theinheritance.simulation.NpcAgent
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard

@Composable
fun NpcPortraitCard(npc: NpcAgent, onBeKind: () -> Unit) {
    ClayCard(modifier = Modifier.fillMaxWidth().testTag("npc_card_${npc.id}")) {
        Text(npc.name, style = MaterialTheme.typography.labelLarge)
        Text("${npc.role} — ${npc.personality}")
        Column(modifier = Modifier.padding(top = 8.dp)) {
            TrustMeter(npc.trustLevel)
        }
        ClayButton(
            "Be kind (+trust)",
            onBeKind,
            isPrimary = false,
            testTag = "btn_befriend_${npc.id}"
        )
    }
}
