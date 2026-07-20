package com.melodify.musicapp.feature.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SocialUiState(
    val followers: List<User> = emptyList(),
    val following: List<User> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SocialUiState())
    val uiState: StateFlow<SocialUiState> = _uiState.asStateFlow()

    fun loadSocialData(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val followers = userRepository.getUserFollowers(userId)
            val following = userRepository.getUserFollowing(userId)
            _uiState.update { it.copy(followers = followers, following = following, isLoading = false) }
        }
    }

    fun unfollowUser(userId: String, currentUserId: String) {
        viewModelScope.launch {
            userRepository.unfollow(userId)
            loadSocialData(currentUserId)
        }
    }
}
