package com.melodify.musicapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.melodify.musicapp.core.common.Constants
import com.melodify.musicapp.data.local.converter.MessageConverters
import com.melodify.musicapp.data.local.dao.*
import com.melodify.musicapp.data.local.entity.*

@Database(
    entities = [
        SearchHistoryEntity::class,
        LikedSongEntity::class,
        DownloadedSongEntity::class,
        MessageEntity::class,
        ConversationEntity::class,
        PlaylistSongEntity::class,
        PlaylistEntity::class,
        RecentSongEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(MessageConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun likedSongDao(): LikedSongDao
    abstract fun downloadedSongDao(): DownloadedSongDao
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
    abstract fun playlistSongDao(): PlaylistSongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun recentSongDao(): RecentSongDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.ROOM_DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
