package com.melodify.musicapp.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.PlayerState
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = playerRepository.playerState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerState())

    fun playSong(song: Song) {
        playerRepository.play(song)
    }

    fun pauseResume() {
        if (playerState.value.isPlaying) {
            playerRepository.pause()
        } else {
            playerState.value.currentSong?.let {
                playerRepository.play(it)
            }
        }
    }

    fun next() = playerRepository.next()
    fun previous() = playerRepository.previous()
    
    fun seekTo(position: Long) = playerRepository.seekTo(position)
    
    fun setSpeed(speed: Float) = playerRepository.setSpeed(speed)
    
    fun setSleepTimer(minutes: Int) = playerRepository.setSleepTimer(minutes)
    
    fun toggleShuffle() = playerRepository.toggleShuffle()
    
    fun setRepeatMode(mode: Int) = playerRepository.setRepeatMode(mode)
}
