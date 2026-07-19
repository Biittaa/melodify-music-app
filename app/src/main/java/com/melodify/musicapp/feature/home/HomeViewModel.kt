package com.melodify.musicapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.repository.ArtistRepository
import com.melodify.musicapp.domain.repository.PlaylistRepository
import com.melodify.musicapp.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.melodify.musicapp.domain.model.*

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository,
    private val artistRepository: ArtistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()

    init {
        loadHomeData()
        collectEvents()
    }

    private fun collectEvents() {
        viewModelScope.launch {
            _events.collect { event ->
                when (event) {
                    is HomeEvent.OnSongClick -> handleSongClick(event.songId)
                    is HomeEvent.OnPlaylistClick -> handlePlaylistClick(event.playlistId)
                    is HomeEvent.OnArtistClick -> handleArtistClick(event.artistId)
                    is HomeEvent.OnRefresh -> loadHomeData()
                    is HomeEvent.OnQuickActionClick -> handleQuickAction(event.action)
                }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // ✅ با مشخص کردن نوع داده
                val trending: List<Song> = songRepository.getTrendingSongs() as List<Song>
                val latest: List<Song> = songRepository.getLatestSongs() as List<Song>
                val playlists: List<Playlist> = playlistRepository.getUserPlaylists("current_user_id") as List<Playlist>
                val artists: List<Artist> = artistRepository.getArtists() as List<Artist>
                val carousel = trending.take(5)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        trendingSongs = trending,
                        latestSongs = latest,
                        playlists = playlists,
                        artists = artists,
                        carouselSongs = carousel
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error loading home data"
                    )
                }
            }
        }
    }

    private fun handleSongClick(songId: String) {
        // Navigate to player
    }

    private fun handlePlaylistClick(playlistId: String) {
        // Navigate to playlist detail
    }

    private fun handleArtistClick(artistId: String) {
        // Navigate to artist detail
    }

    private fun handleQuickAction(action: QuickAction) {
        when (action) {
            QuickAction.LIKED_SONGS -> { /* Navigate to liked songs */ }
            QuickAction.RECENTLY_PLAYED -> { /* Navigate to recently played */ }
            QuickAction.MY_PLAYLISTS -> { /* Navigate to my playlists */ }
            QuickAction.TOP_ARTISTS -> { /* Navigate to top artists */ }
        }
    }
}


































//package com.melodify.musicapp.feature.home
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.melodify.musicapp.domain.repository.ArtistRepository
//import com.melodify.musicapp.domain.repository.PlaylistRepository
//import com.melodify.musicapp.domain.repository.SongRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class HomeViewModel @Inject constructor(
//    private val songRepository: SongRepository,
//    private val playlistRepository: PlaylistRepository,
//    private val artistRepository: ArtistRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(HomeUiState())
//    val uiState = _uiState.asStateFlow()
//
//    private val _events = MutableSharedFlow<HomeEvent>()
//
//    init {
//        loadHomeData()
//        collectEvents()
//    }
//
//    private fun collectEvents() {
//        viewModelScope.launch {
//            _events.collect { event ->
//                when (event) {
//                    is HomeEvent.OnSongClick -> handleSongClick(event.songId)
//                    is HomeEvent.OnPlaylistClick -> handlePlaylistClick(event.playlistId)
//                    is HomeEvent.OnArtistClick -> handleArtistClick(event.artistId)
//                    is HomeEvent.OnRefresh -> loadHomeData()
//                    is HomeEvent.OnQuickActionClick -> handleQuickAction(event.action)
//                }
//            }
//        }
//    }
//
//    fun onEvent(event: HomeEvent) {
//        viewModelScope.launch {
//            _events.emit(event)
//        }
//    }
//
//    private fun loadHomeData() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null) }
//
//            try {
//                // ✅ درست: با .first() جمع کن
//                val trending = songRepository.getTrendingSongs().first()
//                val latest = songRepository.getLatestSongs().first()
//                val playlists = playlistRepository.getUserPlaylists("current_user_id").first()
//                val artists = artistRepository.getArtists().first()
//                val carousel = trending.take(5)
//
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        trendingSongs = trending,
//                        latestSongs = latest,
//                        playlists = playlists,
//                        artists = artists,
//                        carouselSongs = carousel
//                    )
//                }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        error = e.message ?: "Error loading home data"
//                    )
//                }
//            }
//        }
//    }
//
//    private fun handleSongClick(songId: String) {
//        // Navigate to player
//    }
//
//    private fun handlePlaylistClick(playlistId: String) {
//        // Navigate to playlist detail
//    }
//
//    private fun handleArtistClick(artistId: String) {
//        // Navigate to artist detail
//    }
//
//    private fun handleQuickAction(action: QuickAction) {
//        when (action) {
//            QuickAction.LIKED_SONGS -> { /* Navigate to liked songs */ }
//            QuickAction.RECENTLY_PLAYED -> { /* Navigate to recently played */ }
//            QuickAction.MY_PLAYLISTS -> { /* Navigate to my playlists */ }
//            QuickAction.TOP_ARTISTS -> { /* Navigate to top artists */ }
//        }
//    }
//}