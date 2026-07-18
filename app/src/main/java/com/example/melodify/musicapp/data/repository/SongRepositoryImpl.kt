package com.melodify.musicapp.data.repository

import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.data.local.dao.LikedSongDao
import com.melodify.musicapp.data.local.entity.LikedSongEntity
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.SongRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val likedSongDao: LikedSongDao,
    private val currentUserProvider: CurrentUserProvider
) : SongRepository {

    override suspend fun getTrendingSongs(): List<Song> {
        return firestoreDataSource.getTrendingSongs()
    }

    override suspend fun getLatestSongs(): List<Song> {
        return firestoreDataSource.getLatestSongs()
    }

    override suspend fun getSong(songId: String): Song {
        return firestoreDataSource.getSong(songId) ?: throw Exception("Song not found")
    }

    override suspend fun searchSongs(query: String): List<Song> {
        return firestoreDataSource.searchSongs(query)
    }

    override suspend fun likeSong(songId: String) {
        val userId = currentUserProvider.getCurrentUser()?.id ?: return
        // 1- send to Firebase
        firestoreDataSource.likeSong(userId, songId)
        // 2- save in Room (offline)
        val song = getSong(songId)
        likedSongDao.insert(
            LikedSongEntity(
                songId = song.id,
                title = song.title,
                artistId = song.artistId,
                albumId = song.albumId,
                coverUrl = song.coverUrl,
                audioUrl = song.audioUrl,
                duration = song.duration,
                genre = song.genre,
                playCount = song.playCount,
                likedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun unlikeSong(songId: String) {
        val userId = currentUserProvider.getCurrentUser()?.id ?: return
        // 1- remove from Firebase
        firestoreDataSource.unlikeSong(userId, songId)
        // 2- remove from Room
        likedSongDao.delete(LikedSongEntity(songId, "", "", "", "", "", 0, "", 0, 0))
    }

    override suspend fun getLikedSongs(): List<Song> {
        val entities = likedSongDao.getAll().firstOrNull() ?: emptyList()
        return entities.map { entity ->
            Song(
                id = entity.songId,
                title = entity.title,
                artistId = entity.artistId,
                albumId = entity.albumId,
                coverUrl = entity.coverUrl,
                audioUrl = entity.audioUrl,
                duration = entity.duration,
                genre = entity.genre,
                playCount = entity.playCount,
                isLiked = true,
                isDownloaded = false
            )
        }
    }

    override suspend fun getRecentlyPlayed(): List<Song> {
        // optional
        return emptyList()
    }
}