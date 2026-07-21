package com.melodify.musicapp.data.remote.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.melodify.musicapp.core.common.Constants
import com.melodify.musicapp.domain.model.Message
import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    val firestore: FirebaseFirestore
) {

    // ==================== Users ====================

    suspend fun createUser(user: User) {
        firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(user.id)
            .set(user)
            .await()
    }

    suspend fun getUser(userId: String): User? {
        return firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject<User>()
    }

    suspend fun updateUser(user: User) {
        firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(user.id)
            .set(user)
            .await()
    }

    suspend fun followUser(followerId: String, followingId: String) {
        val followData = mapOf(
            "followerId" to followerId,
            "followingId" to followingId,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection(Constants.FIREBASE_FOLLOWS_COLLECTION)
            .add(followData)
            .await()

        firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(followingId)
            .update("followersCount", FieldValue.increment(1))
            .await()

        firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(followerId)
            .update("followingCount", FieldValue.increment(1))
            .await()
    }

    suspend fun unfollowUser(followerId: String, followingId: String) {
        val query = firestore.collection(Constants.FIREBASE_FOLLOWS_COLLECTION)
            .whereEqualTo("followerId", followerId)
            .whereEqualTo("followingId", followingId)
        val snapshot = query.get().await()
        snapshot.documents.forEach { it.reference.delete().await() }

        firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(followingId)
            .update("followersCount", FieldValue.increment(-1))
            .await()

        firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(followerId)
            .update("followingCount", FieldValue.increment(-1))
            .await()
    }

    suspend fun getFollowers(userId: String): List<User> {
        val query = firestore.collection(Constants.FIREBASE_FOLLOWS_COLLECTION)
            .whereEqualTo("followingId", userId)
        val snapshot = query.get().await()
        val followerIds = snapshot.documents.mapNotNull { it.getString("followerId") }
        return followerIds.mapNotNull { getUser(it) }
    }

    suspend fun getFollowing(userId: String): List<User> {
        val query = firestore.collection(Constants.FIREBASE_FOLLOWS_COLLECTION)
            .whereEqualTo("followerId", userId)
        val snapshot = query.get().await()
        val followingIds = snapshot.documents.mapNotNull { it.getString("followingId") }
        return followingIds.mapNotNull { getUser(it) }
    }

    // ==================== Songs ====================

    suspend fun getTrendingSongs(limit: Int = 20): List<Song> {
        return firestore.collection(Constants.FIREBASE_SONGS_COLLECTION)
            .orderBy("playCount", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<Song>() }
    }

    suspend fun getLatestSongs(limit: Int = 20): List<Song> {
        return firestore.collection(Constants.FIREBASE_SONGS_COLLECTION)
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<Song>() }
    }

    suspend fun getSong(songId: String): Song? {
        return firestore.collection(Constants.FIREBASE_SONGS_COLLECTION)
            .document(songId)
            .get()
            .await()
            .toObject<Song>()
    }

    suspend fun searchSongs(query: String): List<Song> {
        return firestore.collection(Constants.FIREBASE_SONGS_COLLECTION)
            .whereArrayContains("searchKeywords", query.lowercase())
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<Song>() }
    }

    suspend fun likeSong(userId: String, songId: String) {
        val likeData = mapOf(
            "userId" to userId,
            "songId" to songId,
            "likedAt" to System.currentTimeMillis()
        )
        firestore.collection(Constants.FIREBASE_LIKES_COLLECTION)
            .add(likeData)
            .await()
    }

    suspend fun unlikeSong(userId: String, songId: String) {
        val query = firestore.collection(Constants.FIREBASE_LIKES_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("songId", songId)
        val snapshot = query.get().await()
        snapshot.documents.forEach { it.reference.delete().await() }
    }

    // ==================== Playlists ====================

    suspend fun getUserPlaylists(userId: String): List<Playlist> {
        return firestore.collection(Constants.FIREBASE_PLAYLISTS_COLLECTION)
            .whereEqualTo("ownerId", userId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<Playlist>() }
    }

    suspend fun createPlaylist(playlist: Playlist) {
        firestore.collection(Constants.FIREBASE_PLAYLISTS_COLLECTION)
            .document(playlist.id)
            .set(playlist)
            .await()
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        firestore.collection(Constants.FIREBASE_PLAYLISTS_COLLECTION)
            .document(playlist.id)
            .set(playlist)
            .await()
    }

    suspend fun deletePlaylist(playlistId: String) {
        firestore.collection(Constants.FIREBASE_PLAYLISTS_COLLECTION)
            .document(playlistId)
            .delete()
            .await()
    }

    suspend fun addSongToPlaylist(playlistId: String, songId: String) {
        val data = mapOf(
            "playlistId" to playlistId,
            "songId" to songId,
            "addedAt" to System.currentTimeMillis()
        )
        firestore.collection(Constants.FIREBASE_PLAYLIST_SONGS_COLLECTION)
            .add(data)
            .await()
    }

    suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {
        val query = firestore.collection(Constants.FIREBASE_PLAYLIST_SONGS_COLLECTION)
            .whereEqualTo("playlistId", playlistId)
            .whereEqualTo("songId", songId)
        val snapshot = query.get().await()
        snapshot.documents.forEach { it.reference.delete().await() }
    }

    suspend fun getPlaylistSongs(playlistId: String): List<Song> {
        val query = firestore.collection(Constants.FIREBASE_PLAYLIST_SONGS_COLLECTION)
            .whereEqualTo("playlistId", playlistId)
        val snapshot = query.get().await()
        val songIds = snapshot.documents.mapNotNull { it.getString("songId") }
        return songIds.mapNotNull { getSong(it) }
    }

    // ==================== Messages & Social Direct Message ====================

    suspend fun sendMessage(message: Message) {
        val messageWithParticipants = message.copy(
            participants = listOf(message.senderId, message.receiverId)
        )
        firestore.collection(Constants.FIREBASE_MESSAGES_COLLECTION)
            .document(message.id)
            .set(messageWithParticipants)
            .await()
    }

    fun observeMessages(userId: String, currentUserId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection(Constants.FIREBASE_MESSAGES_COLLECTION)
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

    fun observeAllMessagesForUser(userId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection(Constants.FIREBASE_MESSAGES_COLLECTION)
            .whereArrayContains("participants", userId)
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

    suspend fun markMessageAsSeen(messageId: String) {
        firestore.collection(Constants.FIREBASE_MESSAGES_COLLECTION)
            .document(messageId)
            .update("isSeen", true)
            .await()
    }

    suspend fun getMessagesForUser(userId: String, currentUserId: String): List<Message> {
        return firestore.collection(Constants.FIREBASE_MESSAGES_COLLECTION)
            .whereIn("senderId", listOf(userId, currentUserId))
            .whereIn("receiverId", listOf(userId, currentUserId))
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<Message>() }
    }

    // ==================== Real-time Typing Status Indicator ====================

    fun observeTypingStatus(userId: String, otherUserId: String): Flow<Boolean> = callbackFlow {
        val docId = if (userId < otherUserId) "${userId}_$otherUserId" else "${otherUserId}_$userId"
        val listener = firestore.collection("typing_status")
            .document(docId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val isTyping = snapshot?.getBoolean(otherUserId) ?: false
                trySend(isTyping)
            }
        awaitClose { listener.remove() }
    }

    suspend fun setTypingStatus(userId: String, otherUserId: String, isTyping: Boolean) {
        val docId = if (userId < otherUserId) "${userId}_$otherUserId" else "${otherUserId}_$userId"
        firestore.collection("typing_status")
            .document(docId)
            .set(mapOf(userId to isTyping), com.google.firebase.firestore.SetOptions.merge())
            .await()
    }
}