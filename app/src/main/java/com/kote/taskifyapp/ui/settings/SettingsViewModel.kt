package com.kote.taskifyapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kote.taskifyapp.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskViewType {
    DefaultView,
    CardView;

    companion object {
        fun fromInt(value: Int) = enumValues<TaskViewType>().getOrNull(value) ?: DefaultView
    }
}

enum class SettingType {
    TaskViewType
}

data class SettingsUiState(
    val taskViewType: TaskViewType = TaskViewType.DefaultView
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _settingsUiState = MutableStateFlow(SettingsUiState())
    val settingsUiState = _settingsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            val initialType = userPreferencesRepository.taskViewTypeFlow.first()
            _settingsUiState.update { it.copy(taskViewType = initialType) }
        }
    }

    fun setSettings(settingType: SettingType?, value: Int) {
        when(settingType) {
            SettingType.TaskViewType -> setTaskView(TaskViewType.fromInt(value))
            else -> Unit
        }
    }

    private fun setTaskView(newViewType: TaskViewType) {
        if (_settingsUiState.value.taskViewType != newViewType) {
            _settingsUiState.update { it.copy(taskViewType = newViewType) }
            viewModelScope.launch { userPreferencesRepository.saveTaskViewType(newViewType) }
        }
    }
}