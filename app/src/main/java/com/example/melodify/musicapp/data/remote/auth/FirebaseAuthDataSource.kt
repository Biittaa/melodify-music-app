package com.melodify.musicapp.data.remote.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.melodify.musicapp.core.common.Result
import com.melodify.musicapp.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.Error(Exception("User is null after login"))
            Result.Success(firebaseUser.toUser())
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Login error", e)
            val message = when (e) {
                is FirebaseAuthException -> when (e.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email."
                    "ERROR_WRONG_PASSWORD" -> "Incorrect password."
                    "ERROR_INVALID_EMAIL" -> "Invalid email format."
                    "ERROR_USER_DISABLED" -> "This account has been disabled."
                    else -> "Authentication failed: ${e.message}"
                }
                else -> "Network error or server unreachable. Please try again."
            }
            Result.Error(Exception(message))
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

            Result.Success(firebaseUser.toUser())
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Registration error", e)
            val message = when (e) {
                is FirebaseAuthUserCollisionException -> "Email already in use."
                is FirebaseAuthException -> when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Invalid email format."
                    "ERROR_WEAK_PASSWORD" -> "Password must be at least 6 characters."
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "Email already registered."
                    else -> "Registration failed: ${e.message}"
                }
                else -> "Network error or server unreachable. Please try again."
            }
            Result.Error(Exception(message))
        }
    }

    suspend fun logout() {
        auth.signOut()
    }

    suspend fun getCurrentUser(): User? {
        return auth.currentUser?.toUser()
    }

    suspend fun resetPassword(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            throw e // propagate; UI can handle
        }
    }

    private fun com.google.firebase.auth.FirebaseUser.toUser(): User {
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

    // در FirebaseAuthDataSource.kt یا AuthRepositoryImpl.kt
    suspend fun createUserInFirestore(user: User) {
        val userMap = mapOf(
            "id" to user.id,
            "username" to user.username,
            "fullName" to user.fullName,
            "email" to user.email,
            "profileImage" to user.profileImage,
            "bio" to user.bio,
            "followersCount" to user.followersCount,
            "followingCount" to user.followingCount,
            "playlistsCount" to user.playlistsCount,
            "isPremium" to user.isPremium,
            "isFollowing" to user.isFollowing,
            "usernameSearch" to user.username.lowercase() // این خط مهم است!
        )

        firestore.collection("users")
            .document(user.id)
            .set(userMap)
            .await()
    }
}