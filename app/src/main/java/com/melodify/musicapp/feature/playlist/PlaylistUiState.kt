//package com.melodify.musicapp.feature.playlist
//
//import com.melodify.musicapp.domain.model.Playlist
//
//data class PlaylistUiState(
//    val isLoading: Boolean = false,
//    val playlists: List<Playlist> = emptyList(),
//    val error: String? = null
//)
//
//sealed class PlaylistEvent {
//    data class OnPlaylistClick(val playlistId: String) : PlaylistEvent()
//    object OnRefresh : PlaylistEvent()
//}

package com.melodify.musicapp.feature.playlist

import com.melodify.musicapp.domain.model.Playlist

data class PlaylistUiState(
    val isLoading: Boolean = false,
    val playlists: List<Playlist> = emptyList(),
    val error: String? = null
)

sealed class PlaylistEvent {
    data class OnPlaylistClick(val playlistId: String) : PlaylistEvent()
    object OnRefresh : PlaylistEvent()
}