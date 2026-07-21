package com.melodify.musicapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.melodify.musicapp.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Chat Messages
 * Handles storing and retrieving messages for offline support
 */
@Dao
interface MessageDao {

    /**
     * Insert a single message
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    /**
     * Insert multiple messages at once (batch insert for sync)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)

    /**
     * Update an existing message (e.g., mark as seen)
     */
    @Update
    suspend fun update(message: MessageEntity)

    /**
     * Get messages between two users as a Flow (for real-time observation from Room)
     * Messages are ordered from oldest to newest for proper chat display
     */
    @Query("""
        SELECT * FROM messages 
        WHERE (senderId = :userId AND receiverId = :currentUserId) 
           OR (senderId = :currentUserId AND receiverId = :userId)
        ORDER BY createdAt ASC
    """)
    fun getMessagesForUser(userId: String, currentUserId: String): Flow<List<MessageEntity>>

    /**
     * Get paginated messages between two users (for Paging3)
     * Messages are ordered from newest to oldest for efficient scrolling
     */
    @Query("""
        SELECT * FROM messages 
        WHERE (senderId = :userId AND receiverId = :currentUserId) 
           OR (senderId = :currentUserId AND receiverId = :userId)
        ORDER BY createdAt DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getMessagesPaged(
        userId: String,
        currentUserId: String,
        limit: Int,
        offset: Int
    ): List<MessageEntity>

    /**
     * Get all messages involving a specific user across all conversations
     * Used by ChatSyncService to sync all messages at once
     */
    @Query("""
        SELECT * FROM messages 
        WHERE senderId = :userId OR receiverId = :userId
        ORDER BY createdAt ASC
    """)
    fun getAllMessagesForUser(userId: String): Flow<List<MessageEntity>>

    /**
     * Mark a specific message as seen/read
     */
    @Query("UPDATE messages SET isSeen = 1 WHERE id = :messageId")
    suspend fun markAsSeen(messageId: String)

    /**
     * Delete a specific message
     */
    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun delete(messageId: String)

    /**
     * Delete all messages between two users (clear conversation)
     */
    @Query("""
        DELETE FROM messages 
        WHERE (senderId = :userId AND receiverId = :currentUserId) 
           OR (senderId = :currentUserId AND receiverId = :userId)
    """)
    suspend fun deleteConversation(userId: String, currentUserId: String)
}