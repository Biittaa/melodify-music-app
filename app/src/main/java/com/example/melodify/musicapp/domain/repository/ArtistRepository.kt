package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Artist

/**
 * Repository interface for artist management
 * Handles artist retrieval, their songs, and follow/unfollow functionality
 */
interface ArtistRepository {

    /**
     * Get all artists
     * @return List of all artists
     */
    suspend fun getArtists(): List<Artist>

    /**
     * Get a specific artist by ID
     * @param id Target artist ID
     * @return Artist data
     */
    suspend fun getArtist(id: String): Artist

    /**
     * Get all songs by a specific artist
     * @param id Target artist ID
     * @return List of songs by the artist
     */
    suspend fun getArtistSongs(id: String): List<Song>

    /**
     * Follow an artist
     * @param id ID of the artist to follow
     */
    suspend fun followArtist(id: String)

    /**
     * Unfollow an artist
     * @param id ID of the artist to unfollow
     */
    suspend fun unfollowArtist(id: String)
}