package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Settings

/**
 * Repository interface for app settings management
 * Handles theme, language, font size, and notification preferences
 * Uses DataStore for persistent storage
 */
interface SettingsRepository {

    /**
     * Get current app settings
     * @return Settings object
     */
    suspend fun getSettings(): Settings

    /**
     * Enable or disable dark mode
     * @param enabled true = dark mode on, false = off
     */
    suspend fun setDarkMode(enabled: Boolean)

    /**
     * Change app language
     * @param language Language code (e.g., "fa" for Persian, "en" for English)
     */
    suspend fun setLanguage(language: String)

    /**
     * Change font scale/size
     * @param scale Font scale multiplier (1.0f = normal, 1.2f = larger)
     */
    suspend fun setFontScale(scale: Float)

    /**
     * Enable or disable notifications
     * @param enabled true = notifications on, false = off
     */
    suspend fun setNotification(enabled: Boolean)

    // Add these methods to SettingsRepository interface
    suspend fun getPremium(): Boolean
    suspend fun setPremium(isPremium: Boolean)
}