package com.melodify.musicapp.domain.model

data class Settings(
    val darkMode: Boolean = false,
    val language: String = "en",
    val fontScale: Float = 1.0f,
    val notificationEnabled: Boolean = true
)
