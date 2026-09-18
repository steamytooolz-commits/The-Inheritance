package com.theinheritance.ui.npc

import androidx.lifecycle.ViewModel
import com.theinheritance.data.repository.NpcRepository
import com.theinheritance.simulation.NpcAgent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NpcHubViewModel @Inject constructor(
    private val repo: NpcRepository
) : ViewModel() {
    private val _state = MutableStateFlow(NpcHubState())
    val state = _state.asStateFlow()

    suspend fun load() {
        repo.seedDefaults()
        _state.value = NpcHubState(
            listOf(
                NpcAgent(1, "Mara Voss", "Bookkeeper", "Precise, nervous", 55),
                NpcAgent(2, "Silas Vane", "Creditor", "Polite menace", 20),
                NpcAgent(3, "Noor Haddad", "Supplier", "Warm, sharp", 60),
            )
        )
    }

    suspend fun befriend(id: Long) { repo.shiftTrust(id, 5) }
}
