package com.melodify.musicapp.domain.model

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val repeatMode: Int = 0, // 0: None, 1: All, 2: One
    val shuffleEnabled: Boolean = false,
    val playbackSpeed: Float = 1.0f
)
