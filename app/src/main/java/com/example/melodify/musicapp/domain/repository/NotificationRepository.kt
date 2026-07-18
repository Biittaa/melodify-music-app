package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(): List<Notification>
    suspend fun markAsRead(id: String)
    suspend fun clearAll()
}