package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Song

interface SongRepository {
    suspend fun getTrendingSongs(): List<Song>
    suspend fun getLatestSongs(): List<Song>
    suspend fun getSong(songId: String): Song
    suspend fun searchSongs(query: String): List<Song>  // برای Paging از متد جداگانه استفاده می‌شود
    suspend fun likeSong(songId: String)
    suspend fun unlikeSong(songId: String)
    suspend fun getLikedSongs(): List<Song>
    suspend fun getRecentlyPlayed(): List<Song>
}