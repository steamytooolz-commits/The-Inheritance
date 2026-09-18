package com.theinheritance.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theinheritance.data.repository.GameStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    repo: GameStateRepository
) : ViewModel() {
    val state: StateFlow<DashboardState> = repo.observe()
        .map { gs ->
            DashboardState(
                day = gs.day,
                maxDays = gs.maxDays,
                cashCents = gs.cashCents,
                businessName = gs.businessName
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardState())
}
