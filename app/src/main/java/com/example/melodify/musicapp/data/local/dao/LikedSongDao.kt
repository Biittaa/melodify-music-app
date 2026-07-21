package com.melodify.musicapp.data.local.dao

import androidx.room.*
import com.melodify.musicapp.data.local.entity.LikedSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: LikedSongEntity)

    @Delete
    suspend fun delete(song: LikedSongEntity)

    @Query("SELECT * FROM liked_songs ORDER BY likedAt DESC")
    fun getAll(): Flow<List<LikedSongEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM liked_songs WHERE songId = :songId)")
    suspend fun isLiked(songId: String): Boolean
}