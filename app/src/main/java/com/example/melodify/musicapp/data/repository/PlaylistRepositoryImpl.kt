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
import com.melodify.musicapp.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val playlistSongDao: PlaylistSongDao,
    private val playlistDao: PlaylistDao,
    private val currentUserProvider: CurrentUserProvider,
    private val songRepository: SongRepository
) : PlaylistRepository {

    // System playlist definitions
    private val systemPlaylists = mapOf(
        "i1" to Playlist("i1", "Persian Pop", "Collection of Persian pop", "", "system", 10, true),
        "i2" to Playlist("i2", "Traditional", "Iranian classical music", "", "system", 8, true),
        "g1" to Playlist("g1", "Global Top 50", "World's most played", "", "system", 50, true),
        "g2" to Playlist("g2", "Rock Classics", "Best of Rock", "", "system", 30, true)
    )

    private val systemSongs = mapOf(
        "i1" to MockData.songs.filter { it.genre.contains("Pop", ignoreCase = true) }.take(10),
        "i2" to MockData.songs.filter { it.genre.contains("Classical", ignoreCase = true) }.take(8),
        "g1" to MockData.songs.take(50),
        "g2" to MockData.songs.filter { it.genre.contains("Rock", ignoreCase = true) }.take(30)
    )

    // This function is suspend and will be called from other suspend functions
    private suspend fun createSystemPlaylistsIfNeeded() {
        systemPlaylists.forEach { (id, playlist) ->
            val existing = playlistDao.getPlaylistById(id)
            if (existing == null) {
                // Insert playlist
                playlistDao.insert(
                    PlaylistEntity(
                        id = playlist.id,
                        title = playlist.title,
                        description = playlist.description,
                        coverUrl = playlist.coverUrl,
                        ownerId = playlist.ownerId,
                        songsCount = playlist.songsCount,
                        isPublic = playlist.isPublic
                    )
                )
                // Insert its songs
                val songs = systemSongs[id] ?: emptyList()
                val entities = songs.mapIndexed { index, song ->
                    PlaylistSongEntity(
                        playlistId = id,
                        songId = song.id,
                        addedAt = System.currentTimeMillis() - (songs.size - index) * 1000,
                        position = index
                    )
                }
                playlistSongDao.insertAll(entities)
            }
        }
    }

    // Reactive flow – ensures system playlists are created before emitting
    override fun getUserPlaylistsFlow(userId: String): Flow<List<Playlist>> = flow {
        createSystemPlaylistsIfNeeded()
        playlistDao.getAllPlaylistsFlow().collect { entities ->
            emit(entities.map {
                Playlist(it.id, it.title, it.description, it.coverUrl, it.ownerId, it.songsCount, it.isPublic)
            })
        }
    }

    // One‑time fetch – also creates system playlists
    override suspend fun getUserPlaylists(userId: String): List<Playlist> {
        createSystemPlaylistsIfNeeded()
        return playlistDao.getAllPlaylists().map {
            Playlist(it.id, it.title, it.description, it.coverUrl, it.ownerId, it.songsCount, it.isPublic)
        }
    }

    // -------- CRUD operations (unchanged) --------

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
        } catch (_: Exception) { }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.update(
            PlaylistEntity(playlist.id, playlist.title, playlist.description, playlist.coverUrl, playlist.ownerId, playlist.songsCount, playlist.isPublic)
        )
        try {
            firestoreDataSource.updatePlaylist(playlist)
        } catch (_: Exception) { }
    }

    override suspend fun deletePlaylist(id: String) {
        playlistDao.deleteById(id)
        try {
            firestoreDataSource.deletePlaylist(id)
        } catch (_: Exception) { }
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
        } catch (_: Exception) { }
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
            } catch (_: Exception) { }
        }
        updatePlaylistCover(playlistId)
    }

    override suspend fun removeSong(playlistId: String, songId: String) {
        playlistSongDao.delete(PlaylistSongEntity(playlistId, songId, 0))
        try {
            firestoreDataSource.removeSongFromPlaylist(playlistId, songId)
        } catch (_: Exception) { }
        updatePlaylistCover(playlistId)
    }

    override suspend fun updateSongsOrder(playlistId: String, songIds: List<String>) {
        songIds.forEachIndexed { index, songId ->
            playlistSongDao.updatePosition(playlistId, songId, index)
        }
    }

    override suspend fun getPlaylistSongs(playlistId: String): List<Song> {
        // 1. Try Room
        val localRelations = playlistSongDao.getSongsForPlaylist(playlistId)
        if (localRelations.isNotEmpty()) {
            return localRelations.mapNotNull { relation ->
                runCatching { songRepository.getSong(relation.songId) }.getOrNull()
            }
        }

        // 2. If system playlist, create it and recurse
        if (systemPlaylists.containsKey(playlistId)) {
            createSystemPlaylistsIfNeeded()
            return getPlaylistSongs(playlistId) // after insertion, fetch again
        }

        // 3. Fallback to Firestore
        return try {
            firestoreDataSource.getPlaylistSongs(playlistId)
        } catch (_: Exception) {
            emptyList()
        }
    }

    override fun getPlaylistSongsPaging(playlistId: String): PagingSource<Int, PlaylistSongEntity> {
        return PlaylistSongsPagingSource(
            playlistSongDao = playlistSongDao,
            playlistId = playlistId
        )
    }

    private suspend fun updatePlaylistCover(playlistId: String) {
        val songs = getPlaylistSongs(playlistId)
        val coverUrl = songs.firstOrNull { it.coverUrl.isNotEmpty() }?.coverUrl ?: ""
        val count = songs.size

        val localPlaylist = playlistDao.getPlaylistById(playlistId)
        if (localPlaylist != null) {
            val updated = localPlaylist.copy(coverUrl = coverUrl, songsCount = count)
            playlistDao.update(updated)

            val domainPlaylist = Playlist(
                updated.id, updated.title, updated.description,
                updated.coverUrl, updated.ownerId, updated.songsCount, updated.isPublic
            )
            try {
                firestoreDataSource.updatePlaylist(domainPlaylist)
            } catch (_: Exception) { }
        }
    }
}