package com.melodify.musicapp.feature.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.SongRepository
import com.melodify.musicapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OtherUserProfileUiState(
    val user: User? = null,
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OtherUserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtherUserProfileUiState())
    val uiState: StateFlow<OtherUserProfileUiState> = _uiState.asStateFlow()

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getProfile(userId)
                val songs = songRepository.getSongsByArtist(userId)
                _uiState.update {
                    it.copy(
                        user = user,
                        songs = songs,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "خطا در بارگذاری"
                    )
                }
            }
        }
    }


//    fun followUser(userId: String) {
//        viewModelScope.launch {
//            try {
//                userRepository.follow(userId)
//                _uiState.update { state ->
//                    state.user?.let { currentUser ->
//                        state.copy(user = currentUser.copy(
//                            isFollowing = true,
//                            followersCount = currentUser.followersCount + 1
//                        ))
//                    } ?: state
//                }
//            } catch (e: Exception) {
//                Log.e("OtherUserProfile", "Error following user", e)
//            }
//        }
//    }

    fun followUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.follow(userId)
                _uiState.update { state ->
                    state.user?.let { currentUser ->
                        state.copy(
                            user = currentUser.copy(
                                isFollowing = true,
                                followersCount = currentUser.followersCount + 1
                            )
                        )
                    } ?: state
                }
            } catch (e: Exception) {
                Log.e("OtherUserProfile", "Error following user", e)
            }
        }
    }

    fun unfollowUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.unfollow(userId)
                _uiState.update { state ->
                    state.user?.let { currentUser ->
                        state.copy(
                            user = currentUser.copy(
                                isFollowing = false,
                                followersCount = (currentUser.followersCount - 1).coerceAtLeast(0)
                            )
                        )
                    } ?: state
                }
            } catch (e: Exception) {
                Log.e("OtherUserProfile", "Error unfollowing user", e)
            }
        }
    }

//    fun unfollowUser(userId: String) {
//        viewModelScope.launch {
//            try {
//                userRepository.unfollow(userId)
//                _uiState.update { state ->
//                    state.user?.let { currentUser ->
//                        state.copy(user = currentUser.copy(
//                            isFollowing = false,
//                            followersCount = currentUser.followersCount - 1
//                        ))
//                    } ?: state
//                }
//            } catch (e: Exception) {
//                Log.e("OtherUserProfile", "Error unfollowing user", e)
//            }
//        }
//    }
}