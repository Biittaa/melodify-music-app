package com.melodify.musicapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.data.local.dao.ConversationDao
import com.melodify.musicapp.data.local.dao.MessageDao
import com.melodify.musicapp.data.local.entity.ConversationEntity
import com.melodify.musicapp.data.local.entity.MessageEntity
import com.melodify.musicapp.data.paging.ChatMessagesPagingSource
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Conversation
import com.melodify.musicapp.domain.model.Message
import com.melodify.musicapp.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val currentUserProvider: CurrentUserProvider
) : ChatRepository {

    override fun getConversationsFlow(): Flow<List<Conversation>> {
        return conversationDao.getAll().map { entities ->
            entities.map { entity ->
                Conversation(
                    id = entity.userId,
                    userId = entity.userId,
                    lastMessage = entity.lastMessage,
                    lastMessageTime = entity.lastMessageTime,
                    unreadCount = entity.unreadCount
                )
            }
        }
    }

    override suspend fun getConversations(): List<Conversation> {
        val entities = conversationDao.getAll().firstOrNull() ?: emptyList()
        return entities.map { entity ->
            Conversation(
                id = entity.userId,
                userId = entity.userId,
                lastMessage = entity.lastMessage,
                lastMessageTime = entity.lastMessageTime,
                unreadCount = entity.unreadCount
            )
        }
    }

    override suspend fun getMessages(userId: String): List<Message> {
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return emptyList()
        val entities = messageDao.getMessagesForUser(userId, currentUserId).firstOrNull() ?: emptyList()
        return entities.map { entity ->
            Message(
                id = entity.id,
                senderId = entity.senderId,
                receiverId = entity.receiverId,
                text = entity.text,
                songId = entity.songId,
                createdAt = entity.createdAt,
                isSeen = entity.isSeen,
                isSent = entity.isSent,
                participants = entity.participants
            )
        }
    }

    override fun getMessagesPaging(userId: String): PagingSource<Int, MessageEntity> {
        val currentUserId = currentUserProvider.getCurrentUser()?.id
            ?: return EmptyPagingSource()
        return ChatMessagesPagingSource(
            messageDao = messageDao,
            userId = userId,
            currentUserId = currentUserId
        )
    }

    override suspend fun sendMessage(receiverId: String, text: String) {
        val currentUser = currentUserProvider.getCurrentUser() ?: return
        val message = Message(
            id = UUID.randomUUID().toString(),
            senderId = currentUser.id,
            receiverId = receiverId,
            text = text,
            songId = null,
            createdAt = System.currentTimeMillis(),
            isSeen = false,
            isSent = false // Start local with 'sending/clock' status
        )

        // Save local immediately in sending state
        messageDao.insert(
            MessageEntity(
                id = message.id,
                senderId = message.senderId,
                receiverId = message.receiverId,
                text = message.text,
                songId = message.songId,
                createdAt = message.createdAt,
                isSeen = message.isSeen,
                isSent = false,
                participants = message.participants
            )
        )
        updateConversation(receiverId, text, System.currentTimeMillis())

        try {
            firestoreDataSource.sendMessage(message)
            // Update local to 'sent'
            messageDao.insert(
                MessageEntity(
                    id = message.id,
                    senderId = message.senderId,
                    receiverId = message.receiverId,
                    text = message.text,
                    songId = message.songId,
                    createdAt = message.createdAt,
                    isSeen = message.isSeen,
                    isSent = true,
                    participants = message.participants
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun sendSong(receiverId: String, songId: String) {
        val currentUser = currentUserProvider.getCurrentUser() ?: return
        val message = Message(
            id = UUID.randomUUID().toString(),
            senderId = currentUser.id,
            receiverId = receiverId,
            text = "🎵 A song was shared",
            songId = songId,
            createdAt = System.currentTimeMillis(),
            isSeen = false,
            isSent = true
        )
        firestoreDataSource.sendMessage(message)
        messageDao.insert(
            MessageEntity(
                id = message.id,
                senderId = message.senderId,
                receiverId = message.receiverId,
                text = message.text,
                songId = message.songId,
                createdAt = message.createdAt,
                isSeen = message.isSeen,
                isSent = true,
                participants = message.participants
            )
        )
        updateConversation(receiverId, "🎵 Song", System.currentTimeMillis())
    }

    override suspend fun markAsRead(messageId: String) {
        firestoreDataSource.markMessageAsSeen(messageId)
        messageDao.markAsSeen(messageId)
    }

    override fun observeMessages(userId: String): Flow<List<Message>> {
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return emptyFlow()
        return messageDao.getMessagesForUser(userId, currentUserId).map { entities ->
            entities.map { entity ->
                Message(
                    id = entity.id,
                    senderId = entity.senderId,
                    receiverId = entity.receiverId,
                    text = entity.text,
                    songId = entity.songId,
                    createdAt = entity.createdAt,
                    isSeen = entity.isSeen,
                    isSent = entity.isSent,
                    participants = entity.participants
                )
            }
        }
    }

    override fun observeTypingStatus(otherUserId: String): Flow<Boolean> {
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return emptyFlow()
        return firestoreDataSource.observeTypingStatus(currentUserId, otherUserId)
    }

    override suspend fun setTypingStatus(otherUserId: String, isTyping: Boolean) {
        val currentUserId = currentUserProvider.getCurrentUser()?.id ?: return
        firestoreDataSource.setTypingStatus(currentUserId, otherUserId, isTyping)
    }

    private suspend fun updateConversation(userId: String, lastMessage: String, time: Long) {
        val conv = ConversationEntity(
            userId = userId,
            lastMessage = lastMessage,
            lastMessageTime = time,
            unreadCount = 0
        )
        conversationDao.insert(conv)
    }

    private class EmptyPagingSource : PagingSource<Int, MessageEntity>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageEntity> {
            return LoadResult.Page(emptyList(), null, null)
        }
        override fun getRefreshKey(state: PagingState<Int, MessageEntity>): Int? = null
    }
}