package com.melodify.musicapp.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.melodify.musicapp.domain.model.SearchResult
import com.melodify.musicapp.domain.model.SearchHistory
import com.melodify.musicapp.domain.model.SearchFilter
import com.melodify.musicapp.domain.repository.SearchRepository
import com.melodify.musicapp.domain.repository.SongRepository
import com.melodify.musicapp.domain.repository.UserRepository
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val history: List<SearchHistory> = emptyList(),
    val selectedFilter: SearchFilter = SearchFilter.All,
    val localSongs: List<Song> = emptyList(),
    val totalCount: Int = 0
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val songRepository: SongRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(SearchFilter.All)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<PagingData<SearchResult>> =
        combine(
            _searchQuery.debounce(500),
            _selectedFilter
        ) { query, filter ->
            query to filter
        }
            .flatMapLatest { (query, filter) ->
                if(query.isBlank()) {
                    _uiState.update { it.copy(totalCount = 0) }
                    flowOf(PagingData.empty())
                } else {
                    flow {
                        val finalResults = mutableListOf<SearchResult>()
                        
                        when(filter) {
                            SearchFilter.All -> {
                                val songs = songRepository.searchSongs(query)
                                val users = userRepository.searchUsers(query)
                                
                                if (songs.isNotEmpty()) {
                                    finalResults.add(SearchResult.Header("Songs"))
                                    addSongGroup(finalResults, songs)
                                }
                                
                                if (users.isNotEmpty()) {
                                    finalResults.add(SearchResult.Header("Users"))
                                    finalResults.addAll(users.map { SearchResult.UserResult(it) })
                                }
                                _uiState.update { it.copy(totalCount = songs.size + users.size) }
                            }
                            SearchFilter.Songs -> {
                                val songs = songRepository.searchSongs(query)
                                addSongGroup(finalResults, songs)
                                _uiState.update { it.copy(totalCount = songs.size) }
                            }
                            SearchFilter.Users, SearchFilter.Artists -> {
                                val users = userRepository.searchUsers(query)
                                finalResults.addAll(users.map { SearchResult.UserResult(it) })
                                _uiState.update { it.copy(totalCount = users.size) }
                            }
                        }
                        emit(PagingData.from(finalResults))
                    }
                }
            }
            .cachedIn(viewModelScope)

    private fun addSongGroup(results: MutableList<SearchResult>, songs: List<Song>) {
        val localSongs = songs.filter { it.id.startsWith("local_") }
        val otherSongs = songs.filter { !it.id.startsWith("local_") }
        
        if (localSongs.isNotEmpty()) {
            results.add(SearchResult.Header("Local"))
            results.addAll(localSongs.map { SearchResult.SongResult(it) })
        }
        if (otherSongs.isNotEmpty()) {
            results.add(SearchResult.Header("Others"))
            results.addAll(otherSongs.map { SearchResult.SongResult(it) })
        }
    }

    init {
        loadHistory()
        loadLocalSongs()
    }

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