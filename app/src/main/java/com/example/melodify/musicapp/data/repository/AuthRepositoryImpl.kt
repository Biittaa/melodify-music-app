package com.melodify.musicapp.data.repository

import android.util.Log
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.core.common.Result
import com.melodify.musicapp.data.remote.auth.FirebaseAuthDataSource
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.data.remote.sync.ChatSyncService
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

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
                chatSyncService.startListening()
            }
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        // 1. Attempt authentication
        val authResult = authDataSource.register(username, email, password)
        if (authResult is Result.Error) {
            return authResult // the error message is already user‑friendly
        }

        val user = (authResult as Result.Success).data

        // 2. Save user to Firestore
        return try {
            firestoreDataSource.createUser(user)
            // success: update local cache and start chat sync
            currentUserProvider.setUser(user)
            chatSyncService.startListening()
            Result.Success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firestore save failed", e)
            // Provide a clear message; the user is still created in Auth,
            // but profile data couldn't be saved – they can retry later.
            Result.Error(Exception("Account created, but profile data couldn't be saved. Please try again later."))
        }
    }

    override suspend fun logout() {
        chatSyncService.stopListening()
        authDataSource.logout()
        currentUserProvider.clear()
    }

    override suspend fun getCurrentUser(): User? {
        return currentUserProvider.getCurrentUser() ?: authDataSource.getCurrentUser()?.also {
            currentUserProvider.setUser(it)
            chatSyncService.startListening()
        }
    }

    override suspend fun resetPassword(email: String) {
        authDataSource.resetPassword(email)
    }
}