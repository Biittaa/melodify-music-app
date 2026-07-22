package com.example.melodify.musicapp.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OtherUserProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OtherUserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtherUserProfileUiState())
    val uiState: StateFlow<OtherUserProfileUiState> = _uiState.asStateFlow()

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getProfile(userId)
                _uiState.update {
                    it.copy(
                        user = user,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error loading profile"
                    )
                }
            }
        }
    }

    fun followUser(userId: String) {
        viewModelScope.launch {
            try {
                // 1. فالو کردن
                userRepository.follow(userId)

                // 2. به‌روزرسانی وضعیت محلی
                val currentUser = _uiState.value.user
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        isFollowing = true,
                        followersCount = currentUser.followersCount + 1
                    )
                    _uiState.update { it.copy(user = updatedUser) }
                }

                // 3. دوباره از سرور بگیر (برای اطمینان)
                loadUserProfile(userId)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error following user")
                }
            }
        }
    }

    fun unfollowUser(userId: String) {
        viewModelScope.launch {
            try {
                // 1. آنفالو کردن
                userRepository.unfollow(userId)

                // 2. به‌روزرسانی وضعیت محلی
                val currentUser = _uiState.value.user
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        isFollowing = false,
                        followersCount = (currentUser.followersCount - 1).coerceAtLeast(0)
                    )
                    _uiState.update { it.copy(user = updatedUser) }
                }

                // 3. دوباره از سرور بگیر (برای اطمینان)
                loadUserProfile(userId)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error unfollowing user")
                }
            }
        }
    }
    private fun updateCurrentUserAfterFollow() {
        viewModelScope.launch {
            val currentUserId = currentUserProvider.getCurrentUser()?.id
            if (currentUserId != null) {
                val updatedUser = userRepository.getProfile(currentUserId)
                currentUserProvider.setUser(updatedUser)
            }
        }
    }
}