package com.melodify.musicapp.feature.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.Download
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.DownloadRepository
import com.melodify.musicapp.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadsUiState(
    val downloadedSongs: List<Song> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadsUiState())
    val uiState: StateFlow<DownloadsUiState> = _uiState.asStateFlow()

    init {
        loadDownloads()
    }

    private fun loadDownloads() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            downloadRepository.observeDownloads().collectLatest { downloads ->
                val songs = downloads.map { songRepository.getSong(it.songId) }
                _uiState.update { it.copy(downloadedSongs = songs, isLoading = false) }
            }
        }
    }

    fun deleteDownload(songId: String) {
        viewModelScope.launch {
            downloadRepository.delete(songId)
        }
    }
}