package com.melodify.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_songs")
data class RecentSongEntity(
    @PrimaryKey val songId: String,
    val title: String,
    val artistId: String,
    val albumId: String,
    val coverUrl: String,
    val audioUrl: String,
    val duration: Long,
    val playedAt: Long
)
