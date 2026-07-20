package com.melodify.musicapp.feature.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistUiState())
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val userId = currentUserProvider.getCurrentUser()?.id ?: ""
                val userPlaylists = if (userId.isNotEmpty()) {
                    playlistRepository.getUserPlaylists(userId)
                } else emptyList()

                // Mocking categories as per requirements
                val internal = listOf(
                    Playlist("i1", "پاپ فارسی", "مجموعه آهنگ‌های پاپ", "", "system", 12, true),
                    Playlist("i2", "سنتی", "موسیقی اصیل ایرانی", "", "system", 8, true)
                )
                val global = listOf(
                    Playlist("g1", "Global Top 50", "World's most played", "", "system", 50, true),
                    Playlist("g2", "Rock Classics", "Best of Rock", "", "system", 30, true)
                )

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        userPlaylists = userPlaylists,
                        internalMusic = internal,
                        globalMusic = global
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name)
            loadPlaylists()
        }
    }
}

data class PlaylistUiState(
    val userPlaylists: List<Playlist> = emptyList(),
    val internalMusic: List<Playlist> = emptyList(),
    val globalMusic: List<Playlist> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
