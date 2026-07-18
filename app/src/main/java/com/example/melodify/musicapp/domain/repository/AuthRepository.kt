package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.core.common.Result
import com.melodify.musicapp.domain.model.User

/**
 * Repository interface for authentication operations
 * Handles login, registration, logout, and password management
 */
interface AuthRepository {

    /**
     * Login user with email and password
     * @param email User's email address
     * @param password User's password
     * @return Result containing User data on success, or Error on failure
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Register a new user
     * @param username Desired username
     * @param email User's email address
     * @param password User's password
     * @return Result containing created User data on success
     */
    suspend fun register(username: String, email: String, password: String): Result<User>

    /**
     * Log out the current user
     */
    suspend fun logout()

    /**
     * Get the currently logged-in user
     * @return User if logged in, null otherwise
     */
    suspend fun getCurrentUser(): User?

    /**
     * Send password reset email
     * @param email User's registered email address
     */
    suspend fun resetPassword(email: String)
}