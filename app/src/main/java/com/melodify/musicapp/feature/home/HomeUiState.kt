package com.melodify.musicapp.feature.home

import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.domain.model.Artist

data class HomeUiState(
    val isLoading: Boolean = false,
    val trendingSongs: List<Song> = emptyList(),
    val latestSongs: List<Song> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val carouselSongs: List<Song> = emptyList(),
    val error: String? = null
)

sealed class HomeEvent {
    data class OnSongClick(val songId: String) : HomeEvent()
    data class OnPlaylistClick(val playlistId: String) : HomeEvent()
    data class OnArtistClick(val artistId: String) : HomeEvent()
    object OnRefresh : HomeEvent()
    data class OnQuickActionClick(val action: QuickAction) : HomeEvent()
}

enum class QuickAction {
    LIKED_SONGS, RECENTLY_PLAYED, MY_PLAYLISTS, TOP_ARTISTS
}