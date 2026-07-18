package com.melodify.musicapp.di

import android.content.Context
import androidx.room.Room
import com.melodify.musicapp.core.common.Constants
import com.melodify.musicapp.data.local.AppDatabase
import com.melodify.musicapp.data.local.dao.ConversationDao
import com.melodify.musicapp.data.local.dao.DownloadedSongDao
import com.melodify.musicapp.data.local.dao.LikedSongDao
import com.melodify.musicapp.data.local.dao.MessageDao
import com.melodify.musicapp.data.local.dao.PlaylistSongDao
import com.melodify.musicapp.data.local.dao.SearchHistoryDao
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
        ).build()
    }

    /**
     * Provides SearchHistoryDao
     */
    @Provides
    fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()

    /**
     * Provides LikedSongDao
     */
    @Provides
    fun provideLikedSongDao(db: AppDatabase): LikedSongDao = db.likedSongDao()

    /**
     * Provides DownloadedSongDao
     */
    @Provides
    fun provideDownloadedSongDao(db: AppDatabase): DownloadedSongDao = db.downloadedSongDao()

    /**
     * Provides MessageDao
     */
    @Provides
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()

    /**
     * Provides ConversationDao
     */
    @Provides
    fun provideConversationDao(db: AppDatabase): ConversationDao = db.conversationDao()

    /**
     * Provides PlaylistSongDao
     */
    @Provides
    fun providePlaylistSongDao(db: AppDatabase): PlaylistSongDao = db.playlistSongDao()
}