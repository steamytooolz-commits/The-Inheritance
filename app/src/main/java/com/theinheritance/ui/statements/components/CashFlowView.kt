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
import com.theinheritance.accounting.model.CashFlowStatement
import com.theinheritance.ui.theme.ClayCard

@Composable
fun CashFlowView(
    cashFlow: CashFlowStatement?,
    onLineClick: (title: String, amountCents: Long, accountId: Long?) -> Unit
) {
    ClayCard(modifier = Modifier.fillMaxWidth().testTag("cash_flow")) {
        Text(
            text = "Statement of Cash Flows",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Cash movement across operations, investing, and financing activities.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        if (cashFlow == null) {
            Text("Calculating cash flow...", style = MaterialTheme.typography.bodyMedium)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatementRow(
                    label = "Cash from Operations",
                    amount = cashFlow.operatingCents,
                    isHighlight = true,
                    onClick = { onLineClick("Operating Cash Flow", cashFlow.operatingCents, null) }
                )

                StatementRow(
                    label = "Cash from Investing (Equipment)",
                    amount = cashFlow.investingCents,
                    onClick = { onLineClick("Investing Cash Flow", cashFlow.investingCents, 1500) }
                )

                StatementRow(
                    label = "Cash from Financing (Debt & Capital)",
                    amount = cashFlow.financingCents,
                    onClick = { onLineClick("Financing Cash Flow", cashFlow.financingCents, 2200) }
                )

                val netCashChange = cashFlow.operatingCents + cashFlow.investingCents + cashFlow.financingCents
                StatementRow(
                    label = "Net Increase / (Decrease) in Cash",
                    amount = netCashChange,
                    isTotal = true,
                    onClick = { onLineClick("Net Cash Change", netCashChange, 1010) }
                )
            }
        }
    }
}
