package com.melodify.musicapp.data.remote.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.melodify.musicapp.core.common.Constants
import com.melodify.musicapp.domain.model.Message
import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for Firebase Firestore
 * Handles all CRUD operations on collections: users, songs, playlists, messages, likes, follows
 *
 * @property firestore Public instance of FirebaseFirestore (exposed for PagingSource usage)
 */
@Singleton
class FirestoreDataSource @Inject constructor(
    val firestore: FirebaseFirestore
) {

    // ==================== Users ====================

    /**
     * Create a new user document in Firestore
     */
    suspend fun createUser(user: User) {
        firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(user.id)
            .set(user)
            .await()
    }

    /**
     * Get a user document by ID
     * @return User object or null if not found
     */
    suspend fun getUser(userId: String): User? {
        return firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject<User>()
    }

    /**
     * Update an existing user document
     */
    suspend fun updateUser(user: User) {
        firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(user.id)
            .set(user)
            .await()
    }

    /**
     * Create a follow relationship between two users
     */
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

    /**
     * Remove a follow relationship between two users
     */
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

    /**
     * Get list of followers for a specific user
     */
    suspend fun getFollowers(userId: String): List<User> {
        val query = firestore.collection(Constants.FIREBASE_FOLLOWS_COLLECTION)
            .whereEqualTo("followingId", userId)
        val snapshot = query.get().await()
        val followerIds = snapshot.documents.mapNotNull { it.getString("followerId") }
        return followerIds.mapNotNull { getUser(it) }
    }

    /**
     * Get list of users that a specific user is following
     */
    suspend fun getFollowing(userId: String): List<User> {
        val query = firestore.collection(Constants.FIREBASE_FOLLOWS_COLLECTION)
            .whereEqualTo("followerId", userId)
        val snapshot = query.get().await()
        val followingIds = snapshot.documents.mapNotNull { it.getString("followingId") }
        return followingIds.mapNotNull { getUser(it) }
    }

    // ==================== Songs ====================

    /**
     * Get trending songs ordered by play count
     */
    suspend fun getTrendingSongs(limit: Int = 20): List<Song> {
        return firestore.collection(Constants.FIREBASE_SONGS_COLLECTION)
            .orderBy("playCount", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<Song>() }
    }

    /**
     * Get latest songs ordered by added date
     */
    suspend fun getLatestSongs(limit: Int = 20): List<Song> {
        return firestore.collection(Constants.FIREBASE_SONGS_COLLECTION)
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<Song>() }
    }

    /**
     * Get a single song by ID
     */
    suspend fun getSong(songId: String): Song? {
        return firestore.collection(Constants.FIREBASE_SONGS_COLLECTION)
            .document(songId)
            .get()
            .await()
            .toObject<Song>()
    }

    /**
     * Search songs using keyword matching (simple implementation)
     * Note: For production, consider using Algolia or Elasticsearch
     */
    suspend fun searchSongs(query: String): List<Song> {
        return firestore.collection(Constants.FIREBASE_SONGS_COLLECTION)
            .whereArrayContains("searchKeywords", query.lowercase())
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<Song>() }
    }

    /**
     * Like a song for a specific user
     */
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

    /**
     * Remove a like from a song
     */
    suspend fun unlikeSong(userId: String, songId: String) {
        val query = firestore.collection(Constants.FIREBASE_LIKES_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("songId", songId)
        val snapshot = query.get().await()
        snapshot.documents.forEach { it.reference.delete().await() }
    }

    // ==================== Playlists ====================

    /**
     * Get all playlists owned by a user
     */
    suspend fun getUserPlaylists(userId: String): List<Playlist> {
        return firestore.collection(Constants.FIREBASE_PLAYLISTS_COLLECTION)
            .whereEqualTo("ownerId", userId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<Playlist>() }
    }

    /**
     * Create a new playlist document
     */
    suspend fun createPlaylist(playlist: Playlist) {
        firestore.collection(Constants.FIREBASE_PLAYLISTS_COLLECTION)
            .document(playlist.id)
            .set(playlist)
            .await()
    }

    /**
     * Update an existing playlist
     */
    suspend fun updatePlaylist(playlist: Playlist) {
        firestore.collection(Constants.FIREBASE_PLAYLISTS_COLLECTION)
            .document(playlist.id)
            .set(playlist)
            .await()
    }

    /**
     * Delete a playlist by ID
     */
    suspend fun deletePlaylist(playlistId: String) {
        firestore.collection(Constants.FIREBASE_PLAYLISTS_COLLECTION)
            .document(playlistId)
            .delete()
            .await()
    }

    /**
     * Add a song to a playlist
     */
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

    /**
     * Remove a song from a playlist
     */
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {
        val query = firestore.collection(Constants.FIREBASE_PLAYLIST_SONGS_COLLECTION)
            .whereEqualTo("playlistId", playlistId)
            .whereEqualTo("songId", songId)
        val snapshot = query.get().await()
        snapshot.documents.forEach { it.reference.delete().await() }
    }

    /**
     * Get all songs in a playlist
     */
    suspend fun getPlaylistSongs(playlistId: String): List<Song> {
        val query = firestore.collection(Constants.FIREBASE_PLAYLIST_SONGS_COLLECTION)
            .whereEqualTo("playlistId", playlistId)
        val snapshot = query.get().await()
        val songIds = snapshot.documents.mapNotNull { it.getString("songId") }
        return songIds.mapNotNull { getSong(it) }
    }

    // ==================== Messages (Real-time Chat) ====================

    /**
     * Send a message to Firestore
     * Automatically sets participants for efficient querying
     */
    suspend fun sendMessage(message: Message) {
        val messageWithParticipants = message.copy(
            participants = listOf(message.senderId, message.receiverId)
        )
        firestore.collection(Constants.FIREBASE_MESSAGES_COLLECTION)
            .document(message.id)
            .set(messageWithParticipants)
            .await()
    }

    /**
     * Observe messages between two users in real-time using Firestore Snapshots
     * @param userId The other user's ID
     * @param currentUserId The current logged-in user's ID
     * @return Flow emitting list of messages
     */
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

    /**
     * Observe ALL messages involving a specific user in real-time
     * Uses the "participants" array field for efficient querying
     * This is used by ChatSyncService to sync all conversations globally
     * @param userId The current user's ID
     * @return Flow emitting list of all messages involving this user
     */
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

    /**
     * Mark a specific message as seen/read
     */
    suspend fun markMessageAsSeen(messageId: String) {
        firestore.collection(Constants.FIREBASE_MESSAGES_COLLECTION)
            .document(messageId)
            .update("isSeen", true)
            .await()
    }

    /**
     * Get all messages for a user (without real-time) - useful for initial load
     */
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
}