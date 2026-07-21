package com.melodify.musicapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.melodify.musicapp.data.local.dao.MessageDao
import com.melodify.musicapp.data.local.entity.MessageEntity

/**
 * PagingSource for chat messages from Room database
 * Uses numeric offset-based pagination
 * @param messageDao Room DAO for messages
 * @param userId ID of the other user in the conversation
 * @param currentUserId ID of the logged-in user
 */
class ChatMessagesPagingSource(
    private val messageDao: MessageDao,
    private val userId: String,
    private val currentUserId: String
) : PagingSource<Int, MessageEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageEntity> {
        return try {
            val page = params.key ?: 0
            val offset = page * params.loadSize

            val items = messageDao.getMessagesPaged(
                userId = userId,
                currentUserId = currentUserId,
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

    override fun getRefreshKey(state: PagingState<Int, MessageEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}