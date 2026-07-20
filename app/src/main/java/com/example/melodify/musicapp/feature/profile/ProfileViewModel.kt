package com.melodify.musicapp.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.UserRepository
import com.melodify.musicapp.domain.repository.SettingsRepository
import com.melodify.musicapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isPremium: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val isPremium = settingsRepository.getPremium()
                val user = try {
                    userRepository.getProfile("current_user_id")
                } catch (e: Exception) {
                    User(
                        id = "current_user_id",
                        username = "User",
                        fullName = "Full Name",
                        email = "user@melodify.com",
                        profileImage = "",
                        bio = "Music Lover",
                        followersCount = 0,
                        followingCount = 0,
                        playlistsCount = 0,
                        isPremium = isPremium,
                        isFollowing = false
                    )
                }
                _uiState.update { it.copy(user = user, isPremium = isPremium, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun upgradeToPremium() {
        viewModelScope.launch {
            settingsRepository.setPremium(true)
            _uiState.update { it.copy(isPremium = true) }
            _uiState.value.user?.let {
                userRepository.updateProfile(it.copy(isPremium = true))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            // DataStore and Room clearing logic is handled in AuthRepositoryImpl
        }
    }
}
