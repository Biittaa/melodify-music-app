package com.melodify.musicapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.melodify.musicapp.data.local.entity.PlaylistSongEntity

@Dao
interface PlaylistSongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<PlaylistSongEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: PlaylistSongEntity)

    @Delete
    suspend fun delete(song: PlaylistSongEntity)

    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY `position` ASC, addedAt DESC")
    suspend fun getSongsForPlaylist(playlistId: String): List<PlaylistSongEntity>

    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY `position` ASC, addedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getPlaylistSongsPaged(playlistId: String, limit: Int, offset: Int): List<PlaylistSongEntity>

    @Query("SELECT MAX(`position`) FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun getMaxPosition(playlistId: String): Int?

    @Query("UPDATE playlist_songs SET `position` = :newPosition WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun updatePosition(playlistId: String, songId: String, newPosition: Int)

    @Transaction
    suspend fun moveSong(playlistId: String, fromPosition: Int, toPosition: Int) {
        // Simple implementation for moving
    }

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun deleteAllForPlaylist(playlistId: String)
}