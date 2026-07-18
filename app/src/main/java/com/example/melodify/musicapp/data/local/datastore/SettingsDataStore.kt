package com.melodify.musicapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.melodify.musicapp.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    companion object {
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val LANGUAGE = stringPreferencesKey("language")
        private val FONT_SCALE = floatPreferencesKey("font_scale")
        private val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    }

    val settingsFlow: Flow<Settings> = context.dataStore.data.map { prefs ->
        Settings(
            darkMode = prefs[DARK_MODE] ?: false,
            language = prefs[LANGUAGE] ?: "fa",
            fontScale = prefs[FONT_SCALE] ?: 1.0f,
            notificationEnabled = prefs[NOTIFICATION_ENABLED] ?: true
        )
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[DARK_MODE] = enabled }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { prefs -> prefs[LANGUAGE] = language }
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { prefs -> prefs[FONT_SCALE] = scale }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[NOTIFICATION_ENABLED] = enabled }
    }
}