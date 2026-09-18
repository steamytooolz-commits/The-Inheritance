package com.theinheritance.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(ClayShapes.Card)
            .background(
                brush = Brush.verticalGradient(
                    listOf(ClayColors.Surface, ClayColors.Background)
                )
            )
            .border(
                width = 3.dp,
                color = ClayColors.Border,
                shape = ClayShapes.Card
            )
            .shadow(
                elevation = 8.dp,
                shape = ClayShapes.Card,
                ambientColor = ClayColors.Primary.copy(alpha = 0.12f),
                spotColor = ClayColors.Primary.copy(alpha = 0.12f)
            )
            .padding(20.dp),
        content = content
    )
}
