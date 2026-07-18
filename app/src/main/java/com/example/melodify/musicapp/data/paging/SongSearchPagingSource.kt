package com.melodify.musicapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.melodify.musicapp.domain.model.Song
import kotlinx.coroutines.tasks.await

class SongSearchPagingSource(
    private val firestore: FirebaseFirestore,
    private val query: String
) : PagingSource<Int, Song>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        return try {
            val page = params.key ?: 1
            val limit = params.loadSize
            val startIndex = (page - 1) * limit

            // using startAt and limit for pagination
            val querySnapshot = firestore.collection("songs")
                .whereArrayContains("searchKeywords", query.lowercase())
                .orderBy("title")
                .startAt(startIndex.toLong())
                .limit(limit.toLong())
                .get()
                .await()

            val songs = querySnapshot.documents.mapNotNull { it.toObject(Song::class.java) }
            LoadResult.Page(
                data = songs,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (songs.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}