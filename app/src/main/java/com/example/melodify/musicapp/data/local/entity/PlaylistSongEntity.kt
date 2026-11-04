package com.melodify.musicapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "playlist_songs",
    primaryKeys = ["playlistId", "songId"]
)
data class PlaylistSongEntity(
    val playlistId: String,
    val songId: String,
    val addedAt: Long,
    @ColumnInfo(name = "position")
    val position: Int = 0
)