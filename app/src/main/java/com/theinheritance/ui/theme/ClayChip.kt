package com.theinheritance.ui.theme

import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ClayChip(label: String, modifier: Modifier = Modifier) {
    SuggestionChip(
        onClick = {},
        label = { Text(label) },
        modifier = modifier
    )
}
