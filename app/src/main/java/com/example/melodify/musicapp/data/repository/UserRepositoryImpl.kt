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
//    private val currentUserProvider: CurrentUserProvider
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
//    override suspend fun follow(userId: String) {
//        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
//        firestoreDataSource.followUser(currentUserId, userId)
//    }
//
//    override suspend fun unfollow(userId: String) {
//        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
//        firestoreDataSource.unfollowUser(currentUserId, userId)
//    }
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
//    override suspend fun getProfile(userId: String): User {
//        Log.d("UserRepository", "Getting profile for userId: $userId")
//
//        // ابتدا از Firestore تلاش کنید
//        val userFromFirestore = try {
//            firestoreDataSource.getUser(userId)
//        } catch (e: Exception) {
//            Log.e("UserRepository", "Error fetching from Firestore", e)
//            null
//        }
//
//        // اگر در Firestore پیدا نشد، از MockData استفاده کنید
//        return userFromFirestore ?: run {
//            Log.d("UserRepository", "User not found in Firestore, using mock data")
//            UserMockData.users.find { it.id == userId }
//                ?: throw Exception("User not found")
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
import com.melodify.musicapp.core.common.UserMockData
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

    // نگهداری لیست محلی کاربران جهت ذخیره موقت وضعیت فالو در حافظه
    private var localUsers = UserMockData.users.toMutableList()

    override suspend fun getProfile(userId: String): User {
        val currentUserId = currentUserProvider.getCurrentUser()?.id

        // ۱. تلاش برای دریافت از Firestore
        val userFromFirestore = try {
            firestoreDataSource.getUser(userId)
        } catch (e: Exception) {
            Log.e("Profile", "getUser failed", e)
            null
        }

        // ۲. در صورت عدم وجود در فایربیس، استفاده از لیست محلی
        val targetUser = userFromFirestore ?: localUsers.find { it.id == userId }
        ?: throw Exception("User not found")

        // ۳. بررسی اینکه آیا کاربر جاری این فرد را فالو کرده است یا خیر
        val followingList = if (currentUserId != null) getUserFollowing(currentUserId) else emptyList()
        val isFollowing = followingList.any { it.id == userId } || targetUser.isFollowing

        return targetUser.copy(isFollowing = isFollowing)
    }



    override suspend fun follow(userId: String) {
        val currentUserId = currentUserProvider.getCurrentUser()?.id
//        firestoreDataSource.followUser(currentUserId, userId)


        // ۱. آپدیت پروفایل کاربر جاری (افزایش Following)
        currentUserProvider.getCurrentUser()?.let { me ->
            val updatedMe = me.copy(followingCount = me.followingCount + 1)
            currentUserProvider.setUser(updatedMe)
        }

//        currentUserProvider.getCurrentUser()?.let { current ->
//            val updatedUser = current.copy(followingCount = current.followingCount + 1)
//            currentUserProvider.setCurrentUser(updatedUser)

        // ۲. آپدیت دیتابیس Firestore
        if (currentUserId != null) {
            try {
                firestoreDataSource.followUser(currentUserId, userId)
            } catch (e: Exception) {
                Log.e("UserRepository", "Firestore follow error", e)
            }
        }

        // ۳. آپدیت لیست محلی جهت حفظ وضعیت
        val index = localUsers.indexOfFirst { it.id == userId }
        if (index != -1) {
            val user = localUsers[index]
            localUsers[index] = user.copy(
                isFollowing = true,
                followersCount = user.followersCount + 1
            )
        }

//        userDao.insertUser(updatedUser.toEntity())
    }

    override suspend fun unfollow(userId: String) {
        val currentUserId = currentUserProvider.getCurrentUser()?.id

        // ۱. آپدیت پروفایل کاربر جاری (کاهش Following)
        currentUserProvider.getCurrentUser()?.let { me ->
            val updatedMe = me.copy(followingCount = (me.followingCount - 1).coerceAtLeast(0))
            currentUserProvider.setUser(updatedMe)
        }

        // ۲. آپدیت دیتابیس Firestore
        if (currentUserId != null) {
            try {
                firestoreDataSource.unfollowUser(currentUserId, userId)
            } catch (e: Exception) {
                Log.e("UserRepository", "Firestore unfollow error", e)
            }
        }

        // ۳. آپدیت لیست محلی
        val index = localUsers.indexOfFirst { it.id == userId }
        if (index != -1) {
            val user = localUsers[index]
            localUsers[index] = user.copy(
                isFollowing = false,
                followersCount = (user.followersCount - 1).coerceAtLeast(0)
            )
        }
    }

    override suspend fun getUserFollowing(userId: String): List<User> {
        val remoteFollowing = try {
            firestoreDataSource.getFollowing(userId)
        } catch (e: Exception) {
            emptyList()
        }

        val localFollowing = localUsers.filter { it.isFollowing }
        return (remoteFollowing + localFollowing).distinctBy { it.id }
    }

    override suspend fun getUserFollowers(userId: String): List<User> {
        return try {
            firestoreDataSource.getFollowers(userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

//    override suspend fun searchUsers(query: String): List<User> {
//        val remote = try {
//            firestoreDataSource.searchUsers(query)
//        } catch (e: Exception) {
//            emptyList()
//        }
//
//        val matchedLocal = localUsers.filter {
//            it.username.contains(query, ignoreCase = true) ||
//                    it.fullName.contains(query, ignoreCase = true)
//        }
//
//        return (remote + matchedLocal).distinctBy { it.id }
//    }

    override suspend fun updateProfile(user: User) {
        try {
            firestoreDataSource.updateUser(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Update profile error", e)
        }
    }

    override suspend fun searchUsers(query: String): List<User> {
        val remote = try {
            firestoreDataSource.searchUsers(query)
        } catch (e: Exception) {
            emptyList()
        }

        val matchedLocal = localUsers.filter {
            it.username.contains(query, ignoreCase = true) ||
                    it.fullName.contains(query, ignoreCase = true)
        }

        return (remote + matchedLocal).distinctBy { it.id }
    }
}