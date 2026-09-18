package com.theinheritance.ui.npc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun NpcPortraitCard(
    npc: NpcAgent,
    onTalk: () -> Unit
) {
    ClayCard(modifier = Modifier.fillMaxWidth().testTag("npc_card_${npc.id}")) {
        Text(
            text = npc.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${npc.role}  •  ${npc.personality}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
        )
        Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp)) {
            TrustMeter(npc.trustLevel)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ClayButton(
                text = "Interrogate / Talk 💬",
                onClick = onTalk,
                modifier = Modifier.fillMaxWidth(),
                testTag = "btn_talk_${npc.id}"
            )
        }
    }
}
