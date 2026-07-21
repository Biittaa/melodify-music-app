package com.melodify.musicapp.feature.player

import com.melodify.musicapp.domain.model.Song

data class PlayerUiState(
    val isLoading: Boolean = false,
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val repeatMode: Int = 0, // 0=Off, 1=Repeat All, 2=Repeat One
    val isShuffleEnabled: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val isLiked: Boolean = false,
    val sleepTimerMinutes: Int = 0,
    val showSleepTimer: Boolean = false,
    val showSpeedDialog: Boolean = false,
    val error: String? = null
)

sealed class PlayerEvent {
    object OnPlayPause : PlayerEvent()
    object OnNext : PlayerEvent()
    object OnPrevious : PlayerEvent()
    data class OnSeek(val position: Long) : PlayerEvent()
    object OnToggleShuffle : PlayerEvent()
    object OnToggleRepeat : PlayerEvent()
    object OnToggleLike : PlayerEvent()
    data class OnChangeSpeed(val speed: Float) : PlayerEvent()
    data class OnSetSleepTimer(val minutes: Int) : PlayerEvent()
    object OnShowSleepTimer : PlayerEvent()
    object OnDismissSleepTimer : PlayerEvent()
    object OnShowSpeedDialog : PlayerEvent()
    object OnDismissSpeedDialog : PlayerEvent()
    object OnShare : PlayerEvent()
}