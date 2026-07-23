package com.melodify.musicapp.data.repository

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import com.melodify.musicapp.domain.model.SearchFilter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.combine

// Standard Namespace Imports
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.core.common.MockData
import com.melodify.musicapp.core.common.LocalMusicScanner
import com.melodify.musicapp.data.local.dao.LikedSongDao
import com.melodify.musicapp.data.local.dao.RecentSongDao
import com.melodify.musicapp.data.local.entity.LikedSongEntity
import com.melodify.musicapp.data.local.entity.RecentSongEntity
import com.melodify.musicapp.data.paging.SongSearchPagingSource
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.SongRepository

@Singleton
class SongRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val likedSongDao: LikedSongDao,
    private val recentSongDao: RecentSongDao,
    private val currentUserProvider: CurrentUserProvider,
    private val localMusicScanner: LocalMusicScanner
) : SongRepository {

    private var localSongsCache: List<Song> = emptyList()

    override suspend fun getTrendingSongs(limit: Int): List<Song> {
        val remote = try { firestoreDataSource.getTrendingSongs(limit) } catch (e: Exception) { emptyList<Song>() }
        val local = getLocalSongs().take(limit)
        val combined = (remote + local + MockData.songs).distinctBy { it.id }.take(limit)
        return applyLikeStatus(combined)
    }

    override suspend fun getLatestSongs(limit: Int): List<Song> {
        val remote = try { firestoreDataSource.getLatestSongs(limit) } catch (e: Exception) { emptyList<Song>() }
        val local = getLocalSongs().shuffled().take(limit)
        val combined = (remote + local + MockData.songs.shuffled()).distinctBy { it.id }.take(limit)
        return applyLikeStatus(combined)
    }

    private suspend fun applyLikeStatus(songs: List<Song>): List<Song> {
        return songs.map { song ->
            song.copy(isLiked = likedSongDao.isLiked(song.id))
        }
    }

    private fun getLocalSongs(): List<Song> {
        if (localSongsCache.isEmpty()) {
            val scannedSongs = try {
                localMusicScanner.scanLocalMusic()
            } catch (e: Exception) {
                emptyList<Song>()
            }
            if (scannedSongs.isNotEmpty()) {
                localSongsCache = scannedSongs
            } else {
                return emptyList<Song>()
            }
        }
        return localSongsCache
    }

    override suspend fun getSong(songId: String): Song {
        val song = (getLocalSongs() + MockData.songs).find { it.id == songId }
            ?: try { firestoreDataSource.getSong(songId) } catch (e: Exception) { null }
            ?: throw Exception("Song not found")
        
        return song.copy(isLiked = likedSongDao.isLiked(songId))
    }

    override suspend fun getSongsByIds(songIds: List<String>): List<Song> {
        val allAvailable = getLocalSongs() + MockData.songs
        
        val matched = songIds.mapNotNull { id ->
            allAvailable.find { it.id == id } ?: try {
                firestoreDataSource.getSong(id)
            } catch (e: Exception) {
                null
            }
        }

        return applyLikeStatus(matched)
    }

    override suspend fun searchSongs(query: String): List<Song> {
        val filteredLocal = getLocalSongs().filter { it.title.contains(query, ignoreCase = true) }
        val remote = try { firestoreDataSource.searchSongs(query) } catch (e: Exception) { emptyList<Song>() }
        val mock = MockData.songs.filter { it.title.contains(query, ignoreCase = true) }
        val combined = (filteredLocal + remote + mock).distinctBy { it.id }
        return applyLikeStatus(combined)
    }

    override fun searchSongsPaging(query: String, filter: SearchFilter): PagingSource<Int, Song> {
        return SongSearchPagingSource(
            songRepository = this,
            query = query,
            filter = filter
        )
    }

    override suspend fun likeSong(songId: String) {
        val userId = currentUserProvider.getCurrentUser()?.id
        if (userId != null) {
            try { firestoreDataSource.likeSong(userId, songId) } catch (e: Exception) {}
        }
        
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
        val userId = currentUserProvider.getCurrentUser()?.id
        if (userId != null) {
            try { firestoreDataSource.unlikeSong(userId, songId) } catch (e: Exception) {}
        }
        
        likedSongDao.delete(LikedSongEntity(songId, "", "", "", "", "", 0, "", 0, 0))
    }

    override fun getLikedSongs(): Flow<List<Song>> {
        return likedSongDao.getAll().map { entities ->
            entities.map { entity ->
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
    }

    override fun isLiked(songId: String): Flow<Boolean> {
        return likedSongDao.isLikedFlow(songId)
    }

    override suspend fun getRecentlyPlayed(): List<Song> {
        val entities = recentSongDao.getAllRecentSongs().first()
        val songs = entities.map { entity ->
            Song(
                id = entity.songId,
                title = entity.title,
                artistId = entity.artistId,
                albumId = entity.albumId,
                coverUrl = entity.coverUrl,
                audioUrl = entity.audioUrl,
                duration = entity.duration,
                genre = "",
                playCount = 0,
                isLiked = false,
                isDownloaded = false
            )
        }
        return applyLikeStatus(songs)
    }

    override suspend fun getLocalMusic(): List<Song> {
        return applyLikeStatus(getLocalSongs())
    }

    override suspend fun recordSongPlay(songId: String) {
        try {
            val song = getSong(songId)
            recentSongDao.insertRecentSong(
                RecentSongEntity(
                    songId = song.id,
                    title = song.title,
                    artistId = song.artistId,
                    albumId = song.albumId,
                    coverUrl = song.coverUrl,
                    audioUrl = song.audioUrl,
                    duration = song.duration,
                    playedAt = System.currentTimeMillis()
                )
            )
            val userId = currentUserProvider.getCurrentUser()?.id
            if (userId != null) {
                firestoreDataSource.recordSongPlay(userId, songId)
            }
        } catch (e: Exception) {}
    }

    override suspend fun getSongsByArtist(artistId: String): List<Song> {
        val remote = try { firestoreDataSource.getSongsByArtist(artistId) } catch (e: Exception) { emptyList<Song>() }
        val local = getLocalSongs().filter { it.artistId == artistId }
        val mock = MockData.songs.filter { it.artistId == artistId || it.artistId.contains(artistId) }.shuffled().take(5)
        val combined = (remote + local + mock).distinctBy { it.id }
        return applyLikeStatus(combined)
    }
    override suspend fun deleteRecentSong(songId: String) {
        recentSongDao.deleteRecentSong(songId)
    }
}
