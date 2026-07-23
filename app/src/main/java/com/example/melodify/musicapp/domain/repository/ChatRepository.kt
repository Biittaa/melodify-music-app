package com.melodify.musicapp.domain.repository

import androidx.paging.PagingSource
import com.melodify.musicapp.data.local.entity.MessageEntity
import com.melodify.musicapp.domain.model.Conversation
import com.melodify.musicapp.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getConversationsFlow(): Flow<List<Conversation>> // Flow added for Live reactive updates
    suspend fun getConversations(): List<Conversation>
    suspend fun getMessages(userId: String): List<Message>
    fun getMessagesPaging(userId: String): PagingSource<Int, MessageEntity>
    suspend fun sendMessage(receiverId: String, text: String)
    suspend fun sendSong(receiverId: String, songId: String)
    suspend fun markAsRead(messageId: String)
    fun observeMessages(userId: String): Flow<List<Message>>

    // Live DM typing contract
    fun observeTypingStatus(otherUserId: String): Flow<Boolean>
    suspend fun setTypingStatus(otherUserId: String, isTyping: Boolean)
    suspend fun markConversationAsRead(userId: String)
}