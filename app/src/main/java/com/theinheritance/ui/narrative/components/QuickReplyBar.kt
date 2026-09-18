package com.theinheritance.ui.narrative.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.theinheritance.ui.theme.ClayButton

@Composable
fun QuickReplyBar(onPick: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Show me what's wrong", "Who do I owe?", "Give me a hint").forEach { q ->
            ClayButton(q, { onPick(q) }, isPrimary = false, testTag = "quick_reply_${q.length}")
        }
    }
}
