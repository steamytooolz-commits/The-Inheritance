package com.theinheritance.ui.market

import androidx.lifecycle.ViewModel
import com.theinheritance.data.repository.RunRepository
import com.theinheritance.simulation.MarketEventGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val runs: RunRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MarketState())
    val state = _state.asStateFlow()

    fun loadWeek(seed: Long = 7L, days: Int = 7) {
        val gen = MarketEventGenerator(seed)
        _state.value = MarketState(
            events = (1..days).mapNotNull { gen.forDay(it) },
            seed = seed
        )
    }

    suspend fun recordAll() {
        for (event in _state.value.events) runs.recordEvent(event)
    }
}
