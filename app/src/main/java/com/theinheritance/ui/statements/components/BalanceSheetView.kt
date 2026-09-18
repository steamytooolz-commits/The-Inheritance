package com.theinheritance.ui.statements.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.theinheritance.accounting.model.ChartOfAccounts
import com.theinheritance.ui.theme.ClayCard

@Composable
fun BalanceSheetView(onAccountClick: (Long) -> Unit) {
    ClayCard(modifier = Modifier.fillMaxWidth().testTag("balance_sheet")) {
        Text("Balance Sheet", style = MaterialTheme.typography.labelLarge)
        ChartOfAccounts.default().take(8).forEach { a ->
            Text(
                "${a.code} ${a.name}",
                modifier = Modifier.clickable { onAccountClick(a.id) }.padding(vertical = 4.dp)
            )
        }
        Text("Every line is tappable — the uncle explains it in plain language.", style = MaterialTheme.typography.bodyMedium)
    }
}
