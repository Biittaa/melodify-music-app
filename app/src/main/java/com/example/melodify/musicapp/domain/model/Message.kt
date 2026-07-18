package com.melodify.musicapp.domain.model

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val songId: String?,
    val createdAt: Long,
    val isSeen: Boolean
)