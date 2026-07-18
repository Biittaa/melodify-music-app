package com.melodify.musicapp.data.repository

import com.melodify.musicapp.core.common.CurrentUserProvider
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
    private val currentUserProvider: CurrentUserProvider
) : PlaylistRepository {

    override suspend fun getUserPlaylists(userId: String): List<Playlist> {
        return firestoreDataSource.getUserPlaylists(userId)
    }

    override suspend fun createPlaylist(name: String) {
        val userId = currentUserProvider.getCurrentUser()?.id ?: return
        val playlist = Playlist(
            id = UUID.randomUUID().toString(),
            title = name,
            description = "",
            coverUrl = "",
            ownerId = userId,
            songsCount = 0,
            isPublic = true
        )
        firestoreDataSource.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        firestoreDataSource.updatePlaylist(playlist)
    }

    override suspend fun deletePlaylist(id: String) {
        firestoreDataSource.deletePlaylist(id)
    }

    override suspend fun addSong(playlistId: String, songId: String) {
        firestoreDataSource.addSongToPlaylist(playlistId, songId)
    }

    override suspend fun removeSong(playlistId: String, songId: String) {
        firestoreDataSource.removeSongFromPlaylist(playlistId, songId)
    }

    override suspend fun getPlaylistSongs(playlistId: String): List<Song> {
        return firestoreDataSource.getPlaylistSongs(playlistId)
    }
}