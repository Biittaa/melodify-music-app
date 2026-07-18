package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.domain.model.Song

interface PlaylistRepository {
    suspend fun getUserPlaylists(userId: String): List<Playlist>
    suspend fun createPlaylist(name: String)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(id: String)
    suspend fun addSong(playlistId: String, songId: String)
    suspend fun removeSong(playlistId: String, songId: String)
    suspend fun getPlaylistSongs(playlistId: String): List<Song>

    fun getPlaylistSongsPaging(playlistId: String): PagingSource<Int, Song>
}