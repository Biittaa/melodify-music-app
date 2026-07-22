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
        // Mock data aligned with UserMockData IDs to ensure navigation works
        return listOf(
            Artist("user_001", "Armin Rahimi", "https://picsum.photos/seed/armin/200/200", 1250),
            Artist("user_002", "Sara vibes", "https://picsum.photos/seed/sara/200/200", 870),
            Artist("user_003", "DJ Nima", "https://picsum.photos/seed/nima/200/200", 5400)
        )
    }

    override suspend fun getArtist(id: String): Artist {
        return getArtists().find { it.id == id } ?: getArtists().first()
    }

    override suspend fun getArtistSongs(id: String): List<Song> {
        return emptyList()
    }

    override suspend fun followArtist(id: String) {
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
        try { firestoreDataSource.followUser(currentUserId, id) } catch (e: Exception) {}
    }

    override suspend fun unfollowArtist(id: String) {
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
        try { firestoreDataSource.unfollowUser(currentUserId, id) } catch (e: Exception) {}
    }
}
