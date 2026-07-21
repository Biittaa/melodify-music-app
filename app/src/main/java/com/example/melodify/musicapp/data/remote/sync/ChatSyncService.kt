package com.melodify.musicapp.data.remote.sync

import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.data.local.dao.MessageDao
import com.melodify.musicapp.data.local.entity.MessageEntity
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service responsible for real-time chat synchronization
 * Listens to Firestore for all messages involving the current user
 * and stores them in Room for offline access.
 *
 * This service should be started when the user logs in and stopped on logout.
 * It runs in the background as long as the app is alive (via injected CoroutineScope).
 */
@Singleton
class ChatSyncService @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val messageDao: MessageDao,
    private val currentUserProvider: CurrentUserProvider
) {
    // Use a var so we can cancel and recreate it
    private var syncScope: CoroutineScope? = null
    private var currentListeningUserId: String? = null

    /**
     * Start listening to all messages for the currently logged-in user
     * This method is idempotent - calling it multiple times with the same user does nothing
     */
    @Synchronized
    fun startListening() {
        val userId = currentUserProvider.getCurrentUser()?.id ?: run {
            stopListening()
            return
        }

        if (currentListeningUserId == userId && syncScope != null) {
            return // Already listening for this user
        }

        stopListening() // Clean up previous
        currentListeningUserId = userId

        val newScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        syncScope = newScope

        newScope.launch {
            firestoreDataSource.observeAllMessagesForUser(userId)
                .collectLatest { messages ->
                    // Convert domain messages to entities and save to Room
                    val entities = messages.map { message ->
                        MessageEntity(
                            id = message.id,
                            senderId = message.senderId,
                            receiverId = message.receiverId,
                            text = message.text,
                            songId = message.songId,
                            createdAt = message.createdAt,
                            isSeen = message.isSeen,
                            isSent = message.senderId == userId,
                            participants = message.participants
                        )
                    }
                    // Batch insert to Room (replaces old data if IDs conflict)
                    if (entities.isNotEmpty()) {
                        messageDao.insertAll(entities)
                    }
                }
        }
    }

    /**
     * Stop listening to Firestore and cancel the ongoing sync coroutine
     */
    @Synchronized
    fun stopListening() {
        syncScope?.cancel()
        syncScope = null
        currentListeningUserId = null
    }

    /**
     * Restart the sync service (useful for when user changes or network state changes)
     */
    fun restartListening() {
        stopListening()
        startListening()
    }
}