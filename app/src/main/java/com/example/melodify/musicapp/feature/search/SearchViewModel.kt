package com.melodify.musicapp.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.melodify.musicapp.domain.model.SearchHistory
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.SearchRepository
import com.melodify.musicapp.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.melodify.musicapp.domain.model.SearchFilter


data class SearchUiState(
    val query: String = "",
    val history: List<SearchHistory> = emptyList(),
    val selectedFilter: SearchFilter = SearchFilter.All,
    val localSongs: List<Song> = emptyList() // <-- Add this line
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(SearchFilter.All)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<PagingData<Song>> = combine(
        _searchQuery.debounce(500L).distinctUntilChanged(),
        _selectedFilter
    ) { query, filter ->
        query to filter
    }.flatMapLatest { (query, filter) ->
        if (query.isBlank()) {
            flowOf(PagingData.empty())
        } else {
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = {
                    songRepository.searchSongsPaging(query, filter) // <-- ارسال فیلتر
                }
            ).flow.cachedIn(viewModelScope)
        }
    }

    init {
        loadHistory()
        loadLocalSongs() // <-- Add this line
    }

    // Add this new function:
    private fun loadLocalSongs() {
        viewModelScope.launch {
            val songs = songRepository.getLocalMusic()
            _uiState.update { it.copy(localSongs = songs) }
        }
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(query = query) }
        if (query.isNotBlank()) {
            viewModelScope.launch { searchRepository.saveHistory(query) }
        }
    }

    fun onFilterChange(filter: SearchFilter) {
        _selectedFilter.value = filter
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val history = searchRepository.getHistory()
            _uiState.update { it.copy(history = history) }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchRepository.clearHistory()
            loadHistory()
        }
    }
}