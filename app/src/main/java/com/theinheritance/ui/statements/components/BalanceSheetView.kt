package com.theinheritance.ui.statements.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.theinheritance.accounting.model.BalanceSheet
import com.theinheritance.ui.theme.ClayCard

@Composable
fun BalanceSheetView(
    balanceSheet: BalanceSheet?,
    onLineClick: (title: String, amountCents: Long, accountId: Long?) -> Unit
) {
    ClayCard(modifier = Modifier.fillMaxWidth().testTag("balance_sheet")) {
        Text(
            text = "Balance Sheet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Tap any account line below to drill down and see Uncle George's verdict.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        if (balanceSheet == null) {
            Text("Loading balance sheet...", style = MaterialTheme.typography.bodyMedium)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Section: Assets
                Text(
                    text = "ASSETS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                balanceSheet.lines.filter { it.indent > 0 && it.amountCents > 0 }.forEach { line ->
                    val accId = when {
                        line.label.contains("Cash on Hand", ignoreCase = true) -> 1000L
                        line.label.contains("Bank", ignoreCase = true) -> 1010L
                        line.label.contains("Receivable", ignoreCase = true) -> 1100L
                        line.label.contains("Inventory", ignoreCase = true) -> 1200L
                        line.label.contains("Equipment", ignoreCase = true) -> 1500L
                        line.label.contains("Prepaid", ignoreCase = true) -> 1300L
                        line.label.contains("Payable", ignoreCase = true) -> 2000L
                        line.label.contains("Loan", ignoreCase = true) -> 2200L
                        line.label.contains("Equity", ignoreCase = true) -> 3000L
                        else -> null
                    }
                    StatementRow(
                        label = line.label,
                        amount = line.amountCents,
                        onClick = { onLineClick(line.label, line.amountCents, accId) }
                    )
                }

                StatementRow(
                    label = "Total Assets",
                    amount = balanceSheet.assetCents,
                    isTotal = true,
                    onClick = { onLineClick("Total Assets", balanceSheet.assetCents, null) }
                )

                // Section: Liabilities
                Text(
                    text = "LIABILITIES & DEBT",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )

                StatementRow(
                    label = "Total Liabilities",
                    amount = balanceSheet.liabilityCents,
                    isTotal = true,
                    onClick = { onLineClick("Total Liabilities", balanceSheet.liabilityCents, 2000) }
                )

                // Section: Equity
                Text(
                    text = "OWNER'S EQUITY",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                StatementRow(
                    label = "Total Equity",
                    amount = balanceSheet.equityCents,
                    isHighlight = true,
                    onClick = { onLineClick("Owner's Equity", balanceSheet.equityCents, 3000) }
                )
            }
        }
    }
}
