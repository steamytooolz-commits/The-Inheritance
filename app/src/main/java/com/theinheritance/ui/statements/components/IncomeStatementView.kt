package com.theinheritance.ui.statements.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.theinheritance.ui.theme.ClayCard

@Composable
fun IncomeStatementView() {
    ClayCard(modifier = Modifier.fillMaxWidth().testTag("income_statement")) {
        Text("Income Statement (Month to date)", style = MaterialTheme.typography.labelLarge)
        Text("Revenue — R12 400  •  Expenses — R9 300  •  Net — R3 100")
    }
}
