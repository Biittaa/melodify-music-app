package com.melodify.musicapp.domain.repository

import androidx.paging.PagingSource
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.SearchFilter
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    suspend fun getTrendingSongs(limit: Int = 20): List<Song>
    suspend fun getLatestSongs(limit: Int = 20): List<Song>
    suspend fun getSong(songId: String): Song
    suspend fun getSongsByIds(songIds: List<String>): List<Song>
    suspend fun searchSongs(query: String): List<Song>
    fun searchSongsPaging(query: String, filter: SearchFilter = SearchFilter.All): PagingSource<Int, Song>
    
    suspend fun likeSong(songId: String)
    suspend fun unlikeSong(songId: String)
    fun getLikedSongs(): Flow<List<Song>>
    fun isLiked(songId: String): Flow<Boolean>

    suspend fun getRecentlyPlayed(): List<Song>
    suspend fun getLocalMusic(): List<Song>
    suspend fun recordSongPlay(songId: String)
    suspend fun getSongsByArtist(artistId: String): List<Song>
}
