package com.melodify.musicapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SongListUiState(
    val isLoading: Boolean = false,
    val songs: List<Song> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SongListViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SongListUiState())
    val uiState: StateFlow<SongListUiState> = _uiState.asStateFlow()

    fun loadSongs(category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val songs = when (category) {
                    "Trending" -> songRepository.getTrendingSongs()
                    "New Releases" -> songRepository.getLatestSongs()
                    "My Local Music" -> songRepository.getLocalMusic()
                    else -> songRepository.getTrendingSongs()
                }
                _uiState.update { it.copy(isLoading = false, songs = songs) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
