package com.melodify.musicapp.core.common

import android.util.Log
import com.melodify.musicapp.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryUserStore @Inject constructor() {

    private val _users = MutableStateFlow(UserMockData.users.toMutableList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    fun updateUser(updatedUser: User) {
        val currentList = _users.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedUser.id }
        if (index != -1) {
            currentList[index] = updatedUser
            _users.value = currentList
            Log.d("InMemoryUserStore", "✅ User updated: ${updatedUser.fullName}")
        } else {
            currentList.add(updatedUser)
            _users.value = currentList
            Log.d("InMemoryUserStore", "✅ New user added: ${updatedUser.fullName}")
        }
    }

    fun followUser(followerId: String, followingId: String) {
        val currentList = _users.value.toMutableList()

        // به‌روزرسانی کاربری که فالو شده
        val followedIndex = currentList.indexOfFirst { it.id == followingId }
        if (followedIndex != -1) {
            val followed = currentList[followedIndex]
            currentList[followedIndex] = followed.copy(
                followersCount = followed.followersCount + 1,
                isFollowing = true
            )
            Log.d("InMemoryUserStore", "✅ ${followed.fullName} now has ${followed.followersCount + 1} followers")
        }

        // به‌روزرسانی کاربری که فالو کرده
        val followerIndex = currentList.indexOfFirst { it.id == followerId }
        if (followerIndex != -1) {
            val follower = currentList[followerIndex]
            currentList[followerIndex] = follower.copy(
                followingCount = follower.followingCount + 1
            )
        }

        _users.value = currentList
    }

    fun unfollowUser(followerId: String, followingId: String) {
        val currentList = _users.value.toMutableList()

        val followedIndex = currentList.indexOfFirst { it.id == followingId }
        if (followedIndex != -1) {
            val followed = currentList[followedIndex]
            currentList[followedIndex] = followed.copy(
                followersCount = (followed.followersCount - 1).coerceAtLeast(0),
                isFollowing = false
            )
            Log.d("InMemoryUserStore", "✅ ${followed.fullName} unfollowed")
        }

        val followerIndex = currentList.indexOfFirst { it.id == followerId }
        if (followerIndex != -1) {
            val follower = currentList[followerIndex]
            currentList[followerIndex] = follower.copy(
                followingCount = (follower.followingCount - 1).coerceAtLeast(0)
            )
        }

        _users.value = currentList
    }

    fun getUser(userId: String): User? {
        return _users.value.find { it.id == userId }
    }
}