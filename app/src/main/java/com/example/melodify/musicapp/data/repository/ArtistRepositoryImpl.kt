package com.melodify.musicapp.data.repository

import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Artist
import com.melodify.musicapp.domain.repository.ArtistRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ArtistRepository
 * Manages artist data and follow/unfollow from Firestore
 */
@Singleton
class ArtistRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val currentUserProvider: CurrentUserProvider
) : ArtistRepository {

    override suspend fun getArtists(): List<Artist> {
        // TODO: Implement artists collection in Firestore
        return emptyList()
    }

    override suspend fun getArtist(id: String): Artist {
        // TODO: Implement getArtist in Firestore
        throw Exception("Not implemented yet")
    }

    override suspend fun getArtistSongs(id: String): List<Song> {
        // TODO: Implement getArtistSongs in Firestore
        return emptyList()
    }

    override suspend fun followArtist(id: String) {
        // Similar to follow user implementation
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
        // firestoreDataSource.followArtist(currentUserId, id)
    }

    override suspend fun unfollowArtist(id: String) {
        // Similar to unfollow user implementation
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
        // firestoreDataSource.unfollowArtist(currentUserId, id)
    }
}