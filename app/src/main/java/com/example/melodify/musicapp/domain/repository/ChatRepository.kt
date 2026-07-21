package com.melodify.musicapp.domain.repository

import androidx.paging.PagingSource
import com.melodify.musicapp.data.local.entity.MessageEntity
import com.melodify.musicapp.domain.model.Conversation
import com.melodify.musicapp.domain.model.Message
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for real-time chat functionality
 * Handles messaging, conversations, read receipts, and song sharing
 * Uses WebSocket/Firestore listeners for real-time updates
 */
interface ChatRepository {

    /**
     * Get all conversations for the current user
     * @return List of Conversation objects
     */
    suspend fun getConversations(): List<Conversation>

    /**
     * Get messages from a specific user (non-paginated)
     * @param userId ID of the other user in the conversation
     * @return List of Message objects
     */
    suspend fun getMessages(userId: String): List<Message>

    /**
     * Get PagingSource for messages of a specific conversation (for Paging3)
     * Uses Room numeric offset pagination with MessageEntity
     * @param userId ID of the other user
     * @return PagingSource for handling pagination
     */
    fun getMessagesPaging(userId: String): PagingSource<Int, MessageEntity>

    /**
     * Send a text message to another user
     * @param receiverId ID of the recipient
     * @param text Message content
     */
    suspend fun sendMessage(receiverId: String, text: String)

    /**
     * Share a song with another user via chat
     * @param receiverId ID of the recipient
     * @param songId ID of the song to share
     */
    suspend fun sendSong(receiverId: String, songId: String)

    /**
     * Mark a message as read (seen)
     * @param messageId ID of the message
     */
    suspend fun markAsRead(messageId: String)

    /**
     * Observe real-time messages from a specific user
     * @param userId ID of the other user
     * @return Flow emitting list of messages in real-time
     */
    fun observeMessages(userId: String): Flow<List<Message>>
}