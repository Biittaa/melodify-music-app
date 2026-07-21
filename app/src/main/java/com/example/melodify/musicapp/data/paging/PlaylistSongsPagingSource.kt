package com.melodify.musicapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.melodify.musicapp.data.local.dao.PlaylistSongDao
import com.melodify.musicapp.data.local.entity.PlaylistSongEntity
import com.melodify.musicapp.domain.model.Song

/**
 * PagingSource for getting songs of a specific playlist from Room database
 * Uses numeric offset-based pagination (default for Room)
 * @param playlistSongDao Room DAO for playlist-songs relationship
 * @param playlistId ID of the target playlist
 */
class PlaylistSongsPagingSource(
    private val playlistSongDao: PlaylistSongDao,
    private val playlistId: String
) : PagingSource<Int, PlaylistSongEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlaylistSongEntity> {
        return try {
            val page = params.key ?: 0
            val offset = page * params.loadSize

            val items = playlistSongDao.getPlaylistSongsPaged(
                playlistId = playlistId,
                limit = params.loadSize,
                offset = offset
            )

            LoadResult.Page(
                data = items,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (items.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PlaylistSongEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}