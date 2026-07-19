package com.melodify.musicapp.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.repository.SearchRepository
import com.melodify.musicapp.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SearchEvent>()

    private var searchJob: Job? = null

    init {
        loadSearchHistory()
        collectEvents()
    }

    private fun collectEvents() {
        viewModelScope.launch {
            _events.collect { event ->
                when (event) {
                    is SearchEvent.OnQueryChange -> handleQueryChange(event.query)
                    is SearchEvent.OnSearch -> performSearch(event.query)
                    is SearchEvent.OnFilterChange -> handleFilterChange(event.filter)
                    is SearchEvent.OnHistoryClick -> handleHistoryClick(event.query)
                    is SearchEvent.OnHistoryDelete -> deleteHistory(event.historyId)
                    is SearchEvent.OnClearHistory -> clearHistory()
                    is SearchEvent.OnSongClick -> handleSongClick(event.songId)
                    is SearchEvent.OnArtistClick -> handleArtistClick(event.artistId)
                    is SearchEvent.OnAlbumClick -> handleAlbumClick(event.albumId)
                }
            }
        }
    }

    fun onEvent(event: SearchEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    private fun handleQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            if (query.length >= 2) {
                performSearch(query)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        songs = emptyList(),
                        artists = emptyList(),
                        albums = emptyList()
                    )
                }
            }
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) return@launch

            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val songs = searchRepository.searchSongs(query)
                val artists = searchRepository.searchArtists(query)
                val albums = searchRepository.searchAlbums(query)

                searchRepository.saveHistory(query)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        songs = songs,
                        artists = artists,
                        albums = albums
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Search failed"
                    )
                }
            }
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            try {
                val history = searchRepository.getHistory()
                _uiState.update { it.copy(history = history) }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun handleFilterChange(filter: SearchFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    private fun handleHistoryClick(query: String) {
        _uiState.update { it.copy(query = query) }
        performSearch(query)
    }

    private fun deleteHistory(historyId: String) {
        viewModelScope.launch {
            try {
                val newHistory = _uiState.value.history.filter { it.id != historyId }
                _uiState.update { it.copy(history = newHistory) }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun clearHistory() {
        viewModelScope.launch {
            try {
                searchRepository.clearHistory()
                _uiState.update { it.copy(history = emptyList()) }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun handleSongClick(songId: String) {
        // Navigate to player
    }

    private fun handleArtistClick(artistId: String) {
        // Navigate to artist detail
    }

    private fun handleAlbumClick(albumId: String) {
        // Navigate to album detail
    }
}