package com.theinheritance.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theinheritance.accounting.model.Account
import com.theinheritance.accounting.model.ChartOfAccounts
import com.theinheritance.accounting.model.JournalEntry
import com.theinheritance.accounting.model.JournalLine
import com.theinheritance.data.local.entity.JournalLineEntity
import com.theinheritance.data.repository.AccountingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repo: AccountingRepository
) : ViewModel() {
    private val _state = MutableStateFlow(JournalState())
    val state = _state.asStateFlow()

    val accounts: List<Account> = ChartOfAccounts.default()

    init {
        viewModelScope.launch {
            repo.seedIfEmpty()
            repo.observeEntries().collectLatest { entries ->
                _state.value = _state.value.copy(postedEntries = entries)
            }
        }
    }

    fun setMemo(v: String) { _state.value = _state.value.copy(memo = v) }
    fun setAmount(v: String) { _state.value = _state.value.copy(amountRands = v) }
    fun setDebit(id: Long) { _state.value = _state.value.copy(debitId = id) }
    fun setCredit(id: Long) { _state.value = _state.value.copy(creditId = id) }

    suspend fun post(): Boolean {
        val s = _state.value
        val rands = s.amountRands.toDoubleOrNull() ?: 0.0
        val cents = (rands * 100).toLong()
        if (cents <= 0 || s.debitId == s.creditId) {
            _state.value = s.copy(message = "Unbalanced: amount must be > 0 and Debit account cannot equal Credit account.")
            return false
        }
        val debitAccountName = accounts.firstOrNull { it.id == s.debitId }?.name ?: "Account ${s.debitId}"
        val creditAccountName = accounts.firstOrNull { it.id == s.creditId }?.name ?: "Account ${s.creditId}"

        val entry = JournalEntry(
            date = LocalDate.now(),
            memo = s.memo.ifBlank { "Manual journal adjustment" },
            lines = listOf(
                JournalLine(s.debitId, debitCents = cents),
                JournalLine(s.creditId, creditCents = cents)
            ),
            postedBy = "Player (Forensic Adjustment)"
        )
        repo.post(
            entry,
            listOf(
                JournalLineEntity(journalEntryId = 0, accountId = s.debitId, debitCents = cents),
                JournalLineEntity(journalEntryId = 0, accountId = s.creditId, creditCents = cents)
            )
        )
        _state.value = s.copy(
            message = "Successfully posted R${s.amountRands}: DR $debitAccountName / CR $creditAccountName.",
            amountRands = "",
            memo = ""
        )
        return true
    }
}
