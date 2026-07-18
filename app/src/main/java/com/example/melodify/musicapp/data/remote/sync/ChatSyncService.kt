package com.melodify.musicapp.data.remote.sync

import com.melodify.musicapp.data.local.dao.MessageDao
import com.melodify.musicapp.data.remote.firestore.FirestoreDataSource
import com.melodify.musicapp.domain.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatSyncService @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val messageDao: MessageDao
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var currentUserId: String? = null

    fun startListening(userId: String) {
        currentUserId = userId
        // گوش دادن به تمام پیام‌های مربوط به این کاربر
        // می‌توانیم برای هر مکالمه یک listener مجزا داشته باشیم، اما ساده‌تر: همه پیام‌ها را بگیریم و فیلتر کنیم
        serviceScope.launch {
            // برای سادگی، فقط یک listener برای همه پیام‌های ارسال/دریافت
            // اما بهتر است برای هر conversation یک listener مجزا داشته باشیم تا بهینه باشد
            // اینجا پیاده‌سازی ساده: تمام پیام‌های کاربر را از Firestore گرفته و در Room ذخیره می‌کنیم
            firestoreDataSource.observeAllMessages(userId) // فرض کنید چنین متدی وجود دارد
                .collectLatest { messages ->
                    // ذخیره در Room
                    messageDao.insertAll(
                        messages.map { msg ->
                            MessageEntity(
                                id = msg.id,
                                senderId = msg.senderId,
                                receiverId = msg.receiverId,
                                text = msg.text,
                                songId = msg.songId,
                                createdAt = msg.createdAt,
                                isSeen = msg.isSeen,
                                isSent = msg.senderId == userId
                            )
                        }
                    )
                }
        }
    }

    fun stopListening() {
        serviceScope.cancel()
    }
}