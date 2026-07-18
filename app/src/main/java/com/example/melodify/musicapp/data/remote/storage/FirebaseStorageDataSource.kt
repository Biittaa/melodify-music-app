package com.melodify.musicapp.data.remote.storage

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageDataSource @Inject constructor(
    private val storage: FirebaseStorage
) {
    suspend fun uploadProfileImage(userId: String, file: File): String {
        val ref = storage.reference.child("profile_images/$userId.jpg")
        ref.putFile(android.net.Uri.fromFile(file)).await()
        return ref.downloadUrl.await().toString()
    }

    // music uploading
    suspend fun uploadSong(songId: String, file: File): String {
        val ref = storage.reference.child("songs/$songId.mp3")
        ref.putFile(android.net.Uri.fromFile(file)).await()
        return ref.downloadUrl.await().toString()
    }
}