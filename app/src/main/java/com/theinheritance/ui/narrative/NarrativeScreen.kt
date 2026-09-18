package com.theinheritance.ui.narrative

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.ui.narrative.components.GmBubble
import com.theinheritance.ui.narrative.components.PlayerBubble
import com.theinheritance.ui.narrative.components.QuickReplyBar
import com.theinheritance.ui.narrative.components.TypingIndicator
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard
import com.theinheritance.ui.theme.ClayTextField
import kotlinx.coroutines.launch

@Composable
fun NarrativeScreen(vm: NarrativeViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    var draft by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("narrative_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ClayCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Uncle George (Inside the Ledger)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Ask about suspicious entries, suspect motives, cash flow advice, or accounting rules. He knows the truth behind the books.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.messages) { m ->
                if (m.fromGm) GmBubble(m.text) else PlayerBubble(m.text)
            }
            if (state.busy) {
                item { TypingIndicator() }
            }
        }

        QuickReplyBar { q ->
            scope.launch { vm.send(q) }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ClayTextField(
                value = draft,
                onValueChange = { draft = it },
                label = "Ask the uncle...",
                modifier = Modifier.weight(1f),
                testTag = "input_narrative_draft"
            )
            ClayButton(
                text = "Send ✉️",
                onClick = {
                    if (draft.isNotBlank()) {
                        scope.launch {
                            val text = draft
                            draft = ""
                            vm.send(text)
                        }
                    }
                },
                testTag = "btn_send_narrative"
            )
        }
    }
}
