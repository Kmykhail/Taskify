@file:Suppress("DEPRECATION")

package com.kote.taskifyapp.ui.settings

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kote.taskifyapp.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

enum class TaskViewType {
    DefaultView,
    CardView;

    companion object {
        fun fromInt(value: Int) = enumValues<TaskViewType>().getOrNull(value) ?: DefaultView
    }
}

enum class Language {
    En,
    Uk;

    companion object {
        fun fromInt(value: Int) = enumValues<Language>().getOrNull(value) ?: En
    }
}

enum class SettingType {
    TaskViewType,
    Language
}

data class SettingsUiState(
    val taskViewType: TaskViewType = TaskViewType.DefaultView,
    val language: Language = Language.En,
)


object LocaleHelper {
    fun updateLocale(context: Context, languageType: Language) {
        val locale = Locale(languageType.name.lowercase())
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun restartActivity(activity: Activity) {
        activity.recreate()
    }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _settingsUiState = MutableStateFlow(SettingsUiState())
    val settingsUiState = _settingsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            val initialViewType = userPreferencesRepository.taskViewTypeFlow.first()
            val initialLanguage = userPreferencesRepository.languageFlow.first()

            _settingsUiState.update {
                it.copy(
                    taskViewType = initialViewType,
                    language = initialLanguage
                )
            }
            Log.d("Debug", "initialViewType: $initialViewType, initialLanguage: $initialLanguage")
        }
    }

    fun setSettings(settingType: SettingType, value: Int, context: Context) {
        when(settingType) {
            SettingType.TaskViewType -> setTaskView(TaskViewType.fromInt(value))
            SettingType.Language -> setLanguage(Language.fromInt(value), context)
        }
    }

    private fun setTaskView(newViewType: TaskViewType) {
        if (_settingsUiState.value.taskViewType != newViewType) {
            _settingsUiState.update { it.copy(taskViewType = newViewType) }
            viewModelScope.launch { userPreferencesRepository.saveTaskViewType(newViewType) }
        }
    }

    private fun setLanguage(language: Language, context: Context) {
        if (_settingsUiState.value.language != language) {
            _settingsUiState.update { it.copy(language = language) }
            viewModelScope.launch {
                userPreferencesRepository.saveLanguage(language)
                LocaleHelper.updateLocale(context, language)
                val activity = context as? Activity
                activity?.let { LocaleHelper.restartActivity(it) }
            }
        }
    }
}