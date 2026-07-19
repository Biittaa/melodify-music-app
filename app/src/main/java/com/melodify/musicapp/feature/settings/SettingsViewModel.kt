package com.melodify.musicapp.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()

    init {
        loadSettings()
        collectEvents()
    }

    private fun collectEvents() {
        viewModelScope.launch {
            _events.collect { event ->
                when (event) {
                    SettingsEvent.OnToggleDarkMode -> toggleDarkMode()
                    SettingsEvent.OnShowLanguageDialog -> showLanguageDialog()
                    SettingsEvent.OnDismissLanguageDialog -> dismissLanguageDialog()
                    is SettingsEvent.OnChangeLanguage -> changeLanguage(event.language)
                    SettingsEvent.OnIncreaseFontSize -> increaseFontSize()
                    SettingsEvent.OnDecreaseFontSize -> decreaseFontSize()
                    SettingsEvent.OnToggleNotifications -> toggleNotifications()
                    SettingsEvent.OnLogout -> logout()
                }
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val settings = settingsRepository.getSettings()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isDarkMode = settings.darkMode,
                        language = settings.language,
                        fontScale = settings.fontScale,
                        isNotificationEnabled = settings.notificationEnabled
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error loading settings"
                    )
                }
            }
        }
    }

    private fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_uiState.value.isDarkMode
            _uiState.update { it.copy(isDarkMode = newValue) }
            try {
                settingsRepository.setDarkMode(newValue)
            } catch (e: Exception) {
                // Revert on error
                _uiState.update { it.copy(isDarkMode = !newValue) }
            }
        }
    }

    private fun showLanguageDialog() {
        _uiState.update { it.copy(showLanguageDialog = true) }
    }

    private fun dismissLanguageDialog() {
        _uiState.update { it.copy(showLanguageDialog = false) }
    }

    private fun changeLanguage(language: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    language = language,
                    showLanguageDialog = false
                )
            }
            try {
                settingsRepository.setLanguage(language)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun increaseFontSize() {
        viewModelScope.launch {
            val newScale = (_uiState.value.fontScale + 0.1f).coerceAtMost(1.5f)
            _uiState.update { it.copy(fontScale = newScale) }
            try {
                settingsRepository.setFontScale(newScale)
            } catch (e: Exception) {
                // Revert on error
                _uiState.update { it.copy(fontScale = _uiState.value.fontScale - 0.1f) }
            }
        }
    }

    private fun decreaseFontSize() {
        viewModelScope.launch {
            val newScale = (_uiState.value.fontScale - 0.1f).coerceAtLeast(0.7f)
            _uiState.update { it.copy(fontScale = newScale) }
            try {
                settingsRepository.setFontScale(newScale)
            } catch (e: Exception) {
                // Revert on error
                _uiState.update { it.copy(fontScale = _uiState.value.fontScale + 0.1f) }
            }
        }
    }

    private fun toggleNotifications() {
        viewModelScope.launch {
            val newValue = !_uiState.value.isNotificationEnabled
            _uiState.update { it.copy(isNotificationEnabled = newValue) }
            try {
                settingsRepository.setNotification(newValue)
            } catch (e: Exception) {
                _uiState.update { it.copy(isNotificationEnabled = !newValue) }
            }
        }
    }

    private fun logout() {
        // TODO: Implement logout
    }
}