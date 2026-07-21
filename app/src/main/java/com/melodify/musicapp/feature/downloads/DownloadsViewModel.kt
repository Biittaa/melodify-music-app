package com.melodify.musicapp.feature.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DownloadsEvent>()

    init {
        loadDownloads()
        collectEvents()
        observeDownloads()
    }

    private fun collectEvents() {
        viewModelScope.launch {
            _events.collect { event ->
                when (event) {
                    is DownloadsEvent.OnDeleteDownload -> deleteDownload(event.songId)
                    is DownloadsEvent.OnSongClick -> handleSongClick(event.songId)
                    is DownloadsEvent.OnRefresh -> loadDownloads()
                }
            }
        }
    }

    fun onEvent(event: DownloadsEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    private fun observeDownloads() {
        viewModelScope.launch {
            downloadRepository.observeDownloads().collect { downloads ->
                _uiState.update { it.copy(downloads = downloads) }
            }
        }
    }

    private fun loadDownloads() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val downloads = downloadRepository.getDownloads()
                _uiState.update {
                    it.copy(isLoading = false, downloads = downloads)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    private fun deleteDownload(songId: String) {
        viewModelScope.launch {
            try {
                downloadRepository.delete(songId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun handleSongClick(songId: String) { /* Navigate */ }
}