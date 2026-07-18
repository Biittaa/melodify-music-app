package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Artist
import com.melodify.musicapp.domain.model.Song

interface ArtistRepository {
    suspend fun getArtists(): List<Artist>
    suspend fun getArtist(id: String): Artist
    suspend fun getArtistSongs(id: String): List<Song>
    suspend fun followArtist(id: String)
    suspend fun unfollowArtist(id: String)
}