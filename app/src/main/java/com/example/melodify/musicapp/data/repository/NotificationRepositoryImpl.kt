package com.melodify.musicapp.data.repository

import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Notification
import com.melodify.musicapp.domain.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val currentUserProvider: CurrentUserProvider
) : NotificationRepository {

    override suspend fun getNotifications(): List<Notification> {
        // recieve from Firebase
        return emptyList()
    }

    override suspend fun markAsRead(id: String) {
        // update Firestore
    }

    override suspend fun clearAll() {
    }
}