package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.User

/**
 * Repository interface for user profile management
 * Handles profile retrieval, updates, and follow/unfollow functionality
 */
interface UserRepository {

    /**
     * Get user profile by ID
     * @param userId Target user ID
     * @return User profile data
     */
    suspend fun getProfile(userId: String): User

    /**
     * Update user profile information
     * @param user Updated user object
     */
    suspend fun updateProfile(user: User)

    /**
     * Follow a user
     * @param userId ID of the user to follow
     */
    suspend fun follow(userId: String)

    /**
     * Unfollow a user
     * @param userId ID of the user to unfollow
     */
    suspend fun unfollow(userId: String)

    /**
     * Get list of followers for a user
     * @param userId Target user ID
     * @return List of User objects who follow the target
     */
    suspend fun getUserFollowers(userId: String): List<User>

    /**
     * Get list of users that a user is following
     * @param userId Target user ID
     * @return List of User objects the target follows
     */
    suspend fun getUserFollowing(userId: String): List<User>

    suspend fun searchUsers(query: String): List<User>

}