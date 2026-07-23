package com.melodify.musicapp.data.local.dao

import androidx.room.*
import com.melodify.musicapp.data.local.entity.RecentSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSongDao {
    @Query("SELECT * FROM recent_songs ORDER BY playedAt DESC LIMIT 20")
    fun getAllRecentSongs(): Flow<List<RecentSongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSong(song: RecentSongEntity)

    @Query("DELETE FROM recent_songs WHERE songId = :songId")
    suspend fun deleteRecentSong(songId: String)
}

