package com.melodify.musicapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.melodify.musicapp.data.local.entity.PlaylistSongEntity
import com.melodify.musicapp.data.local.model.SongWithPlaylistInfo

/**
 * Data Access Object for Playlist-Song relationships
 * Handles CRUD operations for songs within playlists
 */
@Dao
interface PlaylistSongDao {

    /**
     * Insert a list of songs into a playlist
     * Replaces existing entries on conflict
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<PlaylistSongEntity>)

    /**
     * Insert a single song into a playlist
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: PlaylistSongEntity)

    /**
     * Remove a specific song from a playlist
     */
    @Delete
    suspend fun delete(song: PlaylistSongEntity)

    /**
     * Get all songs for a specific playlist (for standard Flow usage)
     */
    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY addedAt DESC")
    fun getSongsForPlaylist(playlistId: String): List<PlaylistSongEntity>

    /**
     * Get paginated songs for a specific playlist (for Paging3)
     * Uses OFFSET and LIMIT for numeric pagination
     */
    @Transaction
    @Query("""
    SELECT * FROM playlist_songs 
    WHERE playlistId = :playlistId 
    ORDER BY addedAt DESC
""")
    fun getPlaylistSongsPaged(playlistId: String): PagingSource<Int, SongWithPlaylistInfo>

    /**
     * Delete all songs from a specific playlist (useful for clearing)
     */
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun deleteAllForPlaylist(playlistId: String)
}