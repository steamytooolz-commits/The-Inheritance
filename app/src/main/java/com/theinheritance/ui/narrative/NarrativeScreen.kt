package com.theinheritance.ui.narrative

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.theinheritance.ui.theme.ClayTextField
import kotlinx.coroutines.launch

@Composable
fun NarrativeScreen(vm: NarrativeViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    var draft by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).testTag("narrative_screen")) {
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.messages) { m ->
                if (m.fromGm) GmBubble(m.text) else PlayerBubble(m.text)
            }
            if (state.busy) item { TypingIndicator() }
        }
        QuickReplyBar { q -> scope.launch { vm.send(q) } }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            ClayTextField(draft, { draft = it }, "Ask the uncle…", Modifier.weight(1f))
            ClayButton("Send", { scope.launch { vm.send(draft); draft = "" } }, testTag = "btn_send_narrative")
        }
    }
}
