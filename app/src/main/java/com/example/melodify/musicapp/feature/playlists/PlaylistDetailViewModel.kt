package com.melodify.musicapp.feature.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.PlaylistRepository
import com.melodify.musicapp.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistDetailUiState(
    val playlist: Playlist? = null,
    val songs: List<Song> = emptyList(),
    val allAvailableSongs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isShuffle: Boolean = false,
    val searchQuery: String = "",
    val selectedSongIds: Set<String> = emptySet()
)

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    private var currentPlaylistId: String? = null

    fun loadPlaylistDetails(playlistId: String) {
        currentPlaylistId = playlistId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Fetch actual playlist from repository
                val playlists = playlistRepository.getUserPlaylists("any")
                val playlist = playlists.find { it.id == playlistId }
                
                val songs = playlistRepository.getPlaylistSongs(playlistId)
                val allSongs = songRepository.searchSongs("")
                
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        songs = songs, 
                        playlist = playlist,
                        allAvailableSongs = allSongs.filter { s -> !songs.any { it.id == s.id } }
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleSongSelection(songId: String) {
        _uiState.update { state ->
            val newSelection = if (state.selectedSongIds.contains(songId)) {
                state.selectedSongIds - songId
            } else {
                state.selectedSongIds + songId
            }
            state.copy(selectedSongIds = newSelection)
        }
    }

    fun addSelectedSongs() {
        val playlistId = currentPlaylistId ?: return
        val songIds = uiState.value.selectedSongIds.toList()
        if (songIds.isEmpty()) return

        viewModelScope.launch {
            playlistRepository.addSongs(playlistId, songIds)
            _uiState.update { it.copy(selectedSongIds = emptySet(), searchQuery = "") }
            loadPlaylistDetails(playlistId)
        }
    }

    fun addSong(songId: String) {
        val playlistId = currentPlaylistId ?: return
        viewModelScope.launch {
            playlistRepository.addSong(playlistId, songId)
            loadPlaylistDetails(playlistId)
        }
    }

    fun removeSong(songId: String) {
        val playlistId = currentPlaylistId ?: return
        viewModelScope.launch {
            playlistRepository.removeSong(playlistId, songId)
            loadPlaylistDetails(playlistId)
        }
    }

    fun toggleShuffle(force: Boolean? = null) {
        _uiState.update { it.copy(isShuffle = force ?: !it.isShuffle) }
    }

    fun moveSong(fromIndex: Int, toIndex: Int) {
        _uiState.update { state ->
            val newList = state.songs.toMutableList()
            if (fromIndex in newList.indices && toIndex in newList.indices) {
                val item = newList.removeAt(fromIndex)
                newList.add(toIndex, item)
                
                // Persist new order
                viewModelScope.launch {
                    currentPlaylistId?.let { id ->
                        playlistRepository.updateSongsOrder(id, newList.map { it.id })
                    }
                }

                state.copy(songs = newList)
            } else {
                state
            }
        }
    }
}