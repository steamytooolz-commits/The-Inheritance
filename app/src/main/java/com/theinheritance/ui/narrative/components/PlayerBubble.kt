package com.theinheritance.ui.narrative.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.theinheritance.ui.theme.ClayCard

@Composable
fun PlayerBubble(text: String) {
    ClayCard(modifier = Modifier.fillMaxWidth().testTag("player_bubble")) {
        Text(text, color = MaterialTheme.colorScheme.primary)
    }
}
