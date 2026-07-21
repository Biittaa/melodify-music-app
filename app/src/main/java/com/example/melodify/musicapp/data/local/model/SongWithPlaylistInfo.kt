package com.melodify.musicapp.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.melodify.musicapp.data.local.entity.PlaylistSongEntity
import com.melodify.musicapp.data.local.entity.SongEntity

data class SongWithPlaylistInfo(
    @Embedded val playlistSong: PlaylistSongEntity,
    @Relation(
        parentColumn = "songId",
        entityColumn = "songId"
    )
    val song: SongEntity
)