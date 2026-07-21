package com.melodify.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val userId: String,   // شناسه کاربر مقابل
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int
)