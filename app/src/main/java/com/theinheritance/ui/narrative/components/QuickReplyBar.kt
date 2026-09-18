package com.theinheritance.ui.narrative.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickReplyBar(onPick: (String) -> Unit) {
    val options = listOf(
        "Who is P. Vance in payroll?",
        "Is Silas Vane's loan real?",
        "Explain the inventory write-off",
        "How much cash runway is left?",
        "Give me a forensic hint",
        "Who do I owe money to?"
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        items(options) { q ->
            FilterChip(
                selected = false,
                onClick = { onPick(q) },
                label = { Text(q) }
            )
        }
    }
}
