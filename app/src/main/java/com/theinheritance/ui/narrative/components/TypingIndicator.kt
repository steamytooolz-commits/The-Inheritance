package com.theinheritance.ui.narrative.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun TypingIndicator() {
    Text("The ledger is thinking…", modifier = Modifier.testTag("typing_indicator"))
}
