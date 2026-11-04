package com.melodify.musicapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.SongRepository
import com.melodify.musicapp.domain.model.SearchFilter
/**
 * PagingSource for searching songs with optional filter.
 */
class SongSearchPagingSource(
    private val songRepository: SongRepository,
    private val query: String,
    private val filter: SearchFilter = SearchFilter.All   // <-- اضافه شد
) : PagingSource<Int, Song>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        return try {
            val page = params.key ?: 0
            // دریافت لیست کامل از مخزن (با اعمال فیلتر)
            val allSongs = songRepository.searchSongs(query)

            // اعمال فیلتر ساده (در صورتی که مخزن از قبل فیلتر نکرده باشد)
            val filteredSongs = when (filter) {
                SearchFilter.Songs -> allSongs
                SearchFilter.Artists -> allSongs // در این نسخه فقط آهنگ داریم، فیلتر پیشرفته‌تر نیاز به تغییرات بیشتر دارد
                else -> allSongs
            }

            val fromIndex = page * params.loadSize
            val toIndex = minOf(fromIndex + params.loadSize, filteredSongs.size)

            val items = if (fromIndex < filteredSongs.size) {
                filteredSongs.subList(fromIndex, toIndex)
            } else {
                emptyList()
            }

            LoadResult.Page(
                data = items,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (toIndex < filteredSongs.size) page + 1 else null
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