package com.theinheritance.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.ui.journal.components.AccountPicker
import com.theinheritance.ui.journal.components.BalanceIndicator
import com.theinheritance.ui.journal.components.DebitCreditColumn
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard
import com.theinheritance.ui.theme.ClayTextField
import kotlinx.coroutines.launch

@Composable
fun JournalScreen(vm: JournalViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    val balanced = (s.amountRands.toDoubleOrNull() ?: 0.0) > 0 && s.debitId != s.creditId

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("journal_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "New Journal Entry",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    BalanceIndicator(balanced)
                    ClayTextField(s.memo, vm::setMemo, "Memo / Description", Modifier.fillMaxWidth(), testTag = "journal_memo")
                    ClayTextField(s.amountRands, vm::setAmount, "Amount (Rands)", Modifier.fillMaxWidth(), testTag = "journal_amount")
                    AccountPicker("Debit account (+Asset/+Expense, -Liability)", s.debitId, vm.accounts, vm::setDebit)
                    AccountPicker("Credit account (+Liability/+Revenue, -Asset)", s.creditId, vm.accounts, vm::setCredit)
                    DebitCreditColumn(
                        debitLabel = vm.accounts.firstOrNull { it.id == s.debitId }?.name ?: "None",
                        creditLabel = vm.accounts.firstOrNull { it.id == s.creditId }?.name ?: "None",
                        amountRands = s.amountRands.ifBlank { "0" }
                    )
                    ClayButton(
                        text = "Post Journal Entry ✍️",
                        onClick = {
                            scope.launch {
                                vm.post()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        testTag = "btn_post_entry"
                    )
                    if (s.message.isNotBlank()) {
                        Text(
                            text = s.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Posted Journal Records (${s.postedEntries.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(s.postedEntries) { entry ->
            ClayCard(modifier = Modifier.fillMaxWidth().testTag("journal_entry_${entry.id}")) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.memo,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${entry.date}  •  Posted by: ${entry.postedBy}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!entry.fraudFlag.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.errorContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Text("Flagged", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }
                }
            }
        }
    }
}
