package com.melodify.musicapp.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.Settings
import com.melodify.musicapp.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _settings.value = settingsRepository.getSettings()
        }
    }
    fun setFontScale(scale: Float) {
        viewModelScope.launch {
            settingsRepository.setFontScale(scale)
            _settings.update { it.copy(fontScale = scale) }
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotification(enabled)
            _settings.update { it.copy(notificationEnabled = enabled) }
        }
    }
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(enabled)
            _settings.update { it.copy(darkMode = enabled) }
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            _settings.update { it.copy(language = language) }
        }
    }
}
