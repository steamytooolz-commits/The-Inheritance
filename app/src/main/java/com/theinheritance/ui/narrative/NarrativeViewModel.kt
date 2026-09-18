package com.theinheritance.ui.narrative

import androidx.lifecycle.ViewModel
import com.theinheritance.data.repository.GameStateRepository
import com.theinheritance.data.repository.GmMemoryRepository
import com.theinheritance.gm.GameMaster
import com.theinheritance.gm.GmTurnResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NarrativeViewModel @Inject constructor(
    private val gm: GameMaster,
    private val game: GameStateRepository,
    private val mem: GmMemoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(NarrativeState())
    val state = _state.asStateFlow()

    suspend fun send(text: String) {
        if (text.isBlank()) return
        _state.value = _state.value.copy(
            messages = _state.value.messages + ChatMsg(false, text),
            busy = true
        )
        val gs = game.get()
        val result = gm.turn(text, gs)
        val line = (result as? GmTurnResult.Narrative)?.text
            ?.ifBlank { "(The uncle says nothing. The silence is deliberate.)" }
            ?: "(silence)"
        mem.remember(gs.day, "narrative", line.take(300))
        _state.value = _state.value.copy(
            messages = _state.value.messages + ChatMsg(true, line),
            busy = false
        )
    }
}
