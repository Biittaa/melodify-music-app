package com.melodify.musicapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.melodify.musicapp.core.common.Constants
import com.melodify.musicapp.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.DATASTORE_NAME
)

@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    // -------- کلیدهای Preferences --------
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey(Constants.PREF_DARK_MODE)
        private val LANGUAGE_KEY = stringPreferencesKey(Constants.PREF_LANGUAGE)
        private val FONT_SCALE_KEY = floatPreferencesKey(Constants.PREF_FONT_SCALE)
        private val NOTIFICATION_ENABLED_KEY = booleanPreferencesKey(Constants.PREF_NOTIFICATION_ENABLED)
        private val USER_PREMIUM_KEY = booleanPreferencesKey("user_premium") // اضافی برای کش پریمیوم
    }

    val settingsFlow: Flow<Settings> = context.dataStore.data
        .map { preferences ->
            Settings(
                darkMode = preferences[DARK_MODE_KEY] ?: false,
                language = preferences[LANGUAGE_KEY] ?: "fa", // پیش‌فرض فارسی
                fontScale = preferences[FONT_SCALE_KEY] ?: 1.0f,
                notificationEnabled = preferences[NOTIFICATION_ENABLED_KEY] ?: true
            )
        }

    suspend fun getSettings(): Settings {
        return settingsFlow.first()
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SCALE_KEY] = scale
        }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED_KEY] = enabled
        }
    }

    // -------- optional funcs for Premium --------
    val premiumFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[USER_PREMIUM_KEY] ?: false
        }

    suspend fun setPremium(isPremium: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USER_PREMIUM_KEY] = isPremium
        }
    }

    // ------ reset all settings --------
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}