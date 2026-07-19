package com.melodify.musicapp.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
<<<<<<< Updated upstream
import com.melodify.musicapp.domain.repository.UserRepository
=======
>>>>>>> Stashed changes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val premiumRepository: PremiumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()

    init {
        loadProfile()
        collectEvents()
    }

    private fun collectEvents() {
        viewModelScope.launch {
            _events.collect { event ->
                when (event) {
                    ProfileEvent.OnUpgradePremium -> upgradePremium()
                    ProfileEvent.OnEditProfile -> editProfile()
                    ProfileEvent.OnSettings -> navigateToSettings()
                    ProfileEvent.OnLogout -> logout()
                    is ProfileEvent.OnFollowerClick -> handleFollowerClick(event.userId)
                    is ProfileEvent.OnFollowingClick -> handleFollowingClick(event.userId)
                    is ProfileEvent.OnPlaylistClick -> handlePlaylistClick(event.playlistId)
                }
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getCurrentUser()
                val isPremium = premiumRepository.isPremium()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                        isPremium = isPremium
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    private fun upgradePremium() {
        viewModelScope.launch {
            try {
                premiumRepository.activatePremium()
                _uiState.update { it.copy(isPremium = true) }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun editProfile() { /* Navigate */ }
    private fun navigateToSettings() { /* Navigate */ }
    private fun logout() { /* Logout */ }
    private fun handleFollowerClick(userId: String) { /* Navigate */ }
    private fun handleFollowingClick(userId: String) { /* Navigate */ }
    private fun handlePlaylistClick(playlistId: String) { /* Navigate */ }
}