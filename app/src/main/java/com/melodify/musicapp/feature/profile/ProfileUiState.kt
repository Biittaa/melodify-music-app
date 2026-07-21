package com.melodify.musicapp.feature.profile

import com.melodify.musicapp.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isPremium: Boolean = false,
    val error: String? = null
)

sealed class ProfileEvent {
    object OnUpgradePremium : ProfileEvent()
    object OnEditProfile : ProfileEvent()
    object OnSettings : ProfileEvent()
    object OnLogout : ProfileEvent()
    data class OnFollowerClick(val userId: String) : ProfileEvent()
    data class OnFollowingClick(val userId: String) : ProfileEvent()
    data class OnPlaylistClick(val playlistId: String) : ProfileEvent()
}