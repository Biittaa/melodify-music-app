package com.example.melodify.musicapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melodify.musicapp.domain.model.Song
import com.example.melodify.musicapp.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val trendingSongs: List<Song> = emptyList(),
    val newestSongs: List<Song> = emptyList(),
    val popularSongs: List<Song> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val trending = songRepository.getTrendingSongs()
                val newest = songRepository.getLatestSongs()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        trendingSongs = trending,
                        newestSongs = newest,
                        popularSongs = trending.shuffled()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}