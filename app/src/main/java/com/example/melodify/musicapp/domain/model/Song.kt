package com.melodify.musicapp.domain.model // Change package name to this

data class Song(
    val id: String,
    val title: String,
    val artistId: String,
    val albumId: String,
    val coverUrl: String,
    val audioUrl: String,
    val duration: Long,
    val genre: String,
    val playCount: Long,
    val isLiked: Boolean,
    val isDownloaded: Boolean
)