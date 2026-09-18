package com.theinheritance.tutorial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard
import kotlinx.coroutines.launch

@Composable
fun TutorialScreen(
    onDone: () -> Unit,
    manager: TutorialManager? = null
) {
    var page by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val (title, body) = TutorialManager.PAGES[page.coerceIn(TutorialManager.PAGES.indices)]
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("tutorial_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("How to survive", style = MaterialTheme.typography.headlineMedium)
        ClayCard(modifier = Modifier.fillMaxWidth().testTag("tutorial_page_$page")) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(body, modifier = Modifier.padding(top = 8.dp))
            Text("Page ${page + 1} of ${TutorialManager.PAGES.size}", modifier = Modifier.padding(top = 8.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (page > 0) {
                ClayButton("Back", { page-- }, isPrimary = false, testTag = "btn_tutorial_back")
            }
            if (page < TutorialManager.PAGES.lastIndex) {
                ClayButton("Next", { page++ }, testTag = "btn_tutorial_next")
            } else {
                ClayButton(
                    "Open the ledger",
                    {
                        scope.launch { manager?.markSeen() }
                        onDone()
                    },
                    testTag = "btn_tutorial_done"
                )
            }
        }
    }
}
