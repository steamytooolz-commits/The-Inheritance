package com.theinheritance.ui.journal

import androidx.lifecycle.ViewModel
import com.theinheritance.accounting.model.Account
import com.theinheritance.accounting.model.ChartOfAccounts
import com.theinheritance.accounting.model.JournalEntry
import com.theinheritance.accounting.model.JournalLine
import com.theinheritance.data.local.entity.JournalLineEntity
import com.theinheritance.data.repository.AccountingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repo: AccountingRepository
) : ViewModel() {
    private val _state = MutableStateFlow(JournalState())
    val state = _state.asStateFlow()

    val accounts: List<Account> = ChartOfAccounts.default()

    fun setMemo(v: String) { _state.value = _state.value.copy(memo = v) }
    fun setAmount(v: String) { _state.value = _state.value.copy(amountRands = v) }
    fun setDebit(id: Long) { _state.value = _state.value.copy(debitId = id) }
    fun setCredit(id: Long) { _state.value = _state.value.copy(creditId = id) }

    suspend fun post(): Boolean {
        val s = _state.value
        val cents = (s.amountRands.toDoubleOrNull() ?: 0.0 * 100).let { (it * 100).toLong() }
        if (cents <= 0 || s.debitId == s.creditId) {
            _state.value = s.copy(message = "Unbalanced: check amount and accounts (DR != CR, amount > 0).")
            return false
        }
        val entry = JournalEntry(
            date = LocalDate.now(),
            memo = s.memo.ifBlank { "Manual entry" },
            lines = listOf(
                JournalLine(s.debitId, debitCents = cents),
                JournalLine(s.creditId, creditCents = cents)
            )
        )
        repo.post(
            entry,
            listOf(
                JournalLineEntity(journalEntryId = 0, accountId = s.debitId, debitCents = cents),
                JournalLineEntity(journalEntryId = 0, accountId = s.creditId, creditCents = cents)
            )
        )
        _state.value = s.copy(
            message = "Posted R${s.amountRands} DR ${s.debitId} / CR ${s.creditId}. The uncle nods.",
            amountRands = ""
        )
        return true
    }
}
