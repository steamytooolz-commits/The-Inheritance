package com.theinheritance.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun ClayButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    testTag: String = "clay_button"
) {
    var isPressed by remember { mutableStateOf(false) }

    val backgroundColor = when {
        !enabled -> ClayColors.Muted.copy(alpha = 0.3f)
        isPrimary -> ClayColors.Primary
        else -> ClayColors.Secondary
    }

    Box(
        modifier = modifier
            .clip(ClayShapes.Button)
            .background(backgroundColor)
            .then(
                if (isPressed) {
                    Modifier.background(
                        backgroundColor.copy(alpha = 0.8f),
                        ClayShapes.Button
                    )
                } else Modifier
            )
            .border(
                width = 3.dp,
                color = ClayColors.Border,
                shape = ClayShapes.Button
            )
            .shadow(
                elevation = if (isPressed) 2.dp else 6.dp,
                shape = ClayShapes.Button,
                ambientColor = ClayColors.Primary.copy(alpha = 0.15f),
                spotColor = ClayColors.Primary.copy(alpha = 0.15f)
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            }
            .padding(horizontal = 24.dp, vertical = 14.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
    }
}
