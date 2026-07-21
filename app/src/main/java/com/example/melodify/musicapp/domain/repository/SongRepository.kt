package com.melodify.musicapp.domain.repository // Unified Package

import androidx.paging.PagingSource
import com.melodify.musicapp.domain.model.Song

interface SongRepository {
    suspend fun getTrendingSongs(): List<Song>
    suspend fun getLatestSongs(): List<Song>
    suspend fun getSong(songId: String): Song
    suspend fun searchSongs(query: String): List<Song>
    // اضافه کردن پارامتر filter
    fun searchSongsPaging(query: String, filter: SearchFilter = SearchFilter.All): PagingSource<Int, Song>
    suspend fun likeSong(songId: String)
    suspend fun unlikeSong(songId: String)
    suspend fun getLikedSongs(): List<Song>
    suspend fun getRecentlyPlayed(): List<Song>
}