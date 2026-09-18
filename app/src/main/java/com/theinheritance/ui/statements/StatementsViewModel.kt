package com.theinheritance.ui.statements

import androidx.lifecycle.ViewModel
import com.theinheritance.data.repository.AccountingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

data class StatementTotals(val revenueCents: Long = 0, val expenseCents: Long = 0, val netCents: Long = 0)

@HiltViewModel
class StatementsViewModel @Inject constructor(
    private val repo: AccountingRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StatementsState())
    val state = _state.asStateFlow()

    private val _totals = MutableStateFlow(StatementTotals())
    val totals = _totals.asStateFlow()

    suspend fun refresh(from: LocalDate, to: LocalDate) {
        val (revenue, expense, net) = repo.statements(from, to)
        _totals.value = StatementTotals(revenue, expense, net)
    }
}
