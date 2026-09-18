package com.theinheritance.ui.ledger

import androidx.lifecycle.ViewModel
import com.theinheritance.accounting.model.Ledger
import com.theinheritance.data.repository.AccountingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val repo: AccountingRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LedgerState())
    val state = _state.asStateFlow()

    private val _ledger = MutableStateFlow<Ledger?>(null)
    val ledger = _ledger.asStateFlow()

    suspend fun load(accountId: Long?) {
        _state.value = LedgerState(accountId)
        if (accountId != null) {
            _ledger.value = repo.buildEngine().ledgerFor(accountId)
        }
    }
}
