package com.theinheritance.ui.statements.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theinheritance.accounting.model.IncomeStatement
import com.theinheritance.accounting.money.Money
import com.theinheritance.accounting.money.MoneyFormatter
import com.theinheritance.ui.theme.ClayCard

@Composable
fun IncomeStatementView(
    statement: IncomeStatement?,
    onLineClick: (title: String, amountCents: Long, accountId: Long?) -> Unit
) {
    ClayCard(modifier = Modifier.fillMaxWidth().testTag("income_statement")) {
        Text(
            text = "Income Statement",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Tap any line below to ask Uncle George to explain it.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        if (statement == null) {
            Text("Calculating figures...", style = MaterialTheme.typography.bodyMedium)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Total Revenue Line
                StatementRow(
                    label = "Total Revenue",
                    amount = statement.revenueCents,
                    isTotal = true,
                    onClick = { onLineClick("Total Revenue", statement.revenueCents, 4000) }
                )

                // Detailed lines if available
                statement.lines.filter { !it.isTotal && it.amountCents != 0L }.forEach { line ->
                    StatementRow(
                        label = "  • ${line.label}",
                        amount = line.amountCents,
                        isSubItem = true,
                        onClick = { onLineClick(line.label, line.amountCents, null) }
                    )
                }

                // Cost of Goods Sold
                StatementRow(
                    label = "Cost of Goods Sold (COGS)",
                    amount = statement.cogsCents,
                    onClick = { onLineClick("Cost of Goods Sold", statement.cogsCents, 5000) }
                )

                // Gross Profit
                val grossProfit = statement.revenueCents - statement.cogsCents
                StatementRow(
                    label = "Gross Profit",
                    amount = grossProfit,
                    isTotal = true,
                    onClick = { onLineClick("Gross Profit", grossProfit, null) }
                )

                // Total Expenses
                StatementRow(
                    label = "Operating Expenses",
                    amount = statement.expenseCents,
                    onClick = { onLineClick("Operating Expenses", statement.expenseCents, 5200) }
                )

                // Net Income
                StatementRow(
                    label = "Net Income / (Loss)",
                    amount = statement.netIncomeCents,
                    isHighlight = true,
                    onClick = { onLineClick("Net Income", statement.netIncomeCents, null) }
                )
            }
        }
    }
}

@Composable
fun StatementRow(
    label: String,
    amount: Long,
    isTotal: Boolean = false,
    isHighlight: Boolean = false,
    isSubItem: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = when {
        isHighlight -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        isTotal -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    }
    val textColor = when {
        isHighlight -> MaterialTheme.colorScheme.primary
        amount < 0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(
                indication = ripple(),
                interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource()
            ) { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal || isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal || isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = MoneyFormatter.format(Money(amount)),
            style = if (isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal || isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}
