package com.melodify.musicapp.domain.model

data class PlayerState(
    val currentSong: Song?,
    val isPlaying: Boolean,
    val currentPosition: Long,
    val duration: Long,
    val repeatMode: Int,
    val shuffleEnabled: Boolean,
    val playbackSpeed: Float
)