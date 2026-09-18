package com.theinheritance.ui.statements.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.theinheritance.ui.theme.ClayCard

@Composable
fun CashFlowView() {
    ClayCard(modifier = Modifier.fillMaxWidth().testTag("cash_flow")) {
        Text("Cash Flow", style = MaterialTheme.typography.labelLarge)
        Text("Operating +R3 100  •  Investing R0  •  Financing R0")
    }
}
