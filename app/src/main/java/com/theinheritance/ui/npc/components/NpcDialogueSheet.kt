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
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard

@Composable
fun NpcDialogueSheet(npcName: String, line: String, onClose: () -> Unit) {
    ClayCard(modifier = Modifier.fillMaxWidth().testTag("npc_dialogue_sheet")) {
        Text(npcName, style = MaterialTheme.typography.labelLarge)
        Text(line, modifier = Modifier.padding(vertical = 8.dp))
        ClayButton("Leave", onClose, isPrimary = false, testTag = "btn_close_dialogue")
    }
}
