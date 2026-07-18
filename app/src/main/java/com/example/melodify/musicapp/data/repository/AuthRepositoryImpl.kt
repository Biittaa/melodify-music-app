package com.melodify.musicapp.data.repository

import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.core.common.Result
import com.melodify.musicapp.data.remote.auth.FirebaseAuthDataSource
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.data.remote.sync.ChatSyncService
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository
 * Combines Firebase Authentication with local user cache
 * Manages real-time chat sync lifecycle based on login/logout
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    private val currentUserProvider: CurrentUserProvider,
    private val chatSyncService: ChatSyncService
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return authDataSource.login(email, password).also { result ->
            if (result is Result.Success) {
                currentUserProvider.setUser(result.data)
                // Start real-time chat sync after successful login
                chatSyncService.startListening()
            }
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        return authDataSource.register(username, email, password).also { result ->
            if (result is Result.Success) {
                // Save user to Firestore and update cache
                firestoreDataSource.createUser(result.data)
                currentUserProvider.setUser(result.data)
                // Start real-time chat sync after successful registration
                chatSyncService.startListening()
            }
        }
    }

    override suspend fun logout() {
        // Stop chat sync before logging out
        chatSyncService.stopListening()
        authDataSource.logout()
        currentUserProvider.clear()
    }

    override suspend fun getCurrentUser(): User? {
        return currentUserProvider.getCurrentUser() ?: authDataSource.getCurrentUser()?.also {
            // If user is present in Firebase but not in cache, set it and start sync
            currentUserProvider.setUser(it)
            chatSyncService.startListening()
        }
    }

    override suspend fun resetPassword(email: String) {
        authDataSource.resetPassword(email)
    }
}