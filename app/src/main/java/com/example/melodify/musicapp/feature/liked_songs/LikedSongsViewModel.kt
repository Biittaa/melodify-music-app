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
    val isLoading: Boolean = false,
    val songs: List<Song> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class LikedSongsViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    val uiState: StateFlow<LikedSongsUiState> = songRepository.getLikedSongs()
        .map { songs -> LikedSongsUiState(songs = songs) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LikedSongsUiState(isLoading = true)
        )

    fun unlikeSong(songId: String) {
        viewModelScope.launch {
            songRepository.unlikeSong(songId)
        }
    }

    fun playAll() {
        val songs = uiState.value.songs
        if (songs.isNotEmpty()) {
            playerRepository.play(songs.first())
        }
    }
}
