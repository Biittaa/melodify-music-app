package com.melodify.musicapp.data.repository

import android.util.Log
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.UserRepository
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.core.common.MockData
import com.melodify.musicapp.core.common.UserMockData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val currentUserProvider: CurrentUserProvider
) : UserRepository {

    override suspend fun getProfile(userId: String): User {
        return firestoreDataSource.getUser(userId) ?: throw Exception("User not found")
    }

    override suspend fun updateProfile(user: User) {
        firestoreDataSource.updateUser(user)
    }

    override suspend fun follow(userId: String) {
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
        firestoreDataSource.followUser(currentUserId, userId)
    }

    override suspend fun unfollow(userId: String) {
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
        firestoreDataSource.unfollowUser(currentUserId, userId)
    }

    override suspend fun getUserFollowers(userId: String): List<User> {
        return firestoreDataSource.getFollowers(userId)
    }

    override suspend fun getUserFollowing(userId: String): List<User> {
        return firestoreDataSource.getFollowing(userId)
    }

//    override suspend fun searchUsers(query: String): List<User> {
//        return firestoreDataSource.searchUsers(query)
//    }

    override suspend fun searchUsers(query: String): List<User> {

        val local = emptyList<User>() // اگر Room/Cache داری اینجا

        val remote = try {
            firestoreDataSource.searchUsers(query)
        } catch (e: Exception) {
            emptyList()
        }

        val mock = UserMockData.users.filter {
            it.username.contains(query, ignoreCase = true) ||
                    it.fullName.contains(query, ignoreCase = true)
        }

        return (local + remote + mock)
            .distinctBy { it.id }
    }}




















