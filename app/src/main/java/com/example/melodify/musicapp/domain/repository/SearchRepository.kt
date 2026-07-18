package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Album
import com.melodify.musicapp.domain.model.Artist
import com.melodify.musicapp.domain.model.SearchHistory
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.User

interface SearchRepository {
    suspend fun searchSongs(query: String): List<Song>      // برای Paging
    suspend fun searchArtists(query: String): List<Artist>
    suspend fun searchAlbums(query: String): List<Album>
    suspend fun searchUsers(query: String): List<User>
    suspend fun saveHistory(query: String)
    suspend fun clearHistory()
    suspend fun getHistory(): List<SearchHistory>
}