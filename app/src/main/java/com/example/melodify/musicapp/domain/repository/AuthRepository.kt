package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.core.common.Result

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(username: String, email: String, password: String): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun resetPassword(email: String)
}