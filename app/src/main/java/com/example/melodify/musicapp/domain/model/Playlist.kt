package com.melodify.musicapp.domain.model

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val ownerId: String,
    val songsCount: Int,
    val isPublic: Boolean
)