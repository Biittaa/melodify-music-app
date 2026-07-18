package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Settings

interface SettingsRepository {
    suspend fun getSettings(): Settings
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setLanguage(language: String)
    suspend fun setFontScale(scale: Float)
    suspend fun setNotification(enabled: Boolean)
}