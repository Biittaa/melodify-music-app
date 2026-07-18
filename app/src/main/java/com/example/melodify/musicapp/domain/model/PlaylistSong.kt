package com.melodify.musicapp.domain.model

data class PlaylistSong(
    val playlistId: String,
    val songId: String,
    val addedAt: Long
)