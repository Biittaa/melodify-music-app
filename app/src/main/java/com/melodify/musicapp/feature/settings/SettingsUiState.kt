package com.melodify.musicapp.feature.settings

data class SettingsUiState(
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false,
    val language: String = "en",
    val fontScale: Float = 1.0f,
    val isNotificationEnabled: Boolean = true,
    val showLanguageDialog: Boolean = false,
    val error: String? = null
)

sealed class SettingsEvent {
    object OnToggleDarkMode : SettingsEvent()
    object OnShowLanguageDialog : SettingsEvent()
    object OnDismissLanguageDialog : SettingsEvent()
    data class OnChangeLanguage(val language: String) : SettingsEvent()
    object OnIncreaseFontSize : SettingsEvent()
    object OnDecreaseFontSize : SettingsEvent()
    object OnToggleNotifications : SettingsEvent()
    object OnLogout : SettingsEvent()
}