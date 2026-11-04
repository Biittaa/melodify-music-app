package com.melodify.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "downloaded_songs")
data class DownloadedSongEntity(
    @PrimaryKey val songId: String,
    val localFilePath: String,
    val downloadedAt: Long
)