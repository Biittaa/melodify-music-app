package com.melodify.musicapp.feature.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.domain.model.Artist
import com.melodify.musicapp.domain.repository.ArtistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val artistRepository: ArtistRepository
) : ViewModel() {

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadArtists()
    }

    fun loadArtists() {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                // Use real artist data when available; fallback to dummy
                val list = artistRepository.getArtists()
                if (list.isNotEmpty()) {
                    _artists.update { list }
                } else {
                    _artists.update { getDummyArtists() }
                }
            } catch (e: Exception) {
                _artists.update { getDummyArtists() }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    private fun getDummyArtists(): List<Artist> {
        return listOf(
            Artist("1", "Artist One", "", 1000),
            Artist("2", "Artist Two", "", 500)
        )
    }
}