package com.melodify.musicapp.data.repository

import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Album
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.AlbumRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AlbumRepository
 * Manages album data from Firestore
 */
@Singleton
class AlbumRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : AlbumRepository {

    override suspend fun getAlbums(): List<Album> {
        // TODO: Implement albums collection in Firestore
        return emptyList()
    }

    override suspend fun getAlbum(albumId: String): Album {
        // TODO: Implement getAlbum in Firestore
        throw Exception("Not implemented yet")
    }

    override suspend fun getAlbumSongs(albumId: String): List<Song> {
        // TODO: Implement getAlbumSongs in Firestore
        return emptyList()
    }
}