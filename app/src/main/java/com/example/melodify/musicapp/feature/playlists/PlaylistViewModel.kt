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
                val userId = currentUserProvider.getCurrentUser()?.id ?: "local_user"
                val userPlaylists = playlistRepository.getUserPlaylists(userId)

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

    fun createPlaylist(name: String, onResult: (Boolean, String) -> Unit) {
        if (_uiState.value.userPlaylists.any { it.title.equals(name, ignoreCase = true) }) {
            onResult(false, "پلی‌لیستی با این نام وجود دارد")
            return
        }
        viewModelScope.launch {
            playlistRepository.createPlaylist(name)
            loadPlaylists()
            onResult(true, "پلی‌لیست ایجاد شد")
        }
    }

    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
            loadPlaylists()
        }
    }

    fun renamePlaylist(playlistId: String, newName: String, onResult: (Boolean, String) -> Unit) {
        if (_uiState.value.userPlaylists.any { it.title.equals(newName, ignoreCase = true) && it.id != playlistId }) {
            onResult(false, "پلی‌لیستی با این نام وجود دارد")
            return
        }
        viewModelScope.launch {
            val playlist = _uiState.value.userPlaylists.find { it.id == playlistId }
            if (playlist != null) {
                playlistRepository.updatePlaylist(playlist.copy(title = newName))
                loadPlaylists()
                onResult(true, "نام پلی‌لیست تغییر کرد")
            }
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
