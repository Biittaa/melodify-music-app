package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.User

interface UserRepository {
    suspend fun getProfile(userId: String): User
    suspend fun updateProfile(user: User)
    suspend fun follow(userId: String)
    suspend fun unfollow(userId: String)
    suspend fun getUserFollowers(userId: String): List<User>
    suspend fun getUserFollowing(userId: String): List<User>
}