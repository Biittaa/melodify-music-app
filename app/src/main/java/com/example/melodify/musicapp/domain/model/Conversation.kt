package com.melodify.musicapp.domain.model

data class Conversation(
    val id: String,
    val userId: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int
)