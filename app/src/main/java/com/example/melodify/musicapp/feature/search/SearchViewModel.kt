package com.melodify.musicapp.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.melodify.musicapp.domain.model.SearchFilter
import com.melodify.musicapp.domain.model.SearchHistory
import com.example.melodify.musicapp.domain.model.SearchResult
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.SearchRepository
import com.melodify.musicapp.domain.repository.SongRepository
import com.melodify.musicapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val selectedFilter: SearchFilter = SearchFilter.All,
    val history: List<SearchHistory> = emptyList(),
    val localSongs: List<Song> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val songRepository: SongRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults: Flow<PagingData<SearchResult>> = _uiState
        .map { it.query to it.selectedFilter }
        .distinctUntilChanged()
        .debounce(300L)
        .flatMapLatest { (query, filter) ->
            if (query.isBlank()) {
                flowOf(PagingData.empty())
            } else {
                flow {
                    val resultsList = mutableListOf<SearchResult>()

                    when (filter) {
                        SearchFilter.All -> {
                            val songs = songRepository.searchSongs(query)
                            val users = userRepository.searchUsers(query)

                            if (songs.isNotEmpty()) {
                                resultsList.add(SearchResult.Header("Songs"))
                                resultsList.addAll(songs.map { SearchResult.SongResult(it) })
                            }
                            if (users.isNotEmpty()) {
                                resultsList.add(SearchResult.Header("Users"))
                                resultsList.addAll(users.map { SearchResult.UserResult(it) })
                            }
                        }
                        SearchFilter.Songs -> {
                            val songs = songRepository.searchSongs(query)
                            if (songs.isNotEmpty()) {
                                resultsList.add(SearchResult.Header("Songs"))
                                resultsList.addAll(songs.map { SearchResult.SongResult(it) })
                            }
                        }
                        SearchFilter.Artists -> {
                            val songs = songRepository.searchSongs(query)
                            if (songs.isNotEmpty()) {
                                resultsList.add(SearchResult.Header("Artists"))
                                resultsList.addAll(songs.map { SearchResult.SongResult(it) })
                            }
                        }
                        SearchFilter.Users -> {
                            val users = userRepository.searchUsers(query)
                            if (users.isNotEmpty()) {
                                resultsList.add(SearchResult.Header("Users"))
                                resultsList.addAll(users.map { SearchResult.UserResult(it) })
                            }
                        }
                    }

                    _uiState.update {
                        it.copy(totalCount = resultsList.filterNot { item -> item is SearchResult.Header }.size)
                    }

                    if (query.isNotBlank()) {
                        searchRepository.saveHistory(query)
                    }

                    emit(PagingData.from(resultsList))
                }
            }
        }
        .cachedIn(viewModelScope)

    init {
        loadHistoryAndLocal()
    }

    private fun loadHistoryAndLocal() {
        viewModelScope.launch {
            val historyList = searchRepository.getHistory()
            val local = songRepository.getLocalMusic()
            _uiState.update { it.copy(history = historyList, localSongs = local) }
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun onFilterChange(newFilter: SearchFilter) {
        _uiState.update { it.copy(selectedFilter = newFilter) }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchRepository.clearHistory()
            _uiState.update { it.copy(history = emptyList()) }
        }
    }
}