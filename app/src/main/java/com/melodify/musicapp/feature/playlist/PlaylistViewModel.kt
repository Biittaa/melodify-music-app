package com.melodify.musicapp.feature.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.melodify.musicapp.domain.model.*

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PlaylistEvent>()

    init {
        loadPlaylists()
        collectEvents()
    }

    private fun collectEvents() {
        viewModelScope.launch {
            _events.collect { event ->
                when (event) {
                    is PlaylistEvent.OnPlaylistClick -> handlePlaylistClick(event.playlistId)
                    is PlaylistEvent.OnRefresh -> loadPlaylists()
                }
            }
        }
    }

    fun onEvent(event: PlaylistEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // ✅ بدون .first() چون خودش List برمی‌گرداند
                val playlists: List<Playlist> = playlistRepository.getUserPlaylists("current_user_id")
                _uiState.update {
                    it.copy(isLoading = false, playlists = playlists)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Error loading playlists")
                }
            }
        }
    }

    private fun handlePlaylistClick(playlistId: String) {
        // Navigate to playlist detail
    }
}