package com.melodify.musicapp.data.repository

import androidx.paging.PagingSource
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.core.common.MockData
import com.melodify.musicapp.data.local.dao.PlaylistSongDao
import com.melodify.musicapp.data.local.dao.PlaylistDao
import com.melodify.musicapp.data.local.entity.PlaylistEntity
import com.melodify.musicapp.data.local.entity.PlaylistSongEntity
import com.melodify.musicapp.data.paging.PlaylistSongsPagingSource
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.PlaylistRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val playlistSongDao: PlaylistSongDao,
    private val playlistDao: PlaylistDao,
    private val currentUserProvider: CurrentUserProvider
) : PlaylistRepository {

    override suspend fun getUserPlaylists(userId: String): List<Playlist> {
        return playlistDao.getAllPlaylists().map {
            Playlist(it.id, it.title, it.description, it.coverUrl, it.ownerId, it.songsCount, it.isPublic)
        }
    }

    override suspend fun createPlaylist(name: String) {
        val userId = currentUserProvider.getCurrentUser()?.id ?: "local_user"
        val playlist = Playlist(
            id = UUID.randomUUID().toString(),
            title = name,
            description = "",
            coverUrl = "",
            ownerId = userId,
            songsCount = 0,
            isPublic = true
        )

        playlistDao.insert(
            PlaylistEntity(playlist.id, playlist.title, playlist.description, playlist.coverUrl, playlist.ownerId, playlist.songsCount, playlist.isPublic)
        )

        try {
            if (userId != "local_user") {
                firestoreDataSource.createPlaylist(playlist)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.update(
            PlaylistEntity(playlist.id, playlist.title, playlist.description, playlist.coverUrl, playlist.ownerId, playlist.songsCount, playlist.isPublic)
        )
        try {
            firestoreDataSource.updatePlaylist(playlist)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deletePlaylist(id: String) {
        playlistDao.deleteById(id)
        try {
            firestoreDataSource.deletePlaylist(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun addSong(playlistId: String, songId: String) {
        val maxPos = playlistSongDao.getMaxPosition(playlistId) ?: -1
        playlistSongDao.insert(
            PlaylistSongEntity(
                playlistId = playlistId,
                songId = songId,
                addedAt = System.currentTimeMillis(),
                position = maxPos + 1
            )
        )
        try {
            firestoreDataSource.addSongToPlaylist(playlistId, songId)
        } catch (e: Exception) { }
        updatePlaylistCover(playlistId)
    }

    override suspend fun addSongs(playlistId: String, songIds: List<String>) {
        var maxPos = playlistSongDao.getMaxPosition(playlistId) ?: -1
        val entities = songIds.map {
            maxPos++
            PlaylistSongEntity(playlistId, it, System.currentTimeMillis(), maxPos)
        }
        playlistSongDao.insertAll(entities)
        
        songIds.forEach { songId ->
            try {
                firestoreDataSource.addSongToPlaylist(playlistId, songId)
            } catch (e: Exception) { }
        }
        updatePlaylistCover(playlistId)
    }

    override suspend fun removeSong(playlistId: String, songId: String) {
        playlistSongDao.delete(PlaylistSongEntity(playlistId, songId, 0))
        try {
            firestoreDataSource.removeSongFromPlaylist(playlistId, songId)
        } catch (e: Exception) { }
        updatePlaylistCover(playlistId)
    }

    override suspend fun updateSongsOrder(playlistId: String, songIds: List<String>) {
        songIds.forEachIndexed { index, songId ->
            playlistSongDao.updatePosition(playlistId, songId, index)
        }
    }

    private suspend fun updatePlaylistCover(playlistId: String) {
        val songs = getPlaylistSongs(playlistId)
        val firstSongWithCover = songs.firstOrNull { it.coverUrl.isNotEmpty() }
        val coverUrl = firstSongWithCover?.coverUrl ?: ""
        
        val localPlaylist = playlistDao.getPlaylistById(playlistId)
        if (localPlaylist != null) {
            val updated = localPlaylist.copy(coverUrl = coverUrl, songsCount = songs.size)
            playlistDao.update(updated)
            
            val domainPlaylist = Playlist(updated.id, updated.title, updated.description, updated.coverUrl, updated.ownerId, updated.songsCount, updated.isPublic)
            try {
                firestoreDataSource.updatePlaylist(domainPlaylist)
            } catch (e: Exception) { }
        }
    }

    override suspend fun getPlaylistSongs(playlistId: String): List<Song> {
        val localRelations = playlistSongDao.getSongsForPlaylist(playlistId)
        if (localRelations.isEmpty()) {
             // Fallback to firestore or mock for specific IDs
             if (playlistId in listOf("i1", "i2", "g1", "g2")) {
                return MockData.songs.shuffled().take(10)
             }
             return try {
                firestoreDataSource.getPlaylistSongs(playlistId)
             } catch (e: Exception) {
                emptyList()
             }
        }
        
        // Fetch full song objects. In a real app, you'd have a SongDao. 
        // For now, we use MockData or local scanner logic via a repository if we had it here.
        // Assuming we can get them from SongRepository. But here we just filter from all available for simplicity.
        val allSongs = MockData.songs // + local scanned songs
        return localRelations.mapNotNull { relation ->
            allSongs.find { it.id == relation.songId }
        }
    }

    override fun getPlaylistSongsPaging(playlistId: String): PagingSource<Int, PlaylistSongEntity> {
        return PlaylistSongsPagingSource(
            playlistSongDao = playlistSongDao,
            playlistId = playlistId
        )
    }
}