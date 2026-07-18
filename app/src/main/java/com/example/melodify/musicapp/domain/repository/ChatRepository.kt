package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Conversation
import com.melodify.musicapp.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getConversations(): List<Conversation>
    suspend fun getMessages(userId: String): List<Message>
    suspend fun sendMessage(receiverId: String, text: String)
    suspend fun sendSong(receiverId: String, songId: String)
    suspend fun markAsRead(messageId: String)
    fun observeMessages(userId: String): Flow<List<Message>>  // Real-time

    fun getMessagesPaging(userId: String): PagingSource<Int, Message>
}