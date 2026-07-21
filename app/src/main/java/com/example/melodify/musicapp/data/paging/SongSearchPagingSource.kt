package com.example.melodify.musicapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.SongRepository

/**
 * PagingSource for searching songs.
 * Integrates local, remote, and mock results, with fallback pagination to prevent offline failures.
 */
class SongSearchPagingSource(
    private val songRepository: SongRepository,
    private val query: String
) : PagingSource<Int, Song>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        return try {
            val page = params.key ?: 0
            val allSongs = songRepository.searchSongs(query)

            val fromIndex = page * params.loadSize
            val toIndex = minOf(fromIndex + params.loadSize, allSongs.size)

            val items = if (fromIndex < allSongs.size) {
                allSongs.subList(fromIndex, toIndex)
            } else {
                emptyList()
            }

            LoadResult.Page(
                data = items,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (toIndex < allSongs.size) page + 1 else null
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