package com.melodify.musicapp.di

import android.content.Context
import androidx.room.Room
import com.melodify.musicapp.core.common.Constants
import com.melodify.musicapp.data.local.AppDatabase
import com.melodify.musicapp.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing Room database and its DAOs
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides AppDatabase instance as a singleton
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.ROOM_DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()

    @Provides
    fun providePlaylistDao(db: AppDatabase): PlaylistDao = db.playlistDao()

    @Provides
    fun provideLikedSongDao(db: AppDatabase): LikedSongDao = db.likedSongDao()

    @Provides
    fun provideDownloadedSongDao(db: AppDatabase): DownloadedSongDao = db.downloadedSongDao()

    @Provides
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()

    @Provides
    fun provideConversationDao(db: AppDatabase): ConversationDao = db.conversationDao()

    @Provides
    fun providePlaylistSongDao(db: AppDatabase): PlaylistSongDao = db.playlistSongDao()

    @Provides
    fun provideRecentSongDao(db: AppDatabase): RecentSongDao = db.recentSongDao()
}
