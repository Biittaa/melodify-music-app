package com.melodify.musicapp.data.repository

import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Artist
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.ArtistRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val currentUserProvider: CurrentUserProvider
) : ArtistRepository {

    override suspend fun getArtists(): List<Artist> {
        // should make a collection of artists
        // optional, implement if needed
        return emptyList()
    }

    override suspend fun getArtist(id: String): Artist {
        // return firestoreDataSource.getArtist(id)
        throw Exception("Not implemented yet")
    }

    override suspend fun getArtistSongs(id: String): List<Song> {
        // return firestoreDataSource.getArtistSongs(id)
        return emptyList()
    }

    override suspend fun followArtist(id: String) {
        // like follow user
    }

    override suspend fun unfollowArtist(id: String) {
        // like unfollow user
    }
}