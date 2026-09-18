package com.theinheritance.ui.ledger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theinheritance.accounting.model.Account
import com.theinheritance.accounting.model.ChartOfAccounts
import com.theinheritance.accounting.model.Ledger
import com.theinheritance.data.repository.AccountingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val repo: AccountingRepository
) : ViewModel() {
    private val _selectedAccountId = MutableStateFlow<Long?>(1010L)
    val selectedAccountId = _selectedAccountId.asStateFlow()

    private val _ledger = MutableStateFlow<Ledger?>(null)
    val ledger = _ledger.asStateFlow()

    val accounts: List<Account> = ChartOfAccounts.default()

    fun selectAccount(id: Long?) {
        _selectedAccountId.value = id
        viewModelScope.launch {
            if (id != null) {
                _ledger.value = repo.getLedger(id)
            } else {
                _ledger.value = null
            }
        }
    }
}
