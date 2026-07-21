package com.melodify.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.melodify.musicapp.domain.model.Song

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val songId: String,
    val title: String,
    val artistId: String,
    val albumId: String,
    val coverUrl: String,
    val audioUrl: String,
    val duration: Long,
    val genre: String,
    val playCount: Long,
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false
)

fun SongEntity.toDomain() = Song(
    id = songId, title = title, artistId = artistId, albumId = albumId,
    coverUrl = coverUrl, audioUrl = audioUrl, duration = duration,
    genre = genre, playCount = playCount, isLiked = isLiked, isDownloaded = isDownloaded
)