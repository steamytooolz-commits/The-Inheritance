package com.theinheritance.ui.modelmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theinheritance.data.llm.LlmModel
import com.theinheritance.data.llm.ModelManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ModelManagerViewModel @Inject constructor(
    private val manager: ModelManager
) : ViewModel() {
    private val _state = MutableStateFlow(ModelManagerState())
    val state = _state.asStateFlow()

    fun download(model: LlmModel) {
        val id: UUID = manager.downloadModel(model)
        _state.value = _state.value.copy(downloadingId = model.id, progress = 0)
        viewModelScope.launch {
            manager.observeProgress(id).collect { p ->
                _state.value = _state.value.copy(progress = p)
            }
        }
    }
}
