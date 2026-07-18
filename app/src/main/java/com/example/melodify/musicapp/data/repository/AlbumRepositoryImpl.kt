package com.melodify.musicapp.data.repository

import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Album
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.AlbumRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : AlbumRepository {

    override suspend fun getAlbums(): List<Album> {
        // should make a collection of albums in Firebase
        // optional, implement if needed
        return emptyList()
    }

    override suspend fun getAlbum(albumId: String): Album {
        // return firestoreDataSource.getAlbum(albumId)
        throw Exception("Not implemented yet")
    }

    override suspend fun getAlbumSongs(albumId: String): List<Song> {
        // return firestoreDataSource.getAlbumSongs(albumId)
        return emptyList()
    }
}