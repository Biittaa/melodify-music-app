package com.melodify.musicapp.domain.model

data class Settings(
    val darkMode: Boolean,
    val language: String,
    val fontScale: Float,
    val notificationEnabled: Boolean
)