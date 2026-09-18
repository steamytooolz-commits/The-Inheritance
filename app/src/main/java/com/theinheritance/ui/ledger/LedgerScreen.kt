package com.theinheritance.ui.ledger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.theinheritance.accounting.model.ChartOfAccounts
import com.theinheritance.ui.theme.ClayCard

@Composable
fun LedgerScreen(accountId: Long?) {
    val accounts = ChartOfAccounts.default()
    val acc = accounts.firstOrNull { it.id == accountId }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("ledger_screen"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    if (acc == null) "General Ledger" else "${acc.code} — ${acc.name}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text("Tap any statement line to drill down here. The uncle annotates the margins.")
            }
        }
        items(accounts) { a ->
            ClayCard(modifier = Modifier.fillMaxWidth().testTag("ledger_row_${a.id}")) {
                Text("${a.code} ${a.name}")
                Text(a.type.name, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
