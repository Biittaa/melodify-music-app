package com.melodify.musicapp.domain.model

data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: Long,
    val isRead: Boolean
)