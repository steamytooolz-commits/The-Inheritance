package com.theinheritance.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theinheritance.monetization.ProUnlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val pro: ProUnlockRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            pro.isPro.collect { unlocked ->
                _state.update { it.copy(isPro = unlocked) }
            }
        }
    }

    fun setRemoteUrl(v: String) = _state.update { it.copy(remoteUrl = v) }
    fun setRemoteKey(v: String) = _state.update { it.copy(remoteKey = v) }
    fun toggleRemote() = _state.update { it.copy(useRemote = !it.useRemote) }
}
