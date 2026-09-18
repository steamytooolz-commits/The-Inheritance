package com.theinheritance.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardShape = ClayShapes.Card
    val surfaceColor = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val contentColor = MaterialTheme.colorScheme.onSurface

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Column(
            modifier = modifier
                .shadow(
                    elevation = 2.dp,
                    shape = cardShape,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                )
                .clip(cardShape)
                .background(surfaceColor)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = cardShape
                )
                .padding(18.dp),
            content = content
        )
    }
}

