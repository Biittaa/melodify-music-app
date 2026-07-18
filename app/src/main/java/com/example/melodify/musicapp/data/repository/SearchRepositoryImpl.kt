package com.melodify.musicapp.data.repository

import com.melodify.musicapp.data.local.dao.SearchHistoryDao
import com.melodify.musicapp.data.local.entity.SearchHistoryEntity
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Album
import com.melodify.musicapp.domain.model.Artist
import com.melodify.musicapp.domain.model.SearchHistory
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.SearchRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SearchRepository
 * Handles search across songs, artists, albums, users
 * Also manages search history persistence with Room
 */
@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val searchHistoryDao: SearchHistoryDao
) : SearchRepository {

    override suspend fun searchSongs(query: String): List<Song> {
        return firestoreDataSource.searchSongs(query)
    }

    override suspend fun searchArtists(query: String): List<Artist> {
        // TODO: Implement artist search in Firestore
        return emptyList()
    }

    override suspend fun searchAlbums(query: String): List<Album> {
        // TODO: Implement album search in Firestore
        return emptyList()
    }

    override suspend fun searchUsers(query: String): List<User> {
        // TODO: Implement user search in Firestore
        return emptyList()
    }

    override suspend fun saveHistory(query: String) {
        val history = SearchHistoryEntity(
            id = UUID.randomUUID().toString(),
            keyword = query,
            searchedAt = System.currentTimeMillis()
        )
        searchHistoryDao.insert(history)
    }

    override suspend fun clearHistory() {
        searchHistoryDao.clearAll()
    }

    override suspend fun getHistory(): List<SearchHistory> {
        val entities = searchHistoryDao.getAll().firstOrNull() ?: emptyList()
        return entities.map { entity ->
            SearchHistory(
                id = entity.id,
                keyword = entity.keyword,
                searchedAt = entity.searchedAt
            )
        }
    }
}