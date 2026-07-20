package com.melodify.musicapp.data.repository

import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.UserRepository
import com.melodify.musicapp.core.common.CurrentUserProvider
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
}
