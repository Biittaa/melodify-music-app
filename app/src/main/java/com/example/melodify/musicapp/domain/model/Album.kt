package com.melodify.musicapp.domain.model

data class Album(
    val id: String,
    val title: String,
    val artistId: String,
    val coverUrl: String,
    val releaseDate: String
)