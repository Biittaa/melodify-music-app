package com.melodify.musicapp.feature.liked_songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.SongRepository
import com.melodify.musicapp.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LikedSongsUiState(
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class LikedSongsViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LikedSongsUiState())
    val uiState: StateFlow<LikedSongsUiState> = _uiState.asStateFlow()

    init {
        loadLikedSongs()
    }

    private fun loadLikedSongs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Assuming getLikedSongs returns a List<Song>
                val likedSongs = songRepository.getLikedSongs()
                _uiState.update { it.copy(songs = likedSongs, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun playAll() {
        if (uiState.value.songs.isNotEmpty()) {
            playerRepository.play(uiState.value.songs.first())
            // In a real implementation, you'd send the whole list to the player queue
        }
    }

    fun unlikeSong(songId: String) {
        viewModelScope.launch {
            songRepository.unlikeSong(songId)
            loadLikedSongs()
        }
    }
}
