//package com.melodify.musicapp.domain.repository
//
//import androidx.paging.PagingSource
//import com.google.firebase.firestore.DocumentSnapshot
//import com.melodify.musicapp.domain.model.Song
//
///**
// * Repository interface for song management
// * Handles trending songs, latest releases, searching, liking, and play history
// */
//interface SongRepository {
//
//    /**
//     * Get trending/popular songs
//     * @return List of trending songs
//     */
//    suspend fun getTrendingSongs(): List<Song>
//
//    /**
//     * Get latest released songs
//     * @return List of newest songs
//     */
//    suspend fun getLatestSongs(): List<Song>
//
//    /**
//     * Get a specific song by ID
//     * @param songId Target song ID
//     * @return Song data
//     */
//    suspend fun getSong(songId: String): Song
//
//    /**
//     * Search for songs by query string (non-paginated version)
//     * @param query Search keyword(s)
//     * @return List of matching songs
//     */
//    suspend fun searchSongs(query: String): List<Song>
//
//    /**
//     * Get PagingSource for searching songs (for Paging3)
//     * Uses Firestore cursor-based pagination with DocumentSnapshot
//     * @param query Search keyword(s)
//     * @return PagingSource for handling pagination
//     */
//    fun searchSongsPaging(query: String): PagingSource<DocumentSnapshot?, Song>
//
//    /**
//     * Like a song
//     * @param songId ID of the song to like
//     */
//    suspend fun likeSong(songId: String)
//
//    /**
//     * Unlike a song (remove like)
//     * @param songId ID of the song to unlike
//     */
//    suspend fun unlikeSong(songId: String)
//
//    /**
//     * Get all liked songs for the current user
//     * @return List of liked songs
//     */
//    suspend fun getLikedSongs(): List<Song>
//
//    /**
//     * Get recently played songs for the current user
//     * @return List of recently played songs
//     */
//    suspend fun getRecentlyPlayed(): List<Song>
//}



package com.melodify.musicapp.domain.repository

import androidx.paging.PagingSource
import com.melodify.musicapp.domain.model.Song

/**
 * Repository interface for song management
 * Handles trending songs, latest releases, searching, liking, and play history
 */
interface SongRepository {

    /**
     * Get trending/popular songs
     * @return Flow emitting list of trending songs
     */
    suspend fun getTrendingSongs(): List<Song>

    /**
     * Get latest released songs
     * @return Flow emitting list of newest songs
     */
    suspend fun getLatestSongs(): List<Song>

    /**
     * Get a specific song by ID
     * @param songId Target song ID
     * @return Song data
     */
    suspend fun getSong(songId: String): Song

    /**
     * Search for songs by query string
     * @param query Search keyword(s)
     * @return List of matching songs
     */
    suspend fun searchSongs(query: String): List<Song>

    /**
     * Get PagingSource for searching songs (for Paging3)
     * @param query Search keyword(s)
     * @return PagingSource for handling pagination
     */
    fun searchSongsPaging(query: String): PagingSource<Int, Song>  // ✅ Int به جای DocumentSnapshot?

    /**
     * Like a song
     * @param songId ID of the song to like
     */
    suspend fun likeSong(songId: String)

    /**
     * Unlike a song (remove like)
     * @param songId ID of the song to unlike
     */
    suspend fun unlikeSong(songId: String)

    /**
     * Get all liked songs for the current user
     * @return List of liked songs
     */
    suspend fun getLikedSongs(): List<Song>

    /**
     * Get recently played songs for the current user
     * @return List of recently played songs
     */
    suspend fun getRecentlyPlayed(): List<Song>
}