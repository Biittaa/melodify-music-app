package com.melodify.musicapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.melodify.musicapp.core.common.Constants
import com.melodify.musicapp.domain.model.Song
import kotlinx.coroutines.tasks.await

/**
 * PagingSource for searching songs from Firestore
 * Uses cursor-based pagination with startAfter(lastDocument)
 * @param firestore FirebaseFirestore instance
 * @param query The search query string
 */
class SongSearchPagingSource(
    private val firestore: FirebaseFirestore,
    private val query: String
) : PagingSource<DocumentSnapshot, Song>() {

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Song> {
        return try {
            // Build base query
            val baseQuery = firestore.collection(Constants.FIREBASE_SONGS_COLLECTION)
                .whereArrayContains("searchKeywords", query.lowercase())
                .orderBy("title")
                .limit(params.loadSize.toLong())

            // Apply startAfter if we have a last document
            val querySnapshot = if (params.key != null) {
                baseQuery.startAfter(params.key!!).get().await()
            } else {
                baseQuery.get().await()
            }

            val songs = querySnapshot.documents.mapNotNull { it.toObject(Song::class.java) }
            val lastDocument = querySnapshot.documents.lastOrNull()

            LoadResult.Page(
                data = songs,
                prevKey = null, // Firestore doesn't support previous pages easily
                nextKey = if (songs.isNotEmpty()) lastDocument else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Song>): DocumentSnapshot? {
        // Return the last document of the last page to refresh from
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}