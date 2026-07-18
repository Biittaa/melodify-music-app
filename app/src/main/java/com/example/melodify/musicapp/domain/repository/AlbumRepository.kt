package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Album
import com.melodify.musicapp.domain.model.Song

interface AlbumRepository {
    suspend fun getAlbums(): List<Album>
    suspend fun getAlbum(albumId: String): Album
    suspend fun getAlbumSongs(albumId: String): List<Song>
}