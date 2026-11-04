package com.melodify.musicapp.data.repository

import com.melodify.musicapp.data.local.datastore.SettingsDataStore
import com.melodify.musicapp.domain.model.Settings
import com.melodify.musicapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SettingsRepository
 * Uses DataStore for persistent preference storage
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override suspend fun getSettings(): Settings {
        return settingsDataStore.settingsFlow.first()
    }

    override fun getSettingsFlow(): Flow<Settings> {
        return settingsDataStore.settingsFlow
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        settingsDataStore.setDarkMode(enabled)
    }

    override suspend fun setLanguage(language: String) {
        settingsDataStore.setLanguage(language)
    }

    override suspend fun setFontScale(scale: Float) {
        settingsDataStore.setFontScale(scale)
    }

    override suspend fun setNotification(enabled: Boolean) {
        settingsDataStore.setNotificationEnabled(enabled)
    }

    override suspend fun getPremium(): Boolean {
        return settingsDataStore.premiumFlow.first()
    }

    override suspend fun setPremium(isPremium: Boolean) {
        settingsDataStore.setPremium(isPremium)
    }
}
