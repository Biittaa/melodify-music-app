package com.melodify.musicapp.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PlayerEvent>()

    init {
        collectEvents()
    }

    private fun collectEvents() {
        viewModelScope.launch {
            _events.collect { event ->
                when (event) {
                    is PlayerEvent.OnPlayPause -> togglePlayPause()
                    is PlayerEvent.OnNext -> nextSong()
                    is PlayerEvent.OnPrevious -> previousSong()
                    is PlayerEvent.OnSeek -> seekTo(event.position)
                    is PlayerEvent.OnToggleShuffle -> toggleShuffle()
                    is PlayerEvent.OnToggleRepeat -> toggleRepeat()
                    is PlayerEvent.OnToggleLike -> toggleLike()
                    is PlayerEvent.OnChangeSpeed -> changeSpeed(event.speed)
                    is PlayerEvent.OnSetSleepTimer -> setSleepTimer(event.minutes)
                    is PlayerEvent.OnShowSleepTimer -> showSleepTimer()
                    is PlayerEvent.OnDismissSleepTimer -> dismissSleepTimer()
                    is PlayerEvent.OnShowSpeedDialog -> showSpeedDialog()
                    is PlayerEvent.OnDismissSpeedDialog -> dismissSpeedDialog()
                    is PlayerEvent.OnShare -> shareSong()
                }
            }
        }
    }

    fun onEvent(event: PlayerEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    fun loadSong(songId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val song = songRepository.getSong(songId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentSong = song,
                        duration = song.duration,
                        isLiked = song.isLiked
                    )
                }
                // Start playing
                playSong(song)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error loading song"
                    )
                }
            }
        }
    }

    private fun playSong(song: Song) {
        _uiState.update {
            it.copy(
                isPlaying = true,
                currentPosition = 0L
            )
        }
        // TODO: Connect to PlayerRepository
    }

    private fun togglePlayPause() {
        _uiState.update {
            it.copy(isPlaying = !it.isPlaying)
        }
        // TODO: Connect to PlayerRepository
    }

    private fun nextSong() {
        // TODO: Get next song from queue
        _uiState.update {
            it.copy(currentPosition = 0L)
        }
    }

    private fun previousSong() {
        // TODO: Get previous song from queue
        _uiState.update {
            it.copy(currentPosition = 0L)
        }
    }

    private fun seekTo(position: Long) {
        _uiState.update {
            it.copy(currentPosition = position)
        }
        // TODO: Connect to PlayerRepository
    }

    private fun toggleShuffle() {
        _uiState.update {
            it.copy(isShuffleEnabled = !it.isShuffleEnabled)
        }
        // TODO: Connect to PlayerRepository
    }

    private fun toggleRepeat() {
        val newMode = when (_uiState.value.repeatMode) {
            0 -> 1  // Off → Repeat All
            1 -> 2  // Repeat All → Repeat One
            else -> 0  // Repeat One → Off
        }
        _uiState.update {
            it.copy(repeatMode = newMode)
        }
        // TODO: Connect to PlayerRepository
    }

    private fun toggleLike() {
        viewModelScope.launch {
            val currentSong = _uiState.value.currentSong ?: return@launch
            val isLiked = !_uiState.value.isLiked
            _uiState.update { it.copy(isLiked = isLiked) }

            try {
                if (isLiked) {
                    songRepository.likeSong(currentSong.id)
                } else {
                    songRepository.unlikeSong(currentSong.id)
                }
            } catch (e: Exception) {
                // Revert on error
                _uiState.update { it.copy(isLiked = !isLiked) }
            }
        }
    }

    private fun changeSpeed(speed: Float) {
        _uiState.update {
            it.copy(playbackSpeed = speed)
        }
        // TODO: Connect to PlayerRepository
    }

    private fun setSleepTimer(minutes: Int) {
        _uiState.update {
            it.copy(
                sleepTimerMinutes = minutes,
                showSleepTimer = false
            )
        }
        if (minutes > 0) {
            // TODO: Start sleep timer with Coroutines
            viewModelScope.launch {
                kotlinx.coroutines.delay(minutes * 60_000L)
                togglePlayPause()
            }
        }
    }

    private fun showSleepTimer() {
        _uiState.update { it.copy(showSleepTimer = true) }
    }

    private fun dismissSleepTimer() {
        _uiState.update { it.copy(showSleepTimer = false) }
    }

    private fun showSpeedDialog() {
        _uiState.update { it.copy(showSpeedDialog = true) }
    }

    private fun dismissSpeedDialog() {
        _uiState.update { it.copy(showSpeedDialog = false) }
    }

    private fun shareSong() {
        // TODO: Share song
    }
}