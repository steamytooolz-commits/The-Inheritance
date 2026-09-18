package com.theinheritance.ui.ledger

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.accounting.model.LedgerEntry
import com.theinheritance.accounting.money.MoneyFormatter
import com.theinheritance.ui.theme.ClayCard

@Composable
fun LedgerScreen(
    accountId: Long?,
    vm: LedgerViewModel = hiltViewModel()
) {
    val selectedId by vm.selectedAccountId.collectAsState()
    val ledger by vm.ledger.collectAsState()

    LaunchedEffect(accountId) {
        vm.selectAccount(accountId ?: 1010L)
    }

    val currentAccount = vm.accounts.firstOrNull { it.id == selectedId }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ledger_screen")
    ) {
        val isWideTablet = maxWidth >= 800.dp

        if (isWideTablet) {
            // Tablet Split-Pane: Left side = Chart of Accounts, Right side = T-Account Postings
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Chart of Accounts
                LazyColumn(
                    modifier = Modifier.weight(0.4f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        ClayCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Chart of Accounts",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Select an account to view T-account ledger entries.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(vm.accounts) { acc ->
                        val isSelected = acc.id == selectedId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .clickable { vm.selectAccount(acc.id) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "${acc.code} ${acc.name}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = acc.type.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Right Column: Ledger Postings
                LazyColumn(
                    modifier = Modifier.weight(0.6f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ClayCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = if (currentAccount == null) "General Ledger" else "${currentAccount.code} — ${currentAccount.name}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (ledger != null) {
                                Text(
                                    text = "Current Running Balance: ${MoneyFormatter.format(ledger!!.runningBalance)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    val entries = ledger?.entries ?: emptyList()
                    if (entries.isEmpty()) {
                        item {
                            ClayCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "No postings found for this account.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(entries) { entry ->
                            LedgerItemCard(entry)
                        }
                    }
                }
            }
        } else {
            // Mobile Layout
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ClayCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (currentAccount == null) "General Ledger" else "${currentAccount.code} — ${currentAccount.name}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Running T-account entries and margin balances.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        if (ledger != null) {
                            Text(
                                text = "Current Balance: ${MoneyFormatter.format(ledger!!.runningBalance)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(vm.accounts) { acc ->
                            FilterChip(
                                selected = acc.id == selectedId,
                                onClick = { vm.selectAccount(acc.id) },
                                label = { Text("${acc.code} ${acc.name}") }
                            )
                        }
                    }
                }

                val entries = ledger?.entries ?: emptyList()
                if (entries.isEmpty()) {
                    item {
                        ClayCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No postings found for this account in the current period.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(entries) { entry ->
                        LedgerItemCard(entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun LedgerItemCard(entry: LedgerEntry) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = entry.memo.ifBlank { "Journal Entry #${entry.journalEntryId}" },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.date.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!entry.debit.isZero()) {
                    Text(
                        text = "DR: ${MoneyFormatter.format(entry.debit)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (!entry.credit.isZero()) {
                    Text(
                        text = "CR: ${MoneyFormatter.format(entry.credit)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "Bal: ${MoneyFormatter.format(entry.balance)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
