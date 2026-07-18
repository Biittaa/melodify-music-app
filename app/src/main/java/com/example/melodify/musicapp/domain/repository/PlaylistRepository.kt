package com.melodify.musicapp.domain.repository

import androidx.paging.PagingSource
import com.melodify.musicapp.data.local.entity.PlaylistSongEntity
import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.domain.model.Song

/**
 * Repository interface for playlist management
 * Handles CRUD operations for playlists and their song associations
 */
interface PlaylistRepository {

    /**
     * Get all playlists owned by a specific user
     * @param userId ID of the playlist owner
     * @return List of playlists
     */
    suspend fun getUserPlaylists(userId: String): List<Playlist>

    /**
     * Create a new playlist
     * @param name Title of the new playlist
     */
    suspend fun createPlaylist(name: String)

    /**
     * Update an existing playlist
     * @param playlist Updated playlist object
     */
    suspend fun updatePlaylist(playlist: Playlist)

    /**
     * Delete a playlist by ID
     * @param id ID of the playlist to delete
     */
    suspend fun deletePlaylist(id: String)

    /**
     * Add a song to a playlist
     * @param playlistId ID of the playlist
     * @param songId ID of the song to add
     */
    suspend fun addSong(playlistId: String, songId: String)

    /**
     * Remove a song from a playlist
     * @param playlistId ID of the playlist
     * @param songId ID of the song to remove
     */
    suspend fun removeSong(playlistId: String, songId: String)

    /**
     * Get all songs in a specific playlist (non-paginated)
     * @param playlistId ID of the playlist
     * @return List of songs in the playlist
     */
    suspend fun getPlaylistSongs(playlistId: String): List<Song>

    /**
     * Get PagingSource for songs of a specific playlist (for Paging3)
     * Uses Room numeric offset pagination
     * @param playlistId ID of the playlist
     * @return PagingSource for handling pagination
     */
    fun getPlaylistSongsPaging(playlistId: String): PagingSource<Int, PlaylistSongEntity>
}