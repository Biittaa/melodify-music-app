package com.melodify.musicapp.data.remote.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.melodify.musicapp.core.common.Result
import com.melodify.musicapp.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.Error(Exception("User is null after login"))
            val user = firebaseUser.toUser()
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.Error(Exception("User is null after registration"))

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            val user = firebaseUser.toUser()
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun logout() {
        auth.signOut()
    }

    suspend fun getCurrentUser(): User? {
        return auth.currentUser?.toUser()
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }


    private fun FirebaseUser.toUser(): User {
        return User(
            id = uid,
            username = displayName ?: "",
            fullName = displayName ?: "",
            email = email ?: "",
            profileImage = photoUrl?.toString() ?: "",
            bio = "",
            followersCount = 0,
            followingCount = 0,
            playlistsCount = 0,
            isPremium = false,
            isFollowing = false
        )
    }
}