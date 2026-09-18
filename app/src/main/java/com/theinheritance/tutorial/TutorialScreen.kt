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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("tutorial_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "How to survive",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        ClayCard(modifier = Modifier.fillMaxWidth().testTag("tutorial_page_$page")) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "Page ${page + 1} of ${TutorialManager.PAGES.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (page > 0) {
                ClayButton("Back", { page-- }, isPrimary = false, modifier = Modifier.weight(1f), testTag = "btn_tutorial_back")
            }
            if (page < TutorialManager.PAGES.lastIndex) {
                ClayButton("Next", { page++ }, modifier = Modifier.weight(1f), testTag = "btn_tutorial_next")
            } else {
                ClayButton(
                    text = "Open the ledger",
                    onClick = {
                        scope.launch { manager?.markSeen() }
                        onDone()
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "btn_tutorial_done"
                )
            }
        }
    }
}

