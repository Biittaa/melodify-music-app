package com.melodify.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.melodify.musicapp.data.local.converter.MessageConverters

@Entity(tableName = "messages")
@TypeConverters(MessageConverters::class)
data class MessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val songId: String?,
    val createdAt: Long,
    val isSeen: Boolean,
    val isSent: Boolean,
    val participants: List<String> = emptyList()
)