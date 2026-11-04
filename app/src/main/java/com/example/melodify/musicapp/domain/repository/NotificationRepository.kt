package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Notification

/**
 * Repository interface for in-app notifications management
 * Handles retrieval, marking as read, and clearing notifications
 * Note: This is for in-app notifications, not system push notifications
 */
interface NotificationRepository {

    /**
     * Get all notifications for the current user
     * @return List of Notification objects
     */
    suspend fun getNotifications(): List<Notification>

    /**
     * Mark a specific notification as read
     * @param id ID of the notification
     */
    suspend fun markAsRead(id: String)

    /**
     * Clear all notifications
     */
    suspend fun clearAll()
}