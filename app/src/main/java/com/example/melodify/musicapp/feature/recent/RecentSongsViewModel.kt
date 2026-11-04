package com.melodify.musicapp.feature.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.PlayerRepository
import com.melodify.musicapp.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecentSongsUiState(
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class RecentSongsViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecentSongsUiState())
    val uiState: StateFlow<RecentSongsUiState> = _uiState.asStateFlow()

    init {
        loadRecentSongs()
    }

    fun loadRecentSongs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val songs = songRepository.getRecentlyPlayed()
                _uiState.update { it.copy(songs = songs, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun playAll(shuffle: Boolean = false) {
        val songs = uiState.value.songs
        if (songs.isNotEmpty()) {
            val playlist = if (shuffle) songs.shuffled() else songs
            playerRepository.playPlaylist(playlist, 0)
        }
    }

    fun deleteRecentSong(songId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(songs = state.songs.filter { it.id != songId })
            }
            runCatching {
                songRepository.deleteRecentSong(songId)
            }
        }
    }
}