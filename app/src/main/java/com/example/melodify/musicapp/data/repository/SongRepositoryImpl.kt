package com.melodify.musicapp.data.repository

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton
import com.melodify.musicapp.domain.model.SearchFilter

// Standard Namespace Imports
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.core.common.MockData
import com.melodify.musicapp.core.common.LocalMusicScanner
import com.melodify.musicapp.data.local.dao.LikedSongDao
import com.melodify.musicapp.data.local.entity.LikedSongEntity
import com.melodify.musicapp.data.paging.SongSearchPagingSource
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.SongRepository

@Singleton
class SongRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val likedSongDao: LikedSongDao,
    private val currentUserProvider: CurrentUserProvider,
    private val localMusicScanner: LocalMusicScanner
) : SongRepository {

    private var localSongsCache: List<Song> = emptyList()

    override suspend fun getTrendingSongs(): List<Song> {
        val remote = try { firestoreDataSource.getTrendingSongs() } catch (e: Exception) { emptyList() }
        val local = getLocalSongs().take(10)
        return (remote + local + MockData.songs.take(20)).distinctBy { it.id }
    }

    override suspend fun getLatestSongs(): List<Song> {
        val remote = try { firestoreDataSource.getLatestSongs() } catch (e: Exception) { emptyList() }
        val local = getLocalSongs().shuffled().take(10)
        return (remote + local + MockData.songs.shuffled().take(20)).distinctBy { it.id }
    }

    private fun getLocalSongs(): List<Song> {
        if (localSongsCache.isEmpty()) {
            val scannedSongs = try {
                localMusicScanner.scanLocalMusic()
            } catch (e: Exception) {
                emptyList()
            }
            if (scannedSongs.isNotEmpty()) {
                localSongsCache = scannedSongs
            } else {
                return emptyList()
            }
        }
        return localSongsCache
    }

    override suspend fun getSong(songId: String): Song {
        val all = getTrendingSongs() + getLatestSongs() + getLocalSongs()
        return all.find { it.id == songId }
            ?: firestoreDataSource.getSong(songId)
            ?: MockData.songs.find { it.id == songId }
            ?: throw Exception("Song not found")
    }

    override suspend fun searchSongs(query: String): List<Song> {
        val filteredLocal = getLocalSongs().filter { it.title.contains(query, ignoreCase = true) }
        val remote = try { firestoreDataSource.searchSongs(query) } catch (e: Exception) { emptyList() }
        val mock = MockData.songs.filter { it.title.contains(query, ignoreCase = true) }
        return (filteredLocal + remote + mock).distinctBy { it.id }
    }

    override fun searchSongsPaging(query: String, filter: SearchFilter): PagingSource<Int, Song> {
        return SongSearchPagingSource(
            songRepository = this,
            query = query,
            filter = filter
        )
    }

    override suspend fun likeSong(songId: String) {
        val userId = currentUserProvider.getCurrentUser()?.id ?: return
        try { firestoreDataSource.likeSong(userId, songId) } catch (e: Exception) {}
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
        try { firestoreDataSource.unlikeSong(userId, songId) } catch (e: Exception) {}
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
        return MockData.songs.shuffled().take(10)
    }
    override suspend fun getLocalMusic(): List<Song> {
        return getLocalSongs() // This uses the existing private helper function
    }
}