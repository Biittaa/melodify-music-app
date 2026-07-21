package com.melodify.musicapp.feature.search

import com.melodify.musicapp.domain.model.Artist
import com.melodify.musicapp.domain.model.Album
import com.melodify.musicapp.domain.model.SearchHistory

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val songs: List<Song> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val history: List<SearchHistory> = emptyList(),
    val selectedFilter: SearchFilter = SearchFilter.ALL,
    val error: String? = null
)

enum class SearchFilter {
    ALL,
    SONGS,
    ARTISTS,
    ALBUMS
}

sealed class SearchEvent {
    data class OnQueryChange(val query: String) : SearchEvent()
    data class OnSearch(val query: String) : SearchEvent()
    data class OnFilterChange(val filter: SearchFilter) : SearchEvent()
    data class OnHistoryClick(val query: String) : SearchEvent()
    data class OnHistoryDelete(val historyId: String) : SearchEvent()
    object OnClearHistory : SearchEvent()
    data class OnSongClick(val songId: String) : SearchEvent()
    data class OnArtistClick(val artistId: String) : SearchEvent()
    data class OnAlbumClick(val albumId: String) : SearchEvent()
}