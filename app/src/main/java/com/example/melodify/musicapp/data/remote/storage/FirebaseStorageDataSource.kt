package com.melodify.musicapp.data.remote.storage

import com.google.firebase.storage.FirebaseStorage
import com.melodify.musicapp.core.common.Constants
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageDataSource @Inject constructor(
    private val storage: FirebaseStorage
) {
    suspend fun uploadProfileImage(userId: String, file: File): String {
        val ref = storage.reference
            .child(Constants.STORAGE_PROFILE_IMAGES)
            .child("$userId.jpg")

        ref.putFile(android.net.Uri.fromFile(file)).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadAlbumCover(albumId: String, file: File): String {
        val ref = storage.reference
            .child(Constants.STORAGE_ALBUM_COVERS)
            .child("$albumId.jpg")

        ref.putFile(android.net.Uri.fromFile(file)).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadPlaylistCover(playlistId: String, file: File): String {
        val ref = storage.reference
            .child(Constants.STORAGE_PLAYLIST_COVERS)
            .child("$playlistId.jpg")

        ref.putFile(android.net.Uri.fromFile(file)).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadSongFile(songId: String, file: File): String {
        val ref = storage.reference
            .child(Constants.STORAGE_SONGS)
            .child("$songId.mp3")

        ref.putFile(android.net.Uri.fromFile(file)).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun deleteFile(path: String) {
        storage.reference.child(path).delete().await()
    }
}