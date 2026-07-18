package com.melodify.musicapp.data.repository

import com.melodify.musicapp.data.remote.auth.FirebaseAuthDataSource
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.AuthRepository
import com.melodify.musicapp.core.common.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return authDataSource.login(email, password)
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        val result = authDataSource.register(username, email, password)
        if (result is Result.Success) {
            // ذخیره کاربر در Firestore
            firestoreDataSource.createUser(result.data)
        }
        return result
    }

    override suspend fun logout() {
        authDataSource.logout()
    }

    override suspend fun getCurrentUser(): User? {
        return authDataSource.getCurrentUser()
    }

    override suspend fun resetPassword(email: String) {
        authDataSource.resetPassword(email)
    }
}