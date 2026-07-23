package com.melodify.musicapp.data.remote.firestore

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Standard Namespace Imports
import com.melodify.musicapp.core.common.Constants
import com.melodify.musicapp.domain.model.Message
import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.User

@Singleton
class FirestoreDataSource @Inject constructor(
    val firestore: FirebaseFirestore
) {

    // ==================== Users ====================

//    suspend fun createUser(user: User) {
//        firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
//            .document(user.id)
//            .set(user)
//            .await()
//    }

    suspend fun getUser(userId: String): User? {
        return firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject<User>()
    }

//    suspend fun searchUsers(query:String):List<User>{
//
//        val q = query.lowercase()
//
//        val snapshot =
//            firestore.collection("users")
//                .whereGreaterThanOrEqualTo(
//                    "usernameSearch",
//                    q
//                )
//                .whereLessThanOrEqualTo(
//                    "usernameSearch",
//                    q+"\uf8ff"
//                )
//                .get()
//                .await()
//
//
//        return snapshot.documents.mapNotNull {
//            it.toObject(User::class.java)
//        }
//    }


    suspend fun searchSongs(query:String):List<Song>{

        val snapshot =
            firestore.collection("songs")
                .get()
                .await()

        return snapshot.documents.mapNotNull {
            it.toObject<Song>()
        }
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

    suspend fun getSongsByArtist(artistId: String): List<Song> {
        return firestore.collection(Constants.FIREBASE_SONGS_COLLECTION)
            .whereEqualTo("artistId", artistId)
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

    suspend fun recordSongPlay(userId: String, songId: String) {
        val playData = mapOf(
            "userId" to userId,
            "songId" to songId,
            "playedAt" to FieldValue.serverTimestamp()
        )
        firestore.collection("recently_played")
            .add(playData)
            .await()
            
        // Also increment playCount on the song
        firestore.collection(Constants.FIREBASE_SONGS_COLLECTION)
            .document(songId)
            .update("playCount", FieldValue.increment(1))
            .await()
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

    // ==================== Messages (Real-time Chat) ====================

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

//    suspend fun searchUsers(query: String): List<User> {
//        if (query.isBlank()) return emptyList()
//
//        return try {
//            // دریافت تمامی کاربران از کالکشن users
//            val snapshot = firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
//                .get()
//                .await()
//
//            // فیلتر کردن کاربران در سمت کلاینت (بدون حساسیت به حروف بزرگ/کوچک)
//            snapshot.documents.mapNotNull { doc ->
//                doc.toObject<User>()
//            }.filter { user ->
//                user.username.contains(query, ignoreCase = true) ||
//                        user.fullName.contains(query, ignoreCase = true) ||
//                        user.email.contains(query, ignoreCase = true)
//            }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }

    // 📁 app/src/main/java/com/example/melodify/musicapp/data/remote/firestore/FirestoreDataSource.kt

//    suspend fun searchUsers(query: String): List<User> {
//        val q = query.trim().lowercase()
//        if (q.isEmpty()) return emptyList()
//
//        return try {
//            // ۱. دریافت همه داکیومنت‌های کالکشن کاربران از فایربیس
//            val snapshot = firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
//                .get()
//                .await()
//
//            // ۲. فیلتر کردن کاربران بر اساس نام کاربری یا نام کامل (Case-Insensitive)
//            snapshot.documents
//                .mapNotNull { it.toObject<User>() }
//                .filter { user ->
//                    user.username.lowercase().contains(q) ||
//                            user.fullName.lowercase().contains(q)
//                }
//        } catch (e: Exception) {
//            Log.e("FirestoreDataSource", "Error searching users", e)
//            emptyList()
//        }
//    }

    // 📁 app/src/main/java/com/example/melodify/musicapp/data/remote/firestore/FirestoreDataSource.kt

    suspend fun createUser(user: User) {
        val userMap = mapOf(
            "id" to user.id,
            "username" to user.username,
            "usernameSearch" to user.username.lowercase(),
            "fullName" to user.fullName,
            "fullNameSearch" to user.fullName.lowercase(),
            "email" to user.email,
            "profileImage" to user.profileImage,
            "bio" to user.bio,
            "followersCount" to user.followersCount,
            "followingCount" to user.followingCount,
            "playlistsCount" to user.playlistsCount,
            "isPremium" to user.isPremium
        )

        firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .document(user.id)
            .set(userMap)
            .await()
    }



    suspend fun searchUsers(query: String): List<User> {
        if (query.isBlank()) return emptyList()

        val q = query.trim().lowercase()

        return try {
            // دریافت کاربران از کالکشن users
            val snapshot = firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    // تبدیل به مدل User به همراه مقداردهی ID
                    val user = doc.toObject(User::class.java)
                    user?.copy(id = doc.id)
                } catch (e: Exception) {
                    null
                }
            }.filter { user ->
                // فیلتر کردن بر اساس نام کاربری یا نام کامل در سمت کلاینت
                user.username.lowercase().contains(q) ||
                        user.fullName.lowercase().contains(q) ||
                        user.email.lowercase().contains(q)
            }
        } catch (e: Exception) {
            Log.e("FirestoreDataSource", "Error searching users in Firebase", e)
            emptyList()
        }
    }
}
