package com.theinheritance.ui.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theinheritance.ui.journal.components.AccountPicker
import com.theinheritance.ui.journal.components.BalanceIndicator
import com.theinheritance.ui.journal.components.DebitCreditColumn
import com.theinheritance.ui.journal.components.TAccount
import com.theinheritance.ui.theme.ClayButton
import com.theinheritance.ui.theme.ClayCard
import com.theinheritance.ui.theme.ClayTextField
import kotlinx.coroutines.launch

@Composable
fun JournalScreen(vm: JournalViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    var entries by remember { mutableStateOf(listOf<String>()) }
    val scope = rememberCoroutineScope()
    val balanced = (s.amountRands.toDoubleOrNull() ?: 0.0) > 0 && s.debitId != s.creditId
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("journal_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
                Text("New Journal Entry", style = MaterialTheme.typography.headlineMedium)
                BalanceIndicator(balanced)
                ClayTextField(s.memo, vm::setMemo, "Memo", testTag = "journal_memo")
                ClayTextField(s.amountRands, vm::setAmount, "Amount (R)", testTag = "journal_amount")
                AccountPicker("Debit account", s.debitId, vm.accounts, vm::setDebit)
                AccountPicker("Credit account", s.creditId, vm.accounts, vm::setCredit)
                DebitCreditColumn(
                    debitLabel = vm.accounts.firstOrNull { it.id == s.debitId }?.name ?: "",
                    creditLabel = vm.accounts.firstOrNull { it.id == s.creditId }?.name ?: "",
                    amountRands = s.amountRands.ifBlank { "0" }
                )
                ClayButton(
                    "Post Entry",
                    {
                        scope.launch {
                            if (vm.post()) entries = entries + "${s.memo.ifBlank { "Manual" }} (R${s.amountRands})"
                        }
                    },
                    testTag = "btn_post_entry"
                )
                if (s.message.isNotBlank()) Text(s.message, modifier = Modifier.padding(top = 8.dp))
            }
        }
        item { Text("Tip: every debit needs an equal credit. Tap a statement line later and the uncle will explain it.") }
        items(entries) { e -> TAccount(e, 0, 0) }
    }
}
