package com.melodify.musicapp.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.melodify.musicapp.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // -------- Users --------
    suspend fun createUser(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    suspend fun getUser(userId: String): User? {
        return firestore.collection("users").document(userId).get().await().toObject<User>()
    }

    suspend fun updateUser(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    suspend fun followUser(followerId: String, followingId: String) {
        // define followers
        val followMap = mapOf(
            "followerId" to followerId,
            "followingId" to followingId,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("follows").add(followMap).await()
        // increase number of a user's follower
        firestore.collection("users").document(followingId)
            .update("followersCount", FieldValue.increment(1)).await()
        firestore.collection("users").document(followerId)
            .update("followingCount", FieldValue.increment(1)).await()
    }

    suspend fun unfollowUser(followerId: String, followingId: String) {
        // unfollow
        val query = firestore.collection("follows")
            .whereEqualTo("followerId", followerId)
            .whereEqualTo("followingId", followingId)
        val snapshot = query.get().await()
        snapshot.documents.forEach { it.reference.delete().await() }
        // decrease number of followers
        firestore.collection("users").document(followingId)
            .update("followersCount", FieldValue.increment(-1)).await()
        firestore.collection("users").document(followerId)
            .update("followingCount", FieldValue.increment(-1)).await()
    }

    suspend fun getFollowers(userId: String): List<User> {
        val query = firestore.collection("follows")
            .whereEqualTo("followingId", userId)
        val snapshot = query.get().await()
        val followerIds = snapshot.documents.mapNotNull { it.getString("followerId") }
        return followerIds.mapNotNull { getUser(it) }
    }

    suspend fun getFollowing(userId: String): List<User> {
        val query = firestore.collection("follows")
            .whereEqualTo("followerId", userId)
        val snapshot = query.get().await()
        val followingIds = snapshot.documents.mapNotNull { it.getString("followingId") }
        return followingIds.mapNotNull { getUser(it) }
    }

    // -------- Songs --------
    suspend fun getTrendingSongs(limit: Int = 20): List<Song> {
        return firestore.collection("songs")
            .orderBy("playCount", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject<Song>() }
    }

    suspend fun getLatestSongs(limit: Int = 20): List<Song> {
        return firestore.collection("songs")
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject<Song>() }
    }

    suspend fun getSong(songId: String): Song? {
        return firestore.collection("songs").document(songId).get().await().toObject<Song>()
    }

    suspend fun searchSongs(query: String): List<Song> {
        return firestore.collection("songs")
            .whereArrayContains("searchKeywords", query.lowercase()) // 需预生成关键词
            .get().await()
            .documents.mapNotNull { it.toObject<Song>() }
    }

    // use another way for Paging

    suspend fun likeSong(userId: String, songId: String) {
        val like = mapOf(
            "userId" to userId,
            "songId" to songId,
            "likedAt" to System.currentTimeMillis()
        )
        firestore.collection("likes").add(like).await()
        // increase like counters
    }

    suspend fun unlikeSong(userId: String, songId: String) {
        val query = firestore.collection("likes")
            .whereEqualTo("userId", userId)
            .whereEqualTo("songId", songId)
        val snapshot = query.get().await()
        snapshot.documents.forEach { it.reference.delete().await() }
    }

    suspend fun getLikedSongs(userId: String): List<Song> {
        val query = firestore.collection("likes")
            .whereEqualTo("userId", userId)
        val snapshot = query.get().await()
        val songIds = snapshot.documents.mapNotNull { it.getString("songId") }
        return songIds.mapNotNull { getSong(it) }
    }

    // -------- Playlists --------
    suspend fun getUserPlaylists(userId: String): List<Playlist> {
        return firestore.collection("playlists")
            .whereEqualTo("ownerId", userId)
            .get().await()
            .documents.mapNotNull { it.toObject<Playlist>() }
    }

    suspend fun createPlaylist(playlist: Playlist) {
        firestore.collection("playlists").document(playlist.id).set(playlist).await()
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        firestore.collection("playlists").document(playlist.id).set(playlist).await()
    }

    suspend fun deletePlaylist(playlistId: String) {
        firestore.collection("playlists").document(playlistId).delete().await()
    }

    suspend fun addSongToPlaylist(playlistId: String, songId: String) {
        val playlistSong = mapOf(
            "playlistId" to playlistId,
            "songId" to songId,
            "addedAt" to System.currentTimeMillis()
        )
        firestore.collection("playlistSongs").add(playlistSong).await()
    }

    suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {
        val query = firestore.collection("playlistSongs")
            .whereEqualTo("playlistId", playlistId)
            .whereEqualTo("songId", songId)
        val snapshot = query.get().await()
        snapshot.documents.forEach { it.reference.delete().await() }
    }

    suspend fun getPlaylistSongs(playlistId: String): List<Song> {
        val query = firestore.collection("playlistSongs")
            .whereEqualTo("playlistId", playlistId)
        val snapshot = query.get().await()
        val songIds = snapshot.documents.mapNotNull { it.getString("songId") }
        return songIds.mapNotNull { getSong(it) }
    }

    // -------- Messages / Chat (Real-time) --------
    // send message
    suspend fun sendMessage(message: Message) {
        firestore.collection("messages").document(message.id).set(message).await()
    }

    // get a user's messages Real-time
    fun observeMessages(userId: String, currentUserId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection("messages")
            .whereIn("senderId", listOf(userId, currentUserId))
            .whereIn("receiverId", listOf(userId, currentUserId))
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject<Message>() } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    // mark as read
    suspend fun markMessageAsSeen(messageId: String) {
        firestore.collection("messages").document(messageId)
            .update("isSeen", true).await()
    }
}