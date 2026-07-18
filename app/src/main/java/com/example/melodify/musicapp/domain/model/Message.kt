package com.melodify.musicapp.domain.model

/**
 * Data model for chat messages
 * Supports text messages and song sharing
 *
 * @property id Unique message identifier
 * @property senderId ID of the user who sent the message
 * @property receiverId ID of the user who receives the message
 * @property text Message content (text or empty for song shares)
 * @property songId ID of the shared song (null if not a song share)
 * @property createdAt Timestamp when the message was created
 * @property isSeen Whether the message has been read by the receiver
 * @property participants List of user IDs involved in this conversation for efficient Firestore queries
 */
data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val songId: String? = null,
    val createdAt: Long,
    val isSeen: Boolean = false,
    val participants: List<String> = listOf(senderId, receiverId)
)