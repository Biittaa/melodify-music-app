//package com.melodify.musicapp.domain.model
//
///**
// * Data model for chat messages
// * Supports text messages, read states, and delivery status indicators
// */
//data class Message(
//    val id: String,
//    val senderId: String,
//    val receiverId: String,
//    val text: String,
//    val songId: String? = null,
//    val createdAt: Long,
//    val isSeen: Boolean = false,
//    val isSent: Boolean = true, // Added isSent back for handling transition sending -> sent
//    val participants: List<String> = listOf(senderId, receiverId)
//)

package com.melodify.musicapp.domain.model

/**
 * Data model for chat messages
 * Supports text messages, read states, and delivery status indicators
 */
data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val songId: String? = null,
    val createdAt: Long = 0L,
    val isSeen: Boolean = false,
    val isSent: Boolean = true,
    val participants: List<String> = emptyList()
)