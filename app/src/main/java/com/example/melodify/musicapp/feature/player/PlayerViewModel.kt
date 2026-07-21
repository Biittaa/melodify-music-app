package com.melodify.musicapp.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.core.common.Result
import com.melodify.musicapp.domain.model.PlayerState
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.AuthRepository
import com.melodify.musicapp.domain.repository.ChatRepository
import com.melodify.musicapp.domain.repository.DownloadRepository
import com.melodify.musicapp.domain.repository.PlayerRepository
import com.melodify.musicapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val downloadRepository: DownloadRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = playerRepository.playerState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerState())

    private val _friendsList = MutableStateFlow<List<User>>(emptyList())
    val friendsList: StateFlow<List<User>> = _friendsList.asStateFlow()

    init {
        loadFriends()
    }

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

    // WorkManager download wrapper
    fun downloadSong(songId: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            when (val result = downloadRepository.download(songId)) {
                is Result.Success -> onResult("Download initiated successfully")
                is Result.Error -> onResult(result.exception.message ?: "Failed to start download")
            }
        }
    }

    private fun loadFriends() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                _friendsList.value = userRepository.getUserFollowing(currentUser.id)
            }
        }
    }

    fun shareSongWithFriend(friendId: String, songId: String) {
        viewModelScope.launch {
            chatRepository.sendSong(friendId, songId)
        }
    }
}