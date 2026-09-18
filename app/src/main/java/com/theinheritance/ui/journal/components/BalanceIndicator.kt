package com.theinheritance.ui.journal.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.theinheritance.ui.theme.ClayColors

@Composable
fun BalanceIndicator(balanced: Boolean) {
    Text(
        if (balanced) "Balanced ✓" else "Unbalanced ✗",
        color = if (balanced) ClayColors.Success else ClayColors.Danger,
        modifier = Modifier.testTag("balanceIndicator")
    )
}
