package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Album
import com.melodify.musicapp.domain.model.Artist
import com.melodify.musicapp.domain.model.SearchHistory
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.User

/**
 * Repository interface for search functionality
 * Handles searching across songs, artists, albums, users
 * Also manages search history with debounce pattern
 */
interface SearchRepository {

    /**
     * Search for songs by query
     * @param query Search keyword(s)
     * @return List of matching songs
     */
    suspend fun searchSongs(query: String): List<Song>

    /**
     * Search for artists by query
     * @param query Search keyword(s)
     * @return List of matching artists
     */
    suspend fun searchArtists(query: String): List<Artist>

    /**
     * Search for albums by query
     * @param query Search keyword(s)
     * @return List of matching albums
     */
    suspend fun searchAlbums(query: String): List<Album>

    /**
     * Search for users by query
     * @param query Search keyword(s)
     * @return List of matching users
     */
    suspend fun searchUsers(query: String): List<User>

    /**
     * Save a search query to history
     * @param query Search keyword to save
     */
    suspend fun saveHistory(query: String)

    /**
     * Clear all search history
     */
    suspend fun clearHistory()

    /**
     * Get all search history
     * @return List of SearchHistory objects
     */
    suspend fun getHistory(): List<SearchHistory>
}