package com.melodify.musicapp.feature.profile

import android.net.Uri
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
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    val user = userRepository.getProfile(currentUser.id)
                    _uiState.update { it.copy(user = user, isPremium = isPremium, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateProfile(fullName: String, bio: String) {
        viewModelScope.launch {
            _uiState.value.user?.let { current ->
                val updatedUser = current.copy(fullName = fullName, bio = bio)
                userRepository.updateProfile(updatedUser)
                _uiState.update { it.copy(user = updatedUser) }
            }
        }
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.value.user?.let { current ->
                // در اپلیکیشن واقعی ابتدا در Storage آپلود می‌شود
                val updatedUser = current.copy(profileImage = uri.toString())
                userRepository.updateProfile(updatedUser)
                _uiState.update { it.copy(user = updatedUser) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(user = null) }
        }
    }
    
    fun upgradeToPremium() {
        viewModelScope.launch {
            settingsRepository.setPremium(true)
            _uiState.update { it.copy(isPremium = true) }
        }
    }
}
