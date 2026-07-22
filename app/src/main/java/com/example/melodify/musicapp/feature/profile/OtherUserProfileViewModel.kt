package com.melodify.musicapp.feature.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtherUserProfileUiState())
    val uiState: StateFlow<OtherUserProfileUiState> = _uiState.asStateFlow()

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                Log.d("OtherUserProfile", "Calling userRepository.getProfile for userId: $userId")
                val user = userRepository.getProfile(userId)
                Log.d("OtherUserProfile", "User loaded: ${user?.fullName}")
                _uiState.update {
                    it.copy(
                        user = user,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                Log.e("OtherUserProfile", "Error loading profile", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "خطا در بارگذاری"
                    )
                }
            }
        }
    }


    fun followUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.follow(userId)
                // به‌روزرسانی وضعیت محلی
                _uiState.value.user?.let { currentUser ->
                    val updatedUser = currentUser.copy(
                        isFollowing = true,
                        followersCount = currentUser.followersCount + 1
                    )
                    _uiState.update { it.copy(user = updatedUser) }
                }
            } catch (e: Exception) {
                // مدیریت خطا
            }
        }
    }

    fun unfollowUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.unfollow(userId)
                // به‌روزرسانی وضعیت محلی
                _uiState.value.user?.let { currentUser ->
                    val updatedUser = currentUser.copy(
                        isFollowing = false,
                        followersCount = currentUser.followersCount - 1
                    )
                    _uiState.update { it.copy(user = updatedUser) }
                }
            } catch (e: Exception) {
                // مدیریت خطا
            }
        }
    }
}