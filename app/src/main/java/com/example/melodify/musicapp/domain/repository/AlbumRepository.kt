package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Album

/**
 * Repository interface for album management
 * Handles album retrieval and fetching songs within an album
 */
interface AlbumRepository {

    /**
     * Get all albums
     * @return List of all albums
     */
    suspend fun getAlbums(): List<Album>

    /**
     * Get a specific album by ID
     * @param albumId Target album ID
     * @return Album data
     */
    suspend fun getAlbum(albumId: String): Album

    /**
     * Get all songs within a specific album
     * @param albumId Target album ID
     * @return List of songs in the album
     */
    suspend fun getAlbumSongs(albumId: String): List<Song>
}