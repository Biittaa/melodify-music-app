package com.melodify.musicapp.feature.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistUiState(
    val userPlaylists: List<Playlist> = emptyList(),
    val internalMusic: List<Playlist> = emptyList(),
    val globalMusic: List<Playlist> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

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

    fun refresh() {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val userId = currentUserProvider.getCurrentUser()?.id ?: "local_user"

                playlistRepository.getUserPlaylistsFlow(userId).collect { allPlaylists ->
                    // Categorize: ownerId != "system" -> user defined; id starts with "i" -> internal; starts with "g" -> global
                    val user = allPlaylists.filter { it.ownerId != "system" }
                    val internal = allPlaylists.filter { it.id.startsWith("i") }
                    val global = allPlaylists.filter { it.id.startsWith("g") }

                    _uiState.update {
                        it.copy(
                            userPlaylists = user,
                            internalMusic = internal,
                            globalMusic = global,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun createPlaylist(name: String, onResult: (Boolean, String) -> Unit) {
        if (_uiState.value.userPlaylists.any { it.title.equals(name, ignoreCase = true) }) {
            onResult(false, "A playlist with this name already exists")
            return
        }
        viewModelScope.launch {
            playlistRepository.createPlaylist(name)
            onResult(true, "Playlist created")
        }
    }

    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }

    fun renamePlaylist(playlistId: String, newName: String, onResult: (Boolean, String) -> Unit) {
        if (_uiState.value.userPlaylists.any { it.title.equals(newName, ignoreCase = true) && it.id != playlistId }) {
            onResult(false, "A playlist with this name already exists")
            return
        }
        viewModelScope.launch {
            val playlist = _uiState.value.userPlaylists.find { it.id == playlistId }
            if (playlist != null) {
                playlistRepository.updatePlaylist(playlist.copy(title = newName))
                onResult(true, "Playlist renamed")
            }
        }
    }
}