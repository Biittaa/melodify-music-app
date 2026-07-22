//package com.melodify.musicapp.data.repository
//
//import android.util.Log
//import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
//import com.melodify.musicapp.domain.model.User
//import com.melodify.musicapp.domain.repository.UserRepository
//import com.melodify.musicapp.core.common.CurrentUserProvider
//import com.melodify.musicapp.core.common.MockData
//import com.melodify.musicapp.core.common.UserMockData
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class UserRepositoryImpl @Inject constructor(
//    private val firestoreDataSource: FirestoreDataSource,
//    private val currentUserProvider: CurrentUserProvider,
//    private val inMemoryUserStore: InMemoryUserStore
//) : UserRepository {
//
////    override suspend fun getProfile(userId: String): User {
////        return firestoreDataSource.getUser(userId) ?: throw Exception("User not found")
////    }
//
//    override suspend fun updateProfile(user: User) {
//        firestoreDataSource.updateUser(user)
//    }
//
////    override suspend fun follow(userId: String) {
////        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
////        firestoreDataSource.followUser(currentUserId, userId)
////    }
////
////    override suspend fun unfollow(userId: String) {
////        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
////        firestoreDataSource.unfollowUser(currentUserId, userId)
////    }
//
//
//    override suspend fun getUserFollowers(userId: String): List<User> {
//        return firestoreDataSource.getFollowers(userId)
//    }
//
//    override suspend fun getUserFollowing(userId: String): List<User> {
//        return firestoreDataSource.getFollowing(userId)
//    }
//
////    override suspend fun searchUsers(query: String): List<User> {
////        return firestoreDataSource.searchUsers(query)
////    }
//override suspend fun follow(userId: String) {
//    val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
//    try {
//        // فالو کردن در Firestore
//        firestoreDataSource.followUser(currentUserId, userId)
//        Log.d("UserRepository", "✅ Successfully followed user: $userId")
//    } catch (e: Exception) {
//        Log.e("UserRepository", "❌ Error following user: ${e.message}")
//        throw e
//    }
//}
//
//    override suspend fun unfollow(userId: String) {
//        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
//        try {
//            // آنفالو کردن در Firestore
//            firestoreDataSource.unfollowUser(currentUserId, userId)
//            Log.d("UserRepository", "✅ Successfully unfollowed user: $userId")
//        } catch (e: Exception) {
//            Log.e("UserRepository", "❌ Error unfollowing user: ${e.message}")
//            throw e
//        }
//    }
//
//    override suspend fun searchUsers(query: String): List<User> {
//
//        val local = emptyList<User>() // اگر Room/Cache داری اینجا
//
//        val remote = try {
//            firestoreDataSource.searchUsers(query)
//        } catch (e: Exception) {
//            emptyList()
//        }
//
//        val mock = UserMockData.users.filter {
//            it.username.contains(query, ignoreCase = true) ||
//                    it.fullName.contains(query, ignoreCase = true)
//        }
//
//        return (local + remote + mock)
//            .distinctBy { it.id }
//    }
//
//    override suspend fun getProfile(userId: String): User {
//        Log.d("UserRepository", "Getting profile for userId: $userId")
//
//        // **اول از داده‌های Mock استفاده کن**
//        val mockUser = UserMockData.users.find { it.id == userId }
//        if (mockUser != null) {
//            Log.d("UserRepository", "User found in Mock data: ${mockUser.fullName}")
//            return mockUser
//        }
//
//        // اگر در Mock نبود، از Firestore تلاش کن
//        return try {
//            val userFromFirestore = firestoreDataSource.getUser(userId)
//            userFromFirestore ?: throw Exception("User not found")
//        } catch (e: Exception) {
//            Log.e("UserRepository", "Error fetching from Firestore", e)
//            throw Exception("User not found: ${e.message}")
//        }
//    }
//}
//
//
//
//
//
//
//
//
//
//
package com.melodify.musicapp.data.repository

import android.util.Log
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val currentUserProvider: CurrentUserProvider
) : UserRepository {

    override suspend fun getProfile(userId: String): User {
        Log.d("UserRepository", "Getting profile for userId: $userId")

        // ✅ فقط از Firestore بگیر
        return try {
            val user = firestoreDataSource.getUser(userId)
            if (user != null) {
                Log.d("UserRepository", "✅ User found in Firestore: ${user.fullName}")
                user
            } else {
                throw Exception("User not found in Firestore")
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error fetching from Firestore", e)
            throw Exception("User not found: ${e.message}")
        }
    }

    override suspend fun updateProfile(user: User) {
        try {
            firestoreDataSource.updateUser(user)
            Log.d("UserRepository", "✅ Profile updated for: ${user.fullName}")
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error updating profile: ${e.message}")
            throw e
        }
    }

    override suspend fun follow(userId: String) {
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
        try {
            firestoreDataSource.followUser(currentUserId, userId)
            Log.d("UserRepository", "✅ Successfully followed user: $userId")
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error following user: ${e.message}")
            throw e
        }
    }

    override suspend fun unfollow(userId: String) {
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
        try {
            firestoreDataSource.unfollowUser(currentUserId, userId)
            Log.d("UserRepository", "✅ Successfully unfollowed user: $userId")
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error unfollowing user: ${e.message}")
            throw e
        }
    }

    override suspend fun searchUsers(query: String): List<User> {
        return try {
            firestoreDataSource.searchUsers(query)
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error searching users: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getUserFollowers(userId: String): List<User> {
        return try {
            firestoreDataSource.getFollowers(userId)
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error getting followers: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getUserFollowing(userId: String): List<User> {
        return try {
            firestoreDataSource.getFollowing(userId)
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error getting following: ${e.message}")
            emptyList()
        }
    }
}